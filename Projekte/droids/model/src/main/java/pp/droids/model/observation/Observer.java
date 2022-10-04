package pp.droids.model.observation;

import pp.droids.model.BoundedItem;
import pp.util.Circle;
import pp.util.map.Observation;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides observation facility to {@linkplain pp.droids.model.BoundedItem} instances.
 */
public class Observer {
    private static final Logger LOGGER = System.getLogger(Observer.class.getName());
    private static final String DUMP_FILE = "dump.json";

    private Observer() { /* don't instantiate */ }

    /**
     * Returns what this item can observe from its current position and looking
     * its way. Only items that a droid etc. can collide with are visible. If the
     * specified contains the category of an item, these items are visible as well.
     * The viewing area is defined by the configuration (specified by
     * {@linkplain pp.droids.model.DroidsConfig#getViewingArea()}). The observation
     * encapsulates a sequence of triangles ({@linkplain pp.util.map.Triangle}).
     * Each triangle represents any other item or any wall, represented by its
     * type defined in {@link pp.droids.model.Category}.
     *
     * @param observer the bounded item used as observer
     * @param visible  indicates segments by their categories which are potentially visible as well.
     * @return the observation of the observer.
     */
    public static Observation getObservation(BoundedItem observer, Set<String> visible) {
        final SegmentCollector collector = new SegmentCollector(observer, visible);
        observer.getLevel().forEach(collector::accept);
        final float viewingArea = observer.getModel().getConfig().getViewingArea();
        try {
            return new Observation(observer, observer.getRotation(), viewingArea,
                                   collector.getSegments(),
                                   translate(collector.getEntityMap(), Circle::new));
        }
        catch (Throwable ex) { // deliberately catch every Throwable here
            LOGGER.log(Level.ERROR, "when updating visibility map", ex); //NON-NLS
            // save the current model so that the error can be reproduced
            saveModel(observer);
            throw ex;
        }
    }

    /**
     * Create a new map from the specified one by translating each value using the specified function.
     */
    private static <A, B, C> Map<A, C> translate(Map<A, B> map, Function<B, C> fun) {
        final Map<A, C> tMap = new HashMap<>();
        map.forEach((a, b) -> tMap.put(a, fun.apply(b)));
        return tMap;
    }

    private static void saveModel(BoundedItem observer) {
        final File file = new File(DUMP_FILE);
        try {
            observer.getModel().saveMap(file);
            LOGGER.log(Level.ERROR, "current model saved in {0}", file.getAbsolutePath()); //NON-NLS
        }
        catch (IOException ioex) {
            LOGGER.log(Level.WARNING, "cannot save {0}", file); //NON-NLS
        }
    }
}

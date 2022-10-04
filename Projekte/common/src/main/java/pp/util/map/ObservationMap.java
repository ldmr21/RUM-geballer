package pp.util.map;

import pp.util.Circle;
import pp.util.IntervalSeq;
import pp.util.TypedSegment;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static pp.util.IntervalSeq.EMPTY;

/**
 * Class for managing a map based on repeated observations using class {@linkplain pp.util.map.Observation}.
 * The map keeps track of all segments that have been observed at least partially so far.
 */
public class ObservationMap {
    private static final Logger LOGGER = System.getLogger(ObservationMap.class.getName());
    private final Map<TypedSegment, ObservedSegment> segMap = new HashMap<>();
    private final Set<String> mapCategories;
    private Set<TypedSegment> segments;
    private final Set<Circle> entities = new HashSet<>();

    /**
     * Creates a new observation map that keeps track of segments of the specified categories.
     *
     * @param mapCategories segments of these categories are managed in this map.
     */
    public ObservationMap(Set<String> mapCategories) {
        this.mapCategories = mapCategories;
    }

    /**
     * Adds the (partial) segment of the specified observation triangle.
     *
     * @param triangle the specified observation triangle
     */
    public void add(Triangle triangle) {
        final TypedSegment seg = triangle.getSegment();
        if (seg != null && mapCategories.contains(seg.cat()))
            if (triangle.getEntity() != null)
                entities.add(triangle.getEntity());
            else {
                final ObservedSegment mapSeg = segMap.get(seg);
                final IntervalSeq prev = mapSeg == null ? EMPTY : mapSeg.intervalSeq();
                final IntervalSeq extended = prev.add(triangle.getInterval());
                if (extended != prev) {
                    segments = null;
                    final ObservedSegment newMapSeg = new ObservedSegment(seg, extended);
                    segMap.put(seg, newMapSeg);
                    LOGGER.log(Level.TRACE, "added segment {0}", newMapSeg); //NON-NLS
                }
            }
    }

    /**
     * Returns the set of circular entities observed so far.
     */
    public Set<Circle> getEntities() {
        return entities;
    }

    /**
     * Returns the set of all (partial) segments observed so far.
     * This method uses {@linkplain #segmentIterator()} for iterating
     * over all partial segments observed so far, but reuses the set
     * computed earlier if it has not changed since then.
     */
    public Set<TypedSegment> getSegments() {
        if (segments == null) {
            LOGGER.log(Level.TRACE, "rebuilding segments set"); //NON-NLS
            segments = new HashSet<>();
            segmentIterator().forEachRemaining(segments::add);
        }
        return segments;
    }

    /**
     * Returns an iterator over all partial segments observed so far.
     */
    public Iterator<TypedSegment> segmentIterator() {
        return new Iterator<>() {
            private final Iterator<ObservedSegment> segIt = segMap.values().iterator();
            private Iterator<TypedSegment> curIt = null;

            @Override
            public boolean hasNext() {
                while ((curIt == null || !curIt.hasNext()) && segIt.hasNext())
                    curIt = segIt.next().iterator();
                return curIt != null && curIt.hasNext();
            }

            @Override
            public TypedSegment next() {
                if (hasNext())
                    return curIt.next();
                throw new NoSuchElementException();
            }
        };
    }
}

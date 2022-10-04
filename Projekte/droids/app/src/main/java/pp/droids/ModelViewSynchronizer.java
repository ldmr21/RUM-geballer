package pp.droids;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import pp.droids.model.Droid;
import pp.droids.model.Item;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This abstract class synchronizes model and view.
 */
public abstract class ModelViewSynchronizer {
    private static final Logger LOGGER = System.getLogger(ModelViewSynchronizer.class.getName());
    protected final GameState gameState;
    private final Node root;
    private final Map<Item, Spatial> itemMap = new HashMap<>();

    /**
     * Saves the game state and the node.
     *
     * @param gameState particular game state
     * @param root      particular node
     */
    protected ModelViewSynchronizer(GameState gameState, Node root) {
        this.gameState = gameState;
        this.root = root;
    }

    protected abstract Spatial translate(Item item);

    /**
     * Synchronizes model and view by iterating over all spatial.
     */
    public void syncWithModel() {
        final Droid droid = gameState.getModel().getDroidsMap().getDroid();
        droid.getLevel().forEach(this::getSpatial);
        final Iterator<Entry<Item, Spatial>> it = itemMap.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<Item, Spatial> entry = it.next();
            if (entry.getKey().isDestroyed() || entry.getKey().getLevel() != droid.getLevel()) {
                if (entry.getValue() != null)
                    entry.getValue().removeFromParent();
                it.remove();
                LOGGER.log(Level.DEBUG, "removed spatial for {0}", entry.getKey()); //NON-NLS
            }
        }
    }

    /**
     * Permits, if an item is destroyed or already part of the map. If it is not, it gets created.
     *
     * @param item the item to be considered
     * @return the spatial of the item
     */
    public Spatial getSpatial(Item item) {
        if (item.isDestroyed()) {
            final Spatial spatial = itemMap.remove(item);
            if (spatial != null)
                spatial.removeFromParent();
            return null;
        }
        if (itemMap.containsKey(item))
            return itemMap.get(item);
        final Spatial spatial = translate(item);
        itemMap.put(item, spatial);
        LOGGER.log(Level.DEBUG, "added spatial for {0}", item); //NON-NLS
        if (spatial != null)
            root.attachChild(spatial);
        return spatial;
    }

    /**
     * Resets it by clearing the map and detach all children of the root.
     */
    public void reset() {
        itemMap.clear();
        root.detachAllChildren();
        syncWithModel();
    }
}

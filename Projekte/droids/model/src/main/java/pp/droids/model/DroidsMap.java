package pp.droids.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the entire game map.
 */
public class DroidsMap {

    /**
     * The droid of this droids map.
     */
    private Droid droid;

    /**
     * A list of all items contained in this droids map.
     */
    private final List<Item> items = new ArrayList<>();

    /**
     * A list of all items to be added to this droids map.
     */
    private final List<Item> addedItems = new ArrayList<>();

    /**
     * The width of the droids map.
     */
    private final int width;

    /**
     * The height of the droids map.
     */
    private final int height;

    /**
     * Creates an empty map of the specified size and with a droid at position (0,0)
     *
     * @param model  the model in which the map is to be created
     * @param width  the width of the map
     * @param height the height of the map
     */
    public DroidsMap(DroidsModel model, int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the list of all items (and all levels) of this map.
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Returns the droid
     *
     * @return droid
     */
    public Droid getDroid() {
        return Objects.requireNonNull(droid, "The droid has not yet been set");
    }

    /**
     * Sets the droid in this map and sets its level to the specified level.
     * This method must not be called twice for the same map.
     *
     * @param droid the droid for this level
     * @param level the level where the droid will be
     */
    public void setDroid(Droid droid, MapLevel level) {
        if (this.droid != null)
            throw new RuntimeException("The droid must not be reset");
        this.droid = droid;
        droid.setLevel(Objects.requireNonNull(level));
        items.add(droid);
    }

    /**
     * Returns the height (i.e., the number of rows) of the map
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the width (i.e., the number of columns) of the map
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Called once per frame. This method calls the update method of each item in this map and removes items that
     * cease to exist.
     *
     * @param deltaTime time in seconds since the last update call
     */
    public void update(float deltaTime) {
        // last update loop or user action may have created new items
        addRegisteredItems();

        // Update the droid even if it has been destroyed and has
        // been removed from the list of items. That way one
        // can still navigate the camera if the droid has been
        // destroyed.
        if (droid != null)
            droid.update(deltaTime);
        // Update all the other items
        for (Item item : items)
            if (item != droid)
                item.update(deltaTime);

        // remove all destroyed items
        items.removeIf(Item::isDestroyed);
    }

    /**
     * Adds all items of the addedItems list.
     */
    public void addRegisteredItems() {
        items.addAll(addedItems);
        addedItems.clear();
    }

    /**
     * Registers the specified item to add it to the map later.
     * The item is not yet added to the list of items.
     * This is in fact done by calling {@linkplain #addRegisteredItems()}.
     *
     * @param item the item to register
     */
    public void register(Item item, MapLevel level) {
        item.setLevel(Objects.requireNonNull(level));
        addedItems.add(item);
    }

    /**
     * Returns the x-coordinate of the left outer wall where the map ends.
     */
    public float getXMin() {
        return -0.5f;
    }

    /**
     * Returns the y-coordinate of the lower outer wall where the map ends.
     */
    public float getYMin() {
        return -0.5f;
    }

    /**
     * Returns the x-coordinate of the right outer wall where the map ends.
     */
    public float getXMax() {
        return width - 0.5f;
    }

    /**
     * Returns the y-coordinate of the upper outer wall where the map ends.
     */
    public float getYMax() {
        return height - 0.5f;
    }
}

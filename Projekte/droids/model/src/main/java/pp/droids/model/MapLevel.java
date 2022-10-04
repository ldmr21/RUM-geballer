package pp.droids.model;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Represents a level of the map and allows to iterate over all
 * items that belong to this map. <b>Note</b> that items belong
 * to this level if {@linkplain Item#getLevel()} returns an
 * object to this MapLevel object. Equality of level names
 * {@linkplain #getName()} does not count.
 */
public class MapLevel implements Iterable<Item> {
    private final DroidsMap map;
    /**
     * The level name
     */
    private final String name;

    /**
     * Creates a new level with the specified name.
     *
     * @param name the level name
     */
    public MapLevel(DroidsMap map, String name) {
        this.map = Objects.requireNonNull(map);
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Returns the name of the level.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the map this level belongs to.
     */
    public DroidsMap getDroidsMap() {
        return map;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns an iterator over all items in this level.
     */
    @Override
    public Iterator<Item> iterator() {
        return new Iterator<>() {
            private final Iterator<Item> it = map.getItems().iterator();
            private Item cur = null;

            @Override
            public boolean hasNext() {
                while (!validItem() && it.hasNext())
                    cur = it.next();
                return validItem();
            }

            private boolean validItem() {
                return cur != null && cur.getLevel() == MapLevel.this;
            }

            @Override
            public Item next() {
                if (hasNext()) {
                    final Item result = cur;
                    cur = null;
                    return result;
                }
                throw new NoSuchElementException();
            }
        };
    }
}

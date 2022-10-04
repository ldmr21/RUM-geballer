package pp.droids.model;

import pp.droids.model.collisions.CollisionPredicate;
import pp.droids.model.collisions.MoveOverlapVisitor;
import pp.droids.model.collisions.OverlapVisitor;
import pp.util.Position;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * An abstract class representing items.
 */
public abstract class Item implements Cloneable {
    /**
     * The logger of an item, primary used for debugging
     */
    protected static final Logger LOGGER = System.getLogger(Item.class.getName());

    /**
     * The model containing this item.
     */
    protected final DroidsModel model;

    /**
     * The level containing this item.
     */
    private MapLevel level;

    /**
     * Indicates whether the object is destroyed.
     */
    private boolean destroyed = false;

    /**
     * Creates a new item.
     *
     * @param model the game model containing said item.
     */
    protected Item(DroidsModel model) {
        this.model = model;
    }

    /**
     * Returns the game model that contains this item.
     *
     * @return the game model
     */
    public DroidsModel getModel() {
        return model;
    }

    /**
     * Returns the category of this item.
     * The default implementation returns {@linkplain pp.droids.model.Category#UNSPECIFIED}.
     *
     * @see pp.droids.model.Category
     */
    public String cat() {
        return Category.UNSPECIFIED;
    }

    /**
     * Returns the level that contains this item.
     */
    public MapLevel getLevel() {
        return level;
    }

    /**
     * Returns the name of the current level.
     *
     * @see #getLevel()
     */
    public String getLevelName() {
        return level.getName();
    }

    /**
     * Sets the level that contains this item.
     */
    public void setLevel(MapLevel level) {
        this.level = Objects.requireNonNull(level);
    }

    /**
     * Indicates that this item has been destroyed.
     */
    public void destroy() {
        LOGGER.log(Level.INFO, "{0} instance destroyed", getClass().getName()); //NON-NLS
        destroyed = true;
    }

    /**
     * Creates and returns a copy of this item by cloning it.
     *
     * @throws RuntimeException if cloning this item fails.
     * @see #clone()
     */
    public Item copy() {
        try {
            return (Item) clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Returns whether this item has been destroyed
     *
     * @return true if the item has been destroyed
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Called once per frame. Used for updating this item's position etc.
     *
     * @param delta time in seconds since the last update call
     */
    public abstract void update(float delta);

    /**
     * Checks whether this item overlaps with the specified one.
     *
     * @param other the other item which is checked for overlap
     * @return true if they overlap.
     */
    public boolean overlapsWith(Item other) {
        return other.accept(accept(OverlapVisitor.INSTANCE));
    }

    /**
     * Checks whether this item overlaps with the specified one
     * when this item is moved from its current position to
     * the specified position on a straight line.
     *
     * @param to    the target position
     * @param other the other item which is checked for overlap
     * @return true if they overlap.
     */
    public boolean overlapsWhenMoving(Position to, Item other) {
        return other.accept(accept(new MoveOverlapVisitor(to)));
    }

    /**
     * Checks whether this item collides with any other item in the same map
     * indicating an invalid position of this item.
     *
     * @return true, if a collision happens
     */
    public boolean collidesWithAnyOtherItem() {
        return overlapsWithAnyOtherItem(CollisionPredicate.INSTANCE);
    }

    /**
     * Checks whether this item overlaps with any other item in the same map
     * and that satisfies the specified predicate. Overlaps may indicate prohibited
     * collisions, which indicate an invalid position of this item, or permitted
     * overlaps with items.
     *
     * @param accept only items accepted by this predicate are considered.
     * @return true, if a collision happens
     */
    public boolean overlapsWithAnyOtherItem(Predicate<Item> accept) {
        for (Item item : level)
            if (accept.test(item) && overlapsWith(item)) return true;
        return false;
    }

    /**
     * This method is called whenever the item is hit. The default implementation does nothing.
     *
     * @param item the item hitting this item
     */
    public void hitBy(Item item) {
        // do nothing
    }

    /**
     * Accept method of the visitor pattern.
     */
    public abstract <T> T accept(Visitor<T> v);

    /**
     * Accept method of the {@link pp.droids.model.VoidVisitor}.
     */
    public abstract void accept(VoidVisitor v);
}

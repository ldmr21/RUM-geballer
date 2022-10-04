package pp.droids.model;

import pp.droids.model.observation.Observer;
import pp.util.CircularEntity;
import pp.util.Position;
import pp.util.map.Observation;

import java.util.Collections;
import java.util.Set;

import static pp.util.Angle.normalizeAngle;

/**
 * Abstract base class of all items with a (roughly) circular shape in a {@linkplain pp.droids.model.DroidsMap}
 */
public abstract class BoundedItem extends Item implements CircularEntity {

    /**
     * The bounding radius of a bounded item.
     */
    private final float boundingRadius;

    /**
     * The x-coordinate of a bounded item.
     */
    private float x;

    /**
     * The y-coordinate of a bounded item.
     */
    private float y;

    /**
     * The rotation of a bounded item.
     */
    private float rotation;

    /**
     * Creates a new item for the specified game model.
     *
     * @param model          the game model whose game map will contain this item.
     * @param boundingRadius the radius of the bounding circle of this item.
     *                       It is used to compute collisions between two items with circular bounds.
     */
    protected BoundedItem(DroidsModel model, float boundingRadius) {
        super(model);
        this.boundingRadius = boundingRadius;
    }

    /**
     * Returns the radius of the item's bounding circle.
     *
     * @return the bounding radius
     */
    public float getRadius() {
        return boundingRadius;
    }

    /**
     * Moves the item to the specified position.
     *
     * @param x x-coordinate of the new position
     * @param y y-coordinate of the new position
     */
    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Moves the item to the specified position.
     * Convenience method for {@linkplain #setPos(float, float)}.
     *
     * @param pos the new position
     */
    public void setPos(Position pos) {
        setPos(pos.getX(), pos.getY());
    }

    /**
     * Returns the x-coordinate of the item's center point.
     *
     * @return x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the item's center point.
     *
     * @return y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the rotation of the item in radians
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the item in radians
     *
     * @param rotation in radians
     */
    public void setRotation(float rotation) {
        this.rotation = normalizeAngle(rotation);
    }

    /**
     * Creates and returns a copy of this item by cloning it. This method in fact
     * calls super.copy(), but casts the result to BoundedItem.
     *
     * @throws RuntimeException if cloning this item fails.
     * @see Item#copy()
     */
    @Override
    public BoundedItem copy() {
        return (BoundedItem) super.copy();
    }

    /**
     * Returns what this item can observe from its current position and looking
     * its way. The viewing area is defined by the configuration (specified by
     * {@linkplain DroidsConfig#getViewingArea()}). The observation
     * encapsulates a sequence of triangles ({@linkplain pp.util.map.Triangle}).
     * Each triangle represents any other item or any wall, represented by its
     * type defined in {@link pp.droids.model.Category}.
     * <p>
     * This is a convenience method calling
     * {@linkplain #getObservation(java.util.Set)}
     * with an empty set, i.e., only those items are
     * potentially visible that a droid etc. can collide with.
     *
     * @return the observation of this item.
     */
    public Observation getObservation() {
        return getObservation(Collections.emptySet());
    }

    /**
     * Returns what this item can observe from its current position and looking
     * its way. Only items that a droid etc. can collide with and additionally
     * those whose category is contained in the specified set are visible.
     * The viewing area is defined by the configuration (specified by
     * {@linkplain DroidsConfig#getViewingArea()}). The observation
     * encapsulates a sequence of triangles ({@linkplain pp.util.map.Triangle}).
     * Each triangle represents any other item or any wall, represented by its
     * type defined in {@link pp.droids.model.Category}.
     *
     * @param visible a set of categories that controls which items shall be considered visible,
     *                additionally to all those items a droid etc. would collide with.
     * @return the observation of this item.
     */
    public Observation getObservation(Set<String> visible) {
        return Observer.getObservation(this, visible);
    }
}
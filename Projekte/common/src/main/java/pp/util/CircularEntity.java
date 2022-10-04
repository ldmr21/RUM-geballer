package pp.util;

public interface CircularEntity extends Position {
    /**
     * Returns the radius of the entity.
     */
    float getRadius();

    /**
     * Returns the rotation of the item in radians. 0 means a direction towards the x-axis.
     */
    float getRotation();

    /**
     * Returns the category of this item.
     */
    String cat();
}

package pp.util;

/**
 * A circle with an orientation. The orientation represents the direction
 * where a circular entity is looking.
 *
 * @param x        x-coordinate of the center
 * @param y        y-coordinate of the center
 * @param r        radius of the circle
 * @param rotation the direction where the circle is "looking" in radians. 0 means towards the x-axis.
 * @param cat      the category of the entity
 */
public record Circle(float x, float y, float r, float rotation, String cat) implements CircularEntity {
    public Circle(CircularEntity c) {
        this(c.getX(), c.getY(),
             c.getRadius(),
             c.getRotation(),
             c.cat());
    }

    @Override
    public float getRadius() {
        return r;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}

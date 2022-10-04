package pp.util;

/**
 * A trivial implementation of points in the plane with float coordinates.
 *
 * @param x x-coordinate
 * @param y y-coordinate
 */
public record FloatPoint(float x, float y) implements Position {
    public static final FloatPoint ZERO = new FloatPoint(0f, 0f);

    /**
     * Create a new FloatPoint object for the given position.
     *
     * @param p a position
     */
    public FloatPoint(Position p) {
        this(p.getX(), p.getY());
    }

    /**
     * Returns the x-coordinate.
     */
    @Override
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate.
     */
    @Override
    public float getY() {
        return y;
    }
}

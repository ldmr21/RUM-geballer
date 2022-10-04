package pp.droids.model;

/**
 * A class representing an obstacle
 */
public class Obstacle extends BoundedItem {

    /**
     * The standard bounding radius of an obstacle.
     */
    public static final float BOUNDING_RADIUS = .45f;

    /**
     * Creates an obstacle
     *
     * @param model  the model
     * @param radius the obstacle's radius
     */
    Obstacle(DroidsModel model, float radius) {
        super(model, radius);
    }

    /**
     * Creates an obstacle with a default radius
     *
     * @param model the model
     */
    public Obstacle(DroidsModel model) {
        this(model, BOUNDING_RADIUS);
    }

    /**
     * Returns the category of an obstacle.
     *
     * @see pp.droids.model.Category
     */
    @Override
    public String cat() {
        return Category.OBSTACLE;
    }

    /**
     * Empty implementation of this method because an obstacle does not move or do anything else.
     */
    @Override
    public void update(float delta) {
        // do nothing
    }

    /**
     * Accept method of the visitor pattern.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    /**
     * Accept method of the {@link pp.droids.model.VoidVisitor}.
     */
    @Override
    public void accept(VoidVisitor v) {
        v.visit(this);
    }
}

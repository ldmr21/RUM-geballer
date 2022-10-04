package pp.droids.model;

public class Exit extends BoundedItem {
    /**
     * The standard bounding radius of exits.
     */
    public static final float BOUNDING_RADIUS = 1f;

    /**
     * Creates an exit
     *
     * @param model  the model
     * @param radius the bounding radius of the exit
     */
    public Exit(DroidsModel model, float radius) {
        super(model, radius);
    }

    /**
     * Creates an exit with standard parameters.
     *
     * @param model the model
     */
    public Exit(DroidsModel model) {
        this(model, BOUNDING_RADIUS);
    }

    @Override
    public String cat() {
        return Category.EXIT;
    }

    /**
     * Empty implementation of this method because touchable items do not move or do anything else.
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

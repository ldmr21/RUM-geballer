package pp.droids.model;

public class Dog extends BoundedItem{

    private static final float BOUNDING_RADIUS = 0.5f;


    public Dog(DroidsModel model) {
        super(model, BOUNDING_RADIUS);
    }

    @Override
    public float getX() {
        return super.getX();
    }

    @Override
    public float getY() {
        return super.getY();
    }

    @Override
    public MapLevel getLevel() {
        // flag follows captor
        return super.getLevel();
    }

    @Override
    public String cat() {
        return Category.DOG;
    }

    @Override
    public void update(float delta) {

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

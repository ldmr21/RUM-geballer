package pp.droids.model;

import pp.util.FloatPoint;
import pp.util.Position;

import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatMath.atan2;

/**
 * Represents a flying rocket, which is destroyed when it reaches its target position.
 */
public class Rocket extends BoundedItem {

    /**
     * The default speed of a rocket.
     */
    private static final float ROCKET_SPEED = 1f;

    /**
     * The bounding radius of a rocket.
     */
    private static final float BOUNDING_RADIUS = 0.75f;

    /**
     * The position of the target of this rocket.
     */
    private Position target;

    /**
     * The specific speed of the rocket.
     */
    private float speed;

    /**
     * Creates a new rocket with the specified starting position.
     * It immediately starts moving.
     *
     * @param model the model the rocket belongs to
     */
    public Rocket(DroidsModel model) {
        super(model, BOUNDING_RADIUS);
        setSpeed(ROCKET_SPEED);
    }

    /**
     * Returns the target position
     */
    public Position getTarget() {
        return target;
    }

    /**
     * Sets the position of the rocket.
     *
     * @param x x-coordinate of the new position
     * @param y y-coordinate of the new position
     */
    @Override
    public void setPos(float x, float y) {
        super.setPos(x, y);
        updateRotation();
    }

    /**
     * Sets the target position
     *
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     */
    public void setTarget(float x, float y) {
        target = new FloatPoint(x, y);
        updateRotation();
    }

    /**
     * Sets the target position
     *
     * @param target a position object of the target position
     */
    public void setTarget(Position target) {
        setTarget(target.getX(), target.getY());
    }

    /**
     * Updates the rotation of the rocket.
     */
    private void updateRotation() {
        if (target != null && distanceTo(target) > ZERO_TOLERANCE) {
            final float dx = target.getX() - getX();
            final float dy = target.getY() - getY();
            setRotation(atan2(dy, dx));
        }
    }

    /**
     * Returns the specific speed.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the specific speed.
     *
     * @param f new speed value
     */
    public void setSpeed(float f) {
        this.speed = f;
    }

    /**
     * This method is called as soon as the rocket reaches its target position.
     * Destroys the rocket.
     */
    protected void targetReached() {
        destroy();
    }

    /**
     * Updates the rocket
     *
     * @param delta time in seconds since the last update call
     */
    @Override
    public void update(float delta) {
        final float distance = distanceTo(target);
        final float step = getSpeed() * delta;

        if (distance <= step || distance < ZERO_TOLERANCE) {
            // if near target jump to target
            setPos(target);
            targetReached();
            setSpeed(0f);
        }
        else if (speed != 0f) {
            final float mu = step / distance;
            setPos(getX() * (1f - mu) + target.getX() * mu,
                   getY() * (1f - mu) + target.getY() * mu);
        }
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

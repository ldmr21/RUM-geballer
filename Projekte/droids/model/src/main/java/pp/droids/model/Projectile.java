package pp.droids.model;

import pp.droids.model.collisions.CollisionPredicate;
import pp.util.FloatPoint;
import pp.util.Position;

import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * A class representing projectiles.
 */
public class Projectile extends BoundedItem {

    /**
     * The specific speed of a projectile.
     */
    private float speed;

    /**
     * The default lifetime of a projectile.
     */
    private float lifeTime = Float.POSITIVE_INFINITY;

    /**
     * Creates a projectile
     *
     * @param model          the game model
     * @param boundingRadius the size of this projectile in terms of the radius of its bounding circle
     */
    public Projectile(DroidsModel model, float boundingRadius) {
        super(model, boundingRadius);
    }

    /**
     * Sets the specific speed.
     *
     * @param speed new speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Returns the specific lifetime of the projectile.
     */
    public float getLifeTime() {
        return lifeTime;
    }

    /**
     * Sets the specific lifetime.
     *
     * @param lifeTime new lifetime
     */
    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }

    /**
     * Updates the projectile.
     *
     * @param delta delta
     */
    @Override
    public void update(float delta) {
        lifeTime -= delta;
        if (lifeTime <= 0f)
            destroy();
        else {
            final float distance = speed * delta;
            final Position to = new FloatPoint(getX() + distance * cos(getRotation()),
                                               getY() + distance * sin(getRotation()));
            processHits(to);
            setPos(to);
        }
    }

    /**
     * Checks if the projectile hits an obstacle or an enemy. Projectiles are destroyed that way.
     *
     * @param to the position where this projectile goes in this time step
     */
    public void processHits(Position to) {
        for (Item item : getLevel())
            if (CollisionPredicate.INSTANCE.test(item) && this.overlapsWhenMoving(to, item)) {
                item.hitBy(this);
                destroy();
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

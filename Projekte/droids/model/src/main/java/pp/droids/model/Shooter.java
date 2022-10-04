package pp.droids.model;

import pp.droids.notifications.ShooterFiredEvent;

import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * Abstract base class for items that can shoot and that may be hit by projectiles.
 */
public abstract class Shooter extends FlagCaptor {

    /**
     * The bounding radius of a projectile.
     */
    public static final float PROJECTILE_BOUNDING_RADIUS = .05f;

    /**
     * The time a projectile will live.
     */
    public static final float PROJECTILE_LIFE_TIME = 10f;

    /**
     * The distance how far away projectiles will start.
     */
    public static final float PROJECTILE_START_DIST = 1f;

    /**
     * The speed of a projectile.
     */
    public static final float PROJECTILE_SPEED = 10f;

    /**
     * The default reload time.
     */
    public static final float STANDARD_RELOAD_TIME = 1f;

    /**
     * The remaining time until reloading is finished.
     */
    private float remainingReloadTime;

    /**
     * The specific reload time.
     */
    private final float reloadTime;

    /**
     * Creates a new shooting item.
     *
     * @param model          the game model
     * @param boundingRadius the size of this item in terms of the radius of its bounding circle
     * @param initialLives   the number of lives that this item initially has
     * @param reloadTime     the time in seconds that must pass before this item can shoot again
     */
    protected Shooter(DroidsModel model, float boundingRadius, int initialLives, float reloadTime) {
        super(model, boundingRadius, initialLives);
        this.reloadTime = reloadTime;
    }

    /**
     * Returns the specific reload time.
     */
    public float getReloadTime() {
        return reloadTime;
    }

    /**
     * Creates a projectile. This method is called whenever this shooter fires.
     *
     * @return the new projectile
     */
    public Projectile makeProjectile() {
        final Projectile projectile = new Projectile(model, PROJECTILE_BOUNDING_RADIUS);
        projectile.setPos(getX() + PROJECTILE_START_DIST * cos(getRotation()),
                          getY() + PROJECTILE_START_DIST * sin(getRotation()));
        projectile.setSpeed(PROJECTILE_SPEED);
        projectile.setRotation(getRotation());
        projectile.setLifeTime(PROJECTILE_LIFE_TIME);
        getModel().notifyListeners(new ShooterFiredEvent(this, projectile));
        return projectile;
    }

    /**
     * Lets the item fire a projectile if it is not reloading. The projectile is
     * created by calling method {@linkplain Shooter#makeProjectile()}
     */
    public void fire() {
        if (!isReloading()) {
            startReloading();
            getModel().getDroidsMap().register(makeProjectile(), getLevel());
        }
    }

    /**
     * Starts the reloading process.
     */
    private void startReloading() {
        remainingReloadTime = reloadTime;
    }

    /**
     * Returns whether this item is still reloading.
     *
     * @return true, if still reloading
     */
    public boolean isReloading() {
        return remainingReloadTime > 0f;
    }

    /**
     * Updates the item
     *
     * @param delta time in seconds since the last update call
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (isReloading())
            remainingReloadTime -= delta;
    }
}

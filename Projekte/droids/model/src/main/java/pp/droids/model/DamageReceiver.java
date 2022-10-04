package pp.droids.model;

import pp.droids.notifications.HitEvent;

/**
 * Abstract base class of all items that live initially and that are destroyed if
 * they are hit a certain number of times.
 *
 * @see #hitBy(Item)
 */
public abstract class DamageReceiver extends BoundedItem {

    /**
     * The number of lives of the damage receiver.
     */
    private int lives;

    /**
     * The time passed since the damage receiver was hit the last time.
     */
    private float timeSinceLastHit = -1f;

    /**
     * Creates an enemy
     *
     * @param model          the game model
     * @param boundingRadius the size of this item in terms of the radius of its bounding circle
     * @param initialLives   the number of lives that this item initially has
     */
    protected DamageReceiver(DroidsModel model, float boundingRadius, int initialLives) {
        super(model, boundingRadius);
        lives = initialLives;
    }

    /**
     * Returns the number of lives this item still has.
     *
     * @return remaining number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Returns the time in seconds since this Item has been hit the last time,
     * or a negative value if it has never been hit.
     */
    public float getTimeSinceLastHit() {
        return timeSinceLastHit;
    }

    /**
     * This method is called whenever the item is hit. This  method reduces the number of lives and
     * destroys it (by calling {@linkplain BoundedItem#destroy()}) if there are no lives left.
     *
     * @param item the item hitting this item
     */
    @Override
    public void hitBy(Item item) {
        timeSinceLastHit = 0f;
        if (--lives > 0)
            getModel().notifyListeners(new HitEvent(this, item));
        else
            destroy();
    }

    /**
     * Updates the item
     *
     * @param delta time in seconds since the last update call
     */
    @Override
    public void update(float delta) {
        if (timeSinceLastHit >= 0)
            timeSinceLastHit += delta;
    }
}

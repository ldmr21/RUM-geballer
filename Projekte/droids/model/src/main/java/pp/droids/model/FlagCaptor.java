package pp.droids.model;

/**
 * Interface for any Item that can capture the flag
 */
public abstract class FlagCaptor extends DamageReceiver {
    /**
     * The flag captured by the enemy. Null if the enemy doesn't have a flag.
     */
    private Flag capturedFlag;

    protected FlagCaptor(DroidsModel model, float boundingRadius, int initialLives) {
        super(model, boundingRadius, initialLives);
    }

    /**
     * Returns the captured flag, or null if no flag has been captured.
     */
    public Flag getCapturedFlag() {
        return capturedFlag;
    }

    /**
     * Sets the captured flag or null if the captor doesn't have the flag (any longer).
     *
     * @param flag the flag; null means that the flag captor doesn't have the flag.
     */
    public void setCapturedFlag(Flag flag) {
        if (capturedFlag != flag) {
            if (capturedFlag != null)
                capturedFlag.releasedBy(this);
            capturedFlag = isDestroyed() ? null : flag;
            if (capturedFlag != null)
                capturedFlag.capturedBy(this);
        }
    }

    /**
     * Returns whether the NPC is currently carrying the flag
     */
    public boolean hasFlag() {
        return getCapturedFlag() != null;
    }

    @Override
    public void destroy() {
        setCapturedFlag(null);
        super.destroy();
    }

    /**
     * Updates the flag captor
     *
     * @param delta time in seconds since the last update call
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        processFlagCapturing();
    }

    /**
     * Checks whether the item captured the flag and whether the item reached the exit.
     */
    private void processFlagCapturing() {
        if (isDestroyed()) return;
        for (Item it : getLevel()) {
            if (it instanceof Flag flag && flag.getCaptor() == null && distanceTo(flag) <= getRadius() && getCapturedFlag() == null)
                setCapturedFlag(flag);
            if (it instanceof Exit exit && distanceTo(exit) <= exit.getRadius())
                getModel().reachedExit(this);
        }
    }
}

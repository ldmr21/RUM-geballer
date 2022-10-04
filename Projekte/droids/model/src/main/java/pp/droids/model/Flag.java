package pp.droids.model;

import java.lang.System.Logger.Level;

public class Flag extends BoundedItem {
    private static final float BOUNDING_RADIUS = 0.45f;

    private FlagCaptor captor;

    public Flag(DroidsModel model) {
        super(model, BOUNDING_RADIUS);
    }

    @Override
    public float getX() {
        // flag follows captor
        return captor == null ? super.getX() : captor.getX();
    }

    @Override
    public float getY() {
        // flag follows captor
        return captor == null ? super.getY() : captor.getY();
    }

    @Override
    public MapLevel getLevel() {
        // flag follows captor
        return captor == null ? super.getLevel() : captor.getLevel();
    }

    @Override
    public String cat() {
        return Category.FLAG;
    }

    /**
     * Returns the item that captured the flag, or null if this flag is not captured.
     */
    public FlagCaptor getCaptor() {
        return captor;
    }

    /**
     * Called by the specified FlagCaptor when it releases this flag.
     *
     * @param releaser the flag captor that holds this flag.
     * @throws RuntimeException if this flag is not held by the specified FlagCaptor
     */
    void releasedBy(FlagCaptor releaser) {
        if (captor != releaser)
            throw new RuntimeException(releaser + " hasn't captured the flag");
        captor = null;
        setPos(releaser);
        setLevel(releaser.getLevel());
    }

    /**
     * Called by the specified FlagCaptor when it captures the flag.
     *
     * @param captor the flag captor that has just captured the flag
     * @throws RuntimeException if this flag is already held by a captor.
     */
    void capturedBy(FlagCaptor captor) {
        if (this.captor != null)
            throw new RuntimeException("cannot capture a flag if it is already captured");
        LOGGER.log(Level.INFO, "{0} captured the flag", captor); //NON-NLS
        this.captor = captor;
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

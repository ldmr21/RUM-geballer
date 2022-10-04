package pp.droids.model;

import pp.droids.notifications.EnemyDestroyedEvent;
import pp.npc.NonPlayerCharacter;
import pp.npc.NonPlayerCharacterBehavior;
import pp.util.Segment;
import pp.util.navigation.Navigator;

import java.util.concurrent.ExecutorService;

/**
 * Represents an enemy
 */
public class Enemy extends Shooter implements NonPlayerCharacter {
    /**
     * The turn speed of the enemy.
     */
    private static final float TURN_SPEED = 0f;

    /**
     * The forward speed of the enemy.
     */
    private static final float FORWARD_SPEED = 2f;

    /**
     * The standard bounding radius of an enemy.
     */
    public static final float BOUNDING_RADIUS = .45f;

    /**
     * The number of lives of this enemy.
     */
    private static final int LIVES = 4;

    /**
     * The npc behavior of this enemy.
     */
    private final NonPlayerCharacterBehavior behavior = new NonPlayerCharacterBehavior(this);

    /**
     * Creates an enemy
     *
     * @param model          the game model
     * @param boundingRadius the size of this item in terms of the radius of its bounding circle
     * @param initialLives   the number of lives that this item initially has
     * @param reloadTime     the time in seconds that must pass before this item can shoot again
     */
    public Enemy(DroidsModel model, float boundingRadius, int initialLives, float reloadTime) {
        super(model, boundingRadius, initialLives, reloadTime);
    }

    /**
     * Creates an enemy with standard parameters.
     *
     * @param model the model
     */
    public Enemy(DroidsModel model) {
        this(model, BOUNDING_RADIUS, LIVES, STANDARD_RELOAD_TIME);
    }

    /**
     * Returns the category of an enemy.
     *
     * @see pp.droids.model.Category
     */
    @Override
    public String cat() {
        return Category.CHARACTER;
    }

    /**
     * Returns the number of collected coins of the NPC.
     */
    @Override
    public int getCoins() {
        //TODO implement
        throw new RuntimeException("Not yet implemented");
    }

    /**
     * Indicates that this enemy has been destroyed.
     */
    @Override
    public void destroy() {
        getModel().notifyListeners(new EnemyDestroyedEvent(this));
        super.destroy();
    }

    /**
     * Updates the enemy based on the npc-behavior.
     *
     * @param delta time in seconds since the last update call
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        behavior.update(delta);
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

    @Override
    public ExecutorService getExecutor() {
        return getModel().getExecutor();
    }

    public NonPlayerCharacterBehavior getBehavior() {
        return behavior;
    }

    /**
     * Returns a navigator that can be used to compute an optimal path for this
     * item to any postion.
     *
     * @return a new navigator
     */
    public Navigator<Segment> getNavigator() {
        return new DroidsNavigator(this, FORWARD_SPEED, TURN_SPEED);
    }
}
package pp.droids.model;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static pp.util.Angle.normalizeAngle;
import static pp.util.FloatMath.TWO_PI;

public class AssignmentTestBase {
    static final Logger LOGGER = System.getLogger(AssignmentTestBase.class.getName());
    static final float DELTA = 0.1f;
    static final int MAX_STEPS = 100000;
    static final float MAX_SPEED = 2.5f;
    static final float MAX_TURN_SPEED = 1.5f;
    static final int MAX_INACTIVITY = 100;

    DroidsModel gameModel;
    private Droid droid;
    private Enemy enemy;
    private boolean first = true;
    private float prevX;
    private float prevY;
    private float prevAngle;
    private int inactive;

    @Before
    public void setUp() {
        // create a game model whose executor service has only a single thread,
        // i.e., all tasks are guaranteed to run sequentially.
        gameModel = new DroidsModel() {
            private ExecutorService service;

            @Override
            public ExecutorService getExecutor() {
                if (service == null) service = Executors.newSingleThreadExecutor();
                return service;
            }
        };
    }

    @After
    public void tearDown() {
        gameModel.shutdown();
    }

    void loadMap(String name) throws IOException {
        final InputStream stream = getClass().getResourceAsStream(name);
        if (stream == null)
            throw new IOException("Cannot find " + name);
        gameModel.loadMap(stream);
    }

    /**
     * Runs a test case where the enemy must destroy the droid, but there is no flag in the game.
     */
    void runDestroyTest() throws InterruptedException, ExecutionException {
        init();

        // loop until droid was destroyed or max time has elapsed
        for (int i = 0; i < MAX_STEPS; i++) {
            step(i);
            if (droid.isDestroyed()) {
                LOGGER.log(Level.INFO, "Enemy destroyed Droid after {0} steps", i); //NON-NLS
                return;
            }
        }
        fail("Enemy didn't destroy droid in time");
    }

    /**
     * Runs a test case where the enemy must capture the flag and carry it to the exit.
     */
    void runCaptureFlagTest() throws InterruptedException, ExecutionException {
        // Check, if map was loaded successfully
        init();

        // loop until enemy has carried the flag to the exit or max time has elapsed
        for (int i = 0; i < MAX_STEPS; i++) {
            step(i);
            if (gameModel.isGameLost()) {
                LOGGER.log(Level.INFO, "Enemy captured the flag and carried to the exit after {0} steps", i); //NON-NLS
                assertTrue(droid.isDestroyed());
                return;
            }
        }
        fail("Enemy didn't succeed");
    }

    private void init() {
        // Check, if map was loaded successfully
        assertNotNull(gameModel.getDroidsMap());

        // remember the current droid and enemy
        droid = gameModel.getDroidsMap().getDroid();
        enemy = gameModel.getDroidsMap().getItems().stream()
                         .filter(Enemy.class::isInstance)
                         .map(Enemy.class::cast)
                         .findFirst()
                         .orElseThrow();
    }

    /**
     * Performs one update cycle of the game.
     *
     * @param round the round number
     */
    private void step(int round) throws InterruptedException, ExecutionException {
        gameModel.update(DELTA);
        checkMovement(DELTA, round);
        // wait for completion of any future that might have been created in the previous update call
        gameModel.getExecutor().submit(() -> null).get();
        assertFalse("Enemy was destroyed", enemy.isDestroyed()); //NON-NLS
    }

    /**
     * Check for long inactivity of the enemy and whether it tries to move faster than allowed.
     *
     * @param delta the time since the last round
     * @param round the round number
     */
    private void checkMovement(float delta, int round) {
        if (first)
            first = false;
        else if (prevX == enemy.getX() && prevY == enemy.getY() && prevAngle == enemy.getRotation()) {
            inactive++;
            assertTrue("enemy was inactive for " + inactive + " rounds after " + (round - inactive) + " rounds", inactive < MAX_INACTIVITY); //NON-NLS
        }
        else {
            inactive = 0;
            // check that the NPC doesn't move faster than allowed.
            assertTrue("enemy moved too fast", enemy.distanceTo(prevX, prevY) < delta * MAX_SPEED);  //NON-NLS
            float maxAngle = normalizeAngle(prevAngle + delta * MAX_TURN_SPEED);
            float minAngle = normalizeAngle(prevAngle - delta * MAX_TURN_SPEED);
            if (maxAngle < minAngle) maxAngle += TWO_PI;
            float curAngle = normalizeAngle(enemy.getRotation());
            if (curAngle < minAngle) curAngle += TWO_PI;
            assertTrue("enemy turned too fast", curAngle < maxAngle);  //NON-NLS
        }
        prevX = enemy.getX();
        prevY = enemy.getY();
        prevAngle = enemy.getRotation();
    }
}

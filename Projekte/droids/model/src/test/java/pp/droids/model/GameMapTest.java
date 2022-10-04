package pp.droids.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static pp.droids.model.GamePlayTest.enemy;
import static pp.droids.model.GamePlayTest.getEnemies;
import static pp.droids.model.GamePlayTest.getObstacles;
import static pp.droids.model.GamePlayTest.getProjectiles;
import static pp.droids.model.GamePlayTest.getRockets;
import static pp.droids.model.GamePlayTest.obstacle;
import static pp.droids.model.GamePlayTest.rocket;

public class GameMapTest {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 25;

    private DroidsModel gameModel;
    private DroidsMap droidsMap;

    @Before
    public void setUp() {
        gameModel = new DroidsModel();
        droidsMap = new DroidsMap(gameModel, WIDTH, HEIGHT);
        final Droid droid = new Droid(gameModel);
        final MapLevel level = new MapLevel(droidsMap, "Level"); //NON-NLS
        droidsMap.setDroid(droid, level);
        gameModel.setDroidsMap(droidsMap);
    }

    @After
    public void tearDown() {
        gameModel.shutdown();
    }

    // Check size
    @Test
    public void mapDimensionTest() {
        assertEquals(WIDTH, gameModel.getDroidsMap().getWidth());
        assertEquals(HEIGHT, gameModel.getDroidsMap().getHeight());
    }

    // Check if Droid is in game
    @Test
    public void droidExist() {
        assertNotNull(getDroid());
    }

    private Droid getDroid() {
        return gameModel.getDroidsMap().getDroid();
    }

    @Test
    public void enemyExist() {
        final Enemy enemy = enemy(gameModel, 2, 2);
        droidsMap.register(enemy, getDroid().getLevel());
        gameModel.update(1);
        assertEquals(1, getEnemies(gameModel).size());
        assertSame(enemy, getEnemies(gameModel).get(0));
    }

    @Test
    public void projectileExist() {
        // wait until droid can fire
        gameModel.update(0.4f);
        assertTrue(getProjectiles(gameModel).isEmpty());
        getDroid().fire();
        gameModel.update(0.1f);
        assertEquals(1, getProjectiles(gameModel).size());
        // droid is still reloading => next try to shoot is ineffective
        getDroid().fire();
        gameModel.update(0.1f);
        assertEquals(1, getProjectiles(gameModel).size());
        gameModel.update(10);
        // projectile has left the game map
        assertTrue(getProjectiles(gameModel).isEmpty());
    }

    @Test
    public void obstacleExist() {
        final Obstacle obstacle = obstacle(gameModel, 6, 6);
        droidsMap.register(obstacle, getDroid().getLevel());
        gameModel.update(0.1f);
        assertEquals(1, getObstacles(gameModel).size());
        assertSame(obstacle, getObstacles(gameModel).get(0));
    }

    @Test
    public void rocketExist() {
        final Rocket rocket = rocket(gameModel, 4, 7);
        rocket.setTarget(2f, 2f);
        droidsMap.register(rocket, getDroid().getLevel());
        gameModel.update(0.1f);
        assertEquals(1, getRockets(gameModel).size());
        assertSame(rocket, getRockets(gameModel).get(0));
    }
}

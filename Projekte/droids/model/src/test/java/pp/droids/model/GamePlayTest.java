package pp.droids.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pp.util.Position;
import pp.util.Segment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatPoint.ZERO;

public class GamePlayTest {
    private static final float EPS = 0.00001f;

    private DroidsModel gameModel;
    private DroidsMap map;
    private Droid droid;
    private Enemy enemy;
    private Flag flag;
    private Exit exit;

    @Before
    public void setUp() {
        gameModel = new DroidsModel();
        map = gameModel.getDroidsMap();
        final MapLevel level = new MapLevel(map, "Level"); //NON-NLS
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int dx = width / 2;
        final int dh = height / 2;
        droid = new Droid(gameModel);
        droid.setPos(dx, dh);
        enemy = enemy(gameModel, 0f, 0f);
        flag = flag(gameModel, dx, height - 1);
        exit = exit(gameModel, width - 1, dx);
        map.setDroid(droid, level);
        map.register(enemy, level);
        map.register(enemy(gameModel, width - 1, height - 1), level);
        map.register(obstacle(gameModel, dx, 0f), level);
        map.register(flag, level);
        map.register(exit, level);
        map.addRegisteredItems();
    }

    @After
    public void tearDown() {
        gameModel.shutdown();
    }

    /**
     * Check that enemy is destroyed after four hits
     */
    @Test
    public void enemyDies() {
        // enemy should be alive
        assertFalse(enemy.isDestroyed());
        // first hit
        hit(enemy);
        // enemy should be alive
        assertFalse(enemy.isDestroyed());
        // three more hits
        hit(enemy);
        hit(enemy);
        hit(enemy);
        // enemy should be dead
        assertTrue(enemy.isDestroyed());
    }

    /**
     * Check that droid is destroyed after 40 hits
     */
    @Test
    public void droidDies() {
        // droid should be alive
        assertFalse(droid.isDestroyed());

        // 39 hits for droid
        for (int i = 0; i < 39; i++)
            hit(droid);

        // droid should be alive with only 1 live
        assertFalse(droid.isDestroyed());
        assertEquals(1, droid.getLives());

        // next hit -> dead
        hit(droid);

        // check, if droid is destroyed and not more visible
        assertTrue(droid.isDestroyed());
    }

    /**
     * Check that droid starts in the center of the map
     */
    @Test
    public void playerStartPosition() {
        // calculate start position
        int sx = gameModel.getDroidsMap().getWidth() / 2;
        int sy = gameModel.getDroidsMap().getHeight() / 2;

        // Check, if droid on the right position
        assertPositionEquals(sx, sy, gameModel.getDroidsMap().getDroid(), EPS);
    }

    /**
     * Check that droid cannot move to obstacle position
     */
    @Test
    public void droidMoveToObstacle() {
        final float startX = droid.getX();
        final float startY = droid.getY();

        // try to navigate to obstacle position
        final Obstacle obstacle = getObstacles(gameModel).get(0);
        navigateTo(obstacle);
        droid.update(2);
        // position of droid should not be changed
        assertEquals(startX, droid.getX(), ZERO_TOLERANCE);
        assertEquals(startY, droid.getY(), ZERO_TOLERANCE);
    }

    /**
     * Check that droid can only move to enemy position if enemy has been destroyed
     */
    @Test
    public void droidMoveToEnemy() {
        final float startX = droid.getX();
        final float startY = droid.getY();

        // navigate to enemy position
        navigateTo(enemy); // should not succeed
        droid.update(2);

        // position of droid should not be changed
        assertEquals(startX, droid.getX(), ZERO_TOLERANCE);
        assertEquals(startY, droid.getY(), ZERO_TOLERANCE);

        // destroy enemy with hits
        for (int i = 0; i < 4; i++)
            hit(enemy);

        // enemy should be destroyed
        assertTrue(enemy.isDestroyed());

        // navigate to enemy position
        gameModel.update(3);
        navigateTo(enemy);
        droid.update(10f);

        // position of droid should be changed
        assertPositionEquals(enemy, droid, EPS);
    }

    /**
     * Check that projectile hits the enemy
     */
    @Test
    public void droidProjectileTest() {
        droid.setRotation(-2.466851711f);
        final Projectile projectile = droid.makeProjectile();

        // add projectile to game
        gameModel.update(3);
        map.register(projectile, map.getDroid().getLevel());
        map.addRegisteredItems();

        assertEquals(4, enemy.getLives());

        // loop until enemy was damaged or max time has elapsed
        for (int i = 0; i < 10000 && enemy.getLives() == 4; i++)
            gameModel.update(0.05f);

        // check that enemy has been hit once
        assertEquals(3, enemy.getLives());
    }

    /**
     * Check that game is won after the droid captured the flag and carried it to the exit
     */
    @Test
    public void gameWonTest() {
        assertFalse(gameModel.isGameWon());
        final List<Segment> path1 = droid.getNavigator().findPathTo(flag);
        assertFalse(path1.isEmpty());
        droid.setPath(path1);
        for (int i = 0; i < 1000; i++)
            gameModel.update(0.02f);
        assertEquals(flag, droid.getCapturedFlag());
        final List<Segment> path2 = droid.getNavigator().findPathTo(exit);
        assertFalse(path2.isEmpty());
        droid.setPath(path2);
        for (int i = 0; i < 1000; i++)
            gameModel.update(0.02f);
        assertTrue(gameModel.isGameWon());
        assertFalse(gameModel.isGameLost());
    }

    /**
     * Check that game is lost after one enemy captured the flag and carried it to the exit
     */
    @Test
    public void gameLostTest() {
        assertFalse(gameModel.isGameWon());
        enemy.setPos(flag);
        gameModel.update(0.02f);
        assertEquals(flag, enemy.getCapturedFlag());
        enemy.setPos(exit);
        gameModel.update(0.02f);
        assertFalse(gameModel.isGameWon());
        assertTrue(gameModel.isGameLost());
    }

    static void assertPositionEquals(BoundedItem expected, BoundedItem actual, float eps) {
        assertPositionEquals(expected.getX(), expected.getY(), actual, eps);
    }

    static void assertPositionEquals(Position expected, BoundedItem actual, float eps) {
        if (expected.distanceSquaredTo(actual.getX(), actual.getY()) > eps)
            fail(formatValues(expected.getX(), expected.getY(), actual.getX(), actual.getY()));
    }

    static void assertPositionEquals(float expectedX, float expectedY, BoundedItem actual, float eps) {
        if (ZERO.distanceSquaredTo(actual.getX() - expectedX, actual.getY() - expectedY) > eps)
            fail(formatValues(expectedX, expectedY, actual.getX(), actual.getY()));
    }

    static String formatValues(float expectedX, float expectedY, float actualX, float actualY) {
        return String.format("expected:(%f, %f) but was: (%f, %f))", expectedX, expectedY, actualX, actualY); //NON-NLS
    }

    static Enemy enemy(DroidsModel gameModel, float x, float y) {
        final Enemy enemy = new Enemy(gameModel);
        enemy.setPos(x, y);
        return enemy;
    }

    static Obstacle obstacle(DroidsModel gameModel, float x, float y) {
        final Obstacle obstacle = new Obstacle(gameModel);
        obstacle.setPos(x, y);
        return obstacle;
    }

    static Flag flag(DroidsModel gameModel, float x, float y) {
        final Flag flag = new Flag(gameModel);
        flag.setPos(x, y);
        return flag;
    }

    static Exit exit(DroidsModel gameModel, float x, float y) {
        final Exit exit = new Exit(gameModel);
        exit.setPos(x, y);
        return exit;
    }

    static Rocket rocket(DroidsModel gameModel, float x, float y) {
        final Rocket rocket = new Rocket(gameModel);
        rocket.setPos(x, y);
        return rocket;
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> getItems(Class<T> clazz, DroidsModel model) {
        final List<Object> result = new ArrayList<>();
        for (Item item : model.getDroidsMap().getDroid().getLevel())
            if (clazz.isInstance(item))
                result.add(item);
        return (List<T>) result;
    }

    static List<Enemy> getEnemies(DroidsModel gameModel) {
        return getItems(Enemy.class, gameModel);
    }

    static List<Obstacle> getObstacles(DroidsModel gameModel) {
        return getItems(Obstacle.class, gameModel);
    }

    static List<Rocket> getRockets(DroidsModel gameModel) {
        return getItems(Rocket.class, gameModel);
    }

    static List<Projectile> getProjectiles(DroidsModel gameModel) {
        return getItems(Projectile.class, gameModel);
    }

    static void hit(DamageReceiver item) {
        final Droid droid = item.getModel().getDroidsMap().getDroid();
        final Projectile projectile = droid.makeProjectile();
        item.hitBy(projectile);
    }

    private void navigateTo(Position pos) {
        final Droid droid = gameModel.getDroidsMap().getDroid();
        droid.setPath(droid.getNavigator().findPathTo(pos));
    }
}

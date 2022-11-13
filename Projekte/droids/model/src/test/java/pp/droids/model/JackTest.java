package pp.droids.model;

import pp.util.FloatPoint;
import pp.util.Position;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static pp.droids.model.GamePlayTest.enemy;
import static pp.droids.model.GamePlayTest.obstacle;
import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatPoint.ZERO;
//import pp.droids.model.collisions.collisions.*;

public class JackTest {

    private static final float EPS = 0.00001f;

    private DroidsModel gameModel;
    private DroidsMap map;
    private Droid Jack;
    private Enemy enemy;
    private Flag flag;
    private Exit exit;
    private Obstacle obstacle;

    //private Projectile projectile;
    private float updateTime = 1;

    @Before
    public void setUp() {
        gameModel = new DroidsModel();
        map = gameModel.getDroidsMap();
        final MapLevel level = new MapLevel(map, "Level"); //NON-NLS
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int dx = width / 2;
        final int dh = height / 2;
        Jack = new Droid(gameModel);
        Jack.setPos(dx, dh);
        enemy = enemy(gameModel, 0f, 0f);
        enemy.setPos(dx, dh);
        enemy = new Enemy(gameModel);
        flag = flag(gameModel, dx, height - 1);
        exit = exit(gameModel, width - 1, dx);
        obstacle = obstacle(gameModel, 0f, 0f);
        obstacle.setPos(dx, dh);
        obstacle = new Obstacle(gameModel);
        map.setDroid(Jack, level);
        map.register(enemy, level);
        map.register(enemy(gameModel, width - 1, height - 1), level);
        map.register(obstacle(gameModel, dx, 0f), level);
        map.register(flag, level);
        map.register(exit, level);
        map.addRegisteredItems();
    }

    @Test
    public void playerStartPosition() {
        // calculate start position
        int sx = gameModel.getDroidsMap().getWidth() / 2;
        int sy = gameModel.getDroidsMap().getHeight() / 2;

        // Check, if droid on the right position
        assertPositionEquals(sx, sy, gameModel.getDroidsMap().getDroid(), EPS);
    }


    @Test //T001
    public void testBewegenVorne(){
        Position start = new FloatPoint(Jack.getX(), Jack.getY());
        navigateTo(new FloatPoint(0,0));
        Jack.goForward();
        Jack.update(updateTime);
        //gameModel.update(updateTime);
        assertEquals((map.getWidth()/2)+(4*updateTime), Jack.getX(), 3);
    }

    @Test //T002
    public void testBewegenHinten(){
        Position start = new FloatPoint(Jack.getX(), Jack.getY());
        navigateTo(new FloatPoint(0,0));
        Jack.goBackward();
        Jack.update(updateTime);
        //gameModel.update(updateTime);
        assertEquals((map.getWidth()/2)-(4*updateTime), Jack.getX(), 3);
    }
    @Test // T009
    public void testFernkampf(){
        Position start = new FloatPoint(Jack.getX(), Jack.getY());
        enemy.setPos(start.getX()+1,start.getY());
        //if( munition < 0) {
        for(int i = 0; i < 4; i++)
        {
        Jack.fire();
        Jack.update(0.1f);
        map.addRegisteredItems();
        map.update(0.1f);
        gameModel.update(0.1f);
        //assertTrue(Jack.isReloading());
        }
        assertTrue(enemy.isDestroyed());


        //}
    }
    @Test //T010 unfinished
    public void testKollisionJack(){
        Position start = new FloatPoint(Jack.getX(), Jack.getY());
        enemy.setPos(start.getX()+1, start.getY());
        Jack.goForward();
        gameModel.update(updateTime);
        map.update(updateTime);
        assertFalse(Jack.collidesWithAnyOtherItem());
        assertEquals(0.1f,Jack.getX(), EPS);
        //Jack.goBackward();
        //Jack.update(updateTime);
        // assertEquals(0.1f,Jack.getX(), EPS);
    }

    static void assertPositionEquals(BoundedItem expected, BoundedItem actual, float eps) {
        assertPositionEquals(expected.getX(), expected.getY(), actual, eps);
    }

    static void assertPositionEquals(float expectedX, float expectedY, BoundedItem actual, float eps) {
        if (ZERO.distanceSquaredTo(actual.getX() - expectedX, actual.getY() - expectedY) > eps)
            fail(formatValues(expectedX, expectedY, actual.getX(), actual.getY()));
    }

    static String formatValues(float expectedX, float expectedY, float actualX, float actualY) {
        return String.format("expected:(%f, %f) but was: (%f, %f))", expectedX, expectedY, actualX, actualY); //NON-NLS
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

    private void navigateTo(Position pos) {
        final Droid droid = gameModel.getDroidsMap().getDroid();
        droid.setPath(droid.getNavigator().findPathTo(pos));
    }

}



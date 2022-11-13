package pp.droids.model;

import org.junit.jupiter.api.BeforeEach;
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
import java.math.*;
import static pp.util.Angle.normalizeAngle;

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
        Jack.setRotation(0);
        enemy = enemy(gameModel, 0f, 0f);
        enemy = new Enemy(gameModel);
        enemy.setPos(19,15);
        flag = new Flag(gameModel);
        flag.setPos(15,16);
        exit = exit(gameModel, 15, 23);
        obstacle = obstacle(gameModel, 0f, 0f);
        obstacle = new Obstacle(gameModel);
        obstacle.setPos(11, 15);
        map.setDroid(Jack, level);
        map.register(enemy, level);
        //map.register(enemy(gameModel, width - 1, height - 1), level);
        //map.register(obstacle(gameModel, 11, 15), level);
        map.register(obstacle, level);
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
        Jack.goForward();
        Jack.update(updateTime);
        //gameModel.update(updateTime);
        assertEquals(start.getX()+(4*updateTime), Jack.getX(), EPS);
    }

    @Test //T002
    public void testBewegenHinten(){
        Position start = new FloatPoint(Jack.getX(), Jack.getY());
        Jack.goBackward();
        Jack.update(updateTime);
        //gameModel.update(updateTime);
        assertEquals(start.getX()-(4*updateTime), Jack.getX(), EPS);
    }

    @Test //T003
    public void testBewegenRechts(){
        Position start = new FloatPoint(Jack.getX(), Jack.getY());
        Jack.stepRight();
        Jack.update(updateTime);
        //gameModel.update(updateTime);
        assertEquals(start.getY()-(4*updateTime), Jack.getY(), EPS);
    }

    @Test //T004
    public void testBewegenLinks(){
        Position start = new FloatPoint(Jack.getX(), Jack.getY());
        Jack.stepLeft();
        Jack.update(updateTime);
        //gameModel.update(updateTime);
        assertEquals(start.getY()+(4*updateTime), Jack.getY(), EPS);
    }

    @Test //014
    public void testDrehen() {
        float rototo = Jack.getRotation(); //Irgendwie muss ich ja kommentieren
        Jack.turnLeft(); //Jack dreht sich einmal nach links
        Jack.update(updateTime);
        assertEquals(normalizeAngle(rototo+updateTime*3.5f), Jack.getRotation(), EPS); //normalize wird benutzt in Angleberechnungen
        float rototo2 = Jack.getRotation();
        Jack.turnRight();
        Jack.update(updateTime*2); //Doppelt zurück drehen um zu schauen ob andere seite genau so funktioniert
        assertEquals(normalizeAngle(rototo2-updateTime*2*3.5f), Jack.getRotation(), EPS);
    }

    @Test //T013
    public void testFlaggeSammeln(){
        Jack.stepLeft();
        gameModel.update(updateTime);
        Jack.update(updateTime);
        assertEquals(16f,flag.getY(),EPS);
        assertEquals(16f,Jack.getY(),EPS);
        assertTrue(Jack.hasFlag()); //Besitzt Jack Flagge?
        Jack.stepLeft();
        Jack.update(updateTime);
        assertEquals(20f, flag.getY(), EPS); //Nimmt er Flagge mit?
        assertPositionEquals(flag, Jack, EPS); //double check
    }

    @Test //T015
    public void testFlaggeAbgeben(){
        testFlaggeSammeln();
        assertPositionEquals(15,23,exit,EPS);
        Jack.stepLeft();
        Jack.update(updateTime);
        map.update(updateTime);
        assertPositionEquals(exit,Jack,EPS); //Jack stoppt nicht im Ziel
        assertTrue(!Jack.hasFlag()); //Fehler: Jack hat die Flagge nicht abgegeben
        assertTrue(gameModel.isGameOver()); //Spiel ist Gewonnen (Test wird hier noch raus gelöscht und in System verschoben)
        assertTrue(gameModel.isGameWon());
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
    @Test //T010
    public void testKollisionJack(){
        Position start = new FloatPoint(15, 15);
        Jack.setPos(start.getX(), start.getY());
        Jack.goForward();
        Jack.update(updateTime);
        Jack.update(updateTime);
        //assertFalse(Jack.collidesWithAnyOtherItem());
        assertEquals(start.getX(),Jack.getX(), EPS);
        Jack.goBackward();
        Jack.update(updateTime);
        assertEquals(start.getX(),Jack.getX(), EPS);
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



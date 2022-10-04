package pp.droids.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pp.util.FloatPoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static pp.droids.model.GamePlayTest.assertPositionEquals;
import static pp.droids.model.GamePlayTest.getEnemies;
import static pp.droids.model.GamePlayTest.getObstacles;
import static pp.droids.model.GamePlayTest.getRockets;

public class LoadMapTest {
    private static final String FILE_NAME = "/maps/map.json"; //NON-NLS
    private static final float EPS = 0.00001f;

    private DroidsModel gameModel;

    @Before
    public void setUp() throws IOException {
        gameModel = new DroidsModel();
        loadMap(FILE_NAME);
    }

    private void loadMap(String name) throws IOException {
        final InputStream stream = getClass().getResourceAsStream(name);
        if (stream == null)
            throw new IOException("Cannot find " + name);
        gameModel.loadMap(stream);
    }

    @After
    public void tearDown() {
        gameModel.shutdown();
    }

    @Test
    public void checkLoadedMap() {
        // Check, if map was loaded successfully
        assertNotNull(gameModel.getDroidsMap());

        // Check, if height and width are loaded successfully
        assertEquals(15, gameModel.getDroidsMap().getHeight());
        assertEquals(25, gameModel.getDroidsMap().getWidth());

        // Check, if the size of all loaded items are right
        assertNotNull(gameModel.getDroidsMap().getDroid());
        assertEquals(5, getObstacles(gameModel).size());
        assertEquals(5, getEnemies(gameModel).size());
        assertEquals(2, getRockets(gameModel).size());
    }

    @Test
    public void checkLoadedDroid() {
        // Check if Droid is on right position
        assertPositionEquals(12, 7, gameModel.getDroidsMap().getDroid(), EPS);
    }

    @Test
    public void checkLoadedEnemies() {
        final Iterator<FloatPoint> it = List.of(new FloatPoint(13, 12),
                                                new FloatPoint(10, 11),
                                                new FloatPoint(4, 9),
                                                new FloatPoint(12, 10),
                                                new FloatPoint(21, 12))
                                            .iterator();
        assertEquals(5, getEnemies(gameModel).size());
        for (Enemy enemy : getEnemies(gameModel))
            GamePlayTest.assertPositionEquals(it.next(), enemy, EPS);
    }

    @Test
    public void checkLoadedObstacles() {
        final Iterator<FloatPoint> it = List.of(new FloatPoint(9, 9),
                                                new FloatPoint(4, 8),
                                                new FloatPoint(22, 7),
                                                new FloatPoint(1, 8),
                                                new FloatPoint(2, 12))
                                            .iterator();
        assertEquals(5, getObstacles(gameModel).size());
        for (Obstacle obstacle : getObstacles(gameModel))
            GamePlayTest.assertPositionEquals(it.next(), obstacle, EPS);
    }

    @Test
    public void checkLoadedRockets() {
        final Iterator<FloatPoint> it = List.of(new FloatPoint(9, 7),
                                                new FloatPoint(9, 5))
                                            .iterator();
        assertEquals(2, getRockets(gameModel).size());
        for (Rocket rocket : getRockets(gameModel))
            GamePlayTest.assertPositionEquals(it.next(), rocket, EPS);
    }
}

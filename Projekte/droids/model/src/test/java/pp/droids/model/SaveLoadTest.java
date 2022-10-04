package pp.droids.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static pp.droids.model.GamePlayTest.getEnemies;
import static pp.droids.model.GamePlayTest.getObstacles;

public class SaveLoadTest {
    private static final float EPS = 0.00001f;
    static final String FILE_NAME = "test-map.json"; //NON-NLS

    private DroidsModel game;
    private File file;

    @Before
    public void setUp() {
        // Create random game
        game = new DroidsModel(new DroidsConfig());
        game.loadRandomMap();

        // Check whether the test file already exists
        file = new File(FILE_NAME);
        if (file.exists())
            fail("File " + FILE_NAME + " already exists. Consider deleting it");
    }

    @Test
    public void saveAndLoad() throws IOException {
        game.saveMap(file);
        // Check whether test file exists
        assertTrue("file " + FILE_NAME + " has not been written", file.exists());

        // Load the file, which has just been written, into a new game
        final DroidsModel game2 = new DroidsModel();
        game2.loadMap(file);

        checkEqualPositions(getEnemies(game),
                            getEnemies(game2));
        checkEqualPositions(getObstacles(game),
                            getObstacles(game2));
        GamePlayTest.assertPositionEquals(game.getDroidsMap().getDroid(),
                                          game2.getDroidsMap().getDroid(),
                                          EPS);
    }

    private void checkEqualPositions(List<? extends BoundedItem> expected, List<? extends BoundedItem> actual) {
        final Iterator<? extends BoundedItem> it1 = expected.iterator();
        final Iterator<? extends BoundedItem> it2 = actual.iterator();
        while (it1.hasNext() && it2.hasNext())
            GamePlayTest.assertPositionEquals(it1.next(), it2.next(), EPS);
        assertFalse(it1.hasNext());
        assertFalse(it2.hasNext());
    }

    @After
    public void tearDown() {
        file.deleteOnExit();
        game.shutdown();
    }
}

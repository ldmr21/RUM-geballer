package pp.droids.model;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

/**
 * Test class for computing observations in models that once caused problems.
 */
public class ObservationTest {
    private static final String FILE_NAME1 = "/maps/problem1.json"; //NON-NLS
    private static final String FILE_NAME2 = "/maps/problem2.json"; //NON-NLS
    private static final String FILE_NAME3 = "/maps/problem3.json"; //NON-NLS
    private static final String FILE_NAME4 = "/maps/problem4.json"; //NON-NLS

    private DroidsModel load(String fileName) throws IOException, URISyntaxException {
        final DroidsModel gameModel = new DroidsModel();
        final URL resource = getClass().getResource(fileName);
        if (resource == null)
            throw new IOException("Cannot find " + fileName);
        final String path = resource.toURI().getPath();
        gameModel.loadMap(new File(path));
        return gameModel;
    }

    @Test
    public void checkVisibility1() throws IOException, URISyntaxException {
        final Droid droid = load(FILE_NAME1).getDroidsMap().getDroid();
        assertNotNull(droid.getObservation());
    }

    @Test
    public void checkVisibility2() throws IOException, URISyntaxException {
        final Droid droid = load(FILE_NAME2).getDroidsMap().getDroid();
        assertNotNull(droid.getObservation());
    }

    @Test
    public void checkVisibility3() throws IOException, URISyntaxException {
        final Droid droid = load(FILE_NAME3).getDroidsMap().getDroid();
        assertNotNull(droid.getObservation());
    }

    @Test
    public void checkVisibility4() throws IOException, URISyntaxException {
        final Droid droid = load(FILE_NAME4).getDroidsMap().getDroid();
        assertNotNull(droid.getObservation());
    }
}

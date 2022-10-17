package pp.droids.exporter;

import com.jme3.app.SimpleApplication;
import com.jme3.export.JmeExporter;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

/**
 * This class exports
 */
public class ModelExporter extends SimpleApplication {
    private static final Logger LOGGER = System.getLogger(ModelExporter.class.getName());

    /**
     * The main method of the converter
     *
     * @param args input args
     */
    public static void main(String[] args) {
        ModelExporter application = new ModelExporter();
        application.start(JmeContext.Type.Headless);
    }

    /**
     * Overrides  {@link com.jme3.app.SimpleApplication#simpleInitApp()}.
     * It initializes a simple app by exporting robots and rocks.
     */
    @Override
    public void simpleInitApp() {
        export(setUpRobot1(), new File("Robot1.j3o")); //NON-NLS
        export(setUpRobot2(), new File("Robot2.j3o")); //NON-NLS
        export(setUpRock(), new File("Rock.j3o")); //NON-NLS
        export(setUpFlag(), new File("Flag.j3o")); //NON-NLS
        export(setUpDog(), new File("Dog.j3o"));       //7h dog initalisieren

        stop();
    }

    /**
     * Sets up robot 1.
     *
     * @return node of the robot
     */
    private Spatial setUpRobot1() {
        return loadModel("Models/Robot2/12211_Robot_l2.obj", "robot", 0.05f); //NON-NLS
    }

    /**
     * Sets up robot 2.
     *
     * @return node of the robot
     */
    private Spatial setUpRobot2() {
        return loadModel("Models/Robot1/12210_robot_v1_L3.obj", "robot", 0.05f); //NON-NLS
    }

    /**
     * Sets up flag.
     *
     * @return node of the robot
     */
    private Spatial setUpFlag() {
        return loadModel("Models/Flag/Flag.obj", "flag", 0.02f); //NON-NLS
    }

    /**
     * Sets up a rock.
     *
     * @return spatial of the rock
     */
    private Spatial setUpRock() {
        final Spatial rock = getAssetManager().loadModel("Models/Rock/Rock.obj"); //NON-NLS
        rock.setLocalScale(0.3f);
        return rock;
    }

    private Spatial setUpDog(){                 //7h Dog obj rein
        final Spatial dog = getAssetManager().loadModel("Models/Dog/Dog.obj"); //NON-NLS
        dog.setLocalScale(0.05f);
        return dog;
    }

    private Node loadModel(String name, String nodeName, float scale) {
        final Node root = new Node(nodeName);
        final Spatial spatial = getAssetManager().loadModel(name);
        root.attachChild(spatial);
        spatial.setLocalScale(scale);
        spatial.rotate(0f, 0f, 0.5f * FastMath.PI);
        spatial.rotate(0f, 0.5f * FastMath.PI, 0f);
        spatial.rotate(0f, 0f, 0.5f * FastMath.PI);
        return root;
    }

    /**
     * Exports spatial into a file
     *
     * @param spatial the spatial
     * @param file    the file where the .j3o file is stored
     */
    private void export(Spatial spatial, File file) {
        JmeExporter exporter = BinaryExporter.getInstance();
        try {
            exporter.save(spatial, file);
        }
        catch (IOException exception) {
            LOGGER.log(Level.ERROR, "write to {0} failed", file); //NON-NLS
            throw new RuntimeException();
        }
        LOGGER.log(Level.INFO, "wrote file {0}", file); //NON-NLS
    }
}

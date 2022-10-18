package pp.droids.view;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.RectangleMesh;
import com.jme3.util.TangentBinormalGenerator;
import pp.droids.GameState;
import pp.droids.ModelViewSynchronizer;
import pp.droids.model.Dog;
import pp.droids.model.Droid;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Item;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.Visitor;
import pp.util.CircularEntity;
import pp.util.TypedSegment;

import static com.jme3.math.FastMath.HALF_PI;
import static com.jme3.math.Vector3f.UNIT_Y;
import static pp.droids.view.CoordinateTransformation.modelToView;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

/**
 * This class is the main synchronizes of droids.
 * Added dog
 */
public class MainSynchronizer extends ModelViewSynchronizer implements Visitor<Spatial> {
    public static final String DROID = "Droid"; //NON-NLS
    public static final String ENEMY = "Enemy"; //NON-NLS
    public static final String FLAG = "Flag"; //NON-NLS
    public static final String PROJECTILE = "Projectile"; //NON-NLS
    public static final String OBSTACLE = "Obstacle"; //NON-NLS
    public static final String ROCKET = "Rocket"; //NON-NLS
    public static final String DROID_MODEL = "Models/Robot1/Robot1.j3o"; //NON-NLS
    public static final String ENEMY_MODEL = "Models/Robot2/Robot2.j3o"; //NON-NLS
    public static final String ROCK_MODEL = "Models/Rock/Rock.j3o"; //NON-NLS
    public static final String FLAG_MODEL = "Models/Flag/Flag.j3o"; //NON-NLS
    public static final String ROCKET_MODEL = "Models/SpaceCraft/Rocket.mesh.xml"; //NON-NLS
    public static final String WALL_TEXTURE = "Textures/Terrain/BrickWall/BrickWall.j3m"; //NON-NLS
    public static final float WALL_STRETCH = 0.3f;
    public static final float ROCKET_HEIGHT = 3f;
    public static final float PROJECTILE_HEIGHT = 0.8f;
    private static final String UNSHADED = "Common/MatDefs/Misc/Unshaded.j3md"; //NON-NLS
    private static final String COLOR = "Color"; //NON-NLS
    private static final ColorRGBA EXIT_COLOR = new ColorRGBA(0.5f, 1f, 1f, 0.5f);
    public static final float EXIT_HEIGHT = 2.5f;
    public static final String DOG_MODEL = "Models/Dog/Dog.j3o";
    public static final String DOG = "DOG";

    /**
     * Constructor of the synchronizer.
     *
     * @param gameState currently game state
     * @param root      root node
     */
    public MainSynchronizer(GameState gameState, Node root) {
        super(gameState, root);
    }

    /**
     * Calls the accept method.
     *
     * @param item given item
     * @return spatial
     */
    @Override
    protected Spatial translate(Item item) {
        return item.accept(this);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Droid}.
     *
     * @param robot the droid
     * @return created spacial
     */
    @Override
    public Spatial visit(Droid robot) {
        final Spatial spatial = gameState.getApp().getAssetManager().loadModel(DROID_MODEL);
        spatial.scale(robot.getRadius() / Droid.BOUNDING_RADIUS);
        spatial.setShadowMode(ShadowMode.CastAndReceive);
        spatial.addControl(new RobotControl(robot));
        spatial.setName(DROID);
        return spatial;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Enemy}.
     *
     * @param robot an enemy
     * @return created spacial
     */
    @Override
    public Spatial visit(Enemy robot) {
        final Spatial spatial = gameState.getApp().getAssetManager().loadModel(ENEMY_MODEL);
        spatial.scale(robot.getRadius() / Enemy.BOUNDING_RADIUS);
        spatial.setShadowMode(ShadowMode.CastAndReceive);
        spatial.addControl(new RobotControl(robot));
        spatial.setName(ENEMY);
        return spatial;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Projectile}.
     *
     * @param projectile a projectile
     * @return created spacial
     */
    @Override
    public Spatial visit(Projectile projectile) {
        Spatial missile = gameState.getApp().getAssetManager().loadModel(ROCKET_MODEL);
        missile.scale(0.3f);
        missile.updateGeometricState();
        missile.setShadowMode(ShadowMode.Cast);
        missile.addControl(new MissileControl(projectile, PROJECTILE_HEIGHT));
        missile.setName(PROJECTILE);
        return missile;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Obstacle}.
     *
     * @param obstacle an obstacle
     * @return created spacial
     */
    @Override
    public Spatial visit(Obstacle obstacle) {
        Spatial rock = gameState.getApp().getAssetManager().loadModel(ROCK_MODEL);
        rock.scale(obstacle.getRadius() / Obstacle.BOUNDING_RADIUS);
        rock.setShadowMode(ShadowMode.CastAndReceive);
        rock.getLocalRotation().fromAngleAxis(obstacle.getRotation(), UNIT_Y);
        rock.setLocalTranslation(modelToViewX(obstacle),
                                 modelToViewY(obstacle),
                                 modelToViewZ(obstacle));
        rock.setName(OBSTACLE);
        return rock;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Rocket}.
     *
     * @param rocket a rocket
     * @return created spacial
     */
    @Override
    public Spatial visit(Rocket rocket) {
        Spatial missile = gameState.getApp().getAssetManager().loadModel(ROCKET_MODEL);
        missile.updateGeometricState();
        missile.addControl(new MissileControl(rocket, ROCKET_HEIGHT));
        missile.setName(ROCKET);
        return missile;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Maze}.
     *
     * @param maze a maze
     * @return parent node
     */
    @Override
    public Spatial visit(Maze maze) {
        Node parent = new Node("Maze"); //NON-NLS
        int ctr = 0;
        final float roundedLength = Math.round(maze.getLength());
        final float stretch = WALL_STRETCH * roundedLength / maze.getLength();
        final float height = maze.getHeight() * stretch;
        float len = 0f;
        for (TypedSegment seg : maze.getSegments()) {
            RectangleMesh rect = new RectangleMesh(modelToView(seg.from()),
                                                   modelToView(seg.to()),
                                                   modelToView(seg.from()).add(0f, maze.getHeight(), 0f));
            final float next = len + seg.length() * stretch;
            rect.setTexCoords(new Vector2f[]{
                    new Vector2f(len, 0f),
                    new Vector2f(next, 0f),
                    new Vector2f(next, -height),
                    new Vector2f(len, -height)});
            Material mat = gameState.getApp().getAssetManager().loadMaterial(WALL_TEXTURE);
            Geometry wall = new Geometry("WALL-" + ++ctr, rect); //NON-NLS
            TangentBinormalGenerator.generate(wall);
            wall.setMaterial(mat);
            wall.setShadowMode(ShadowMode.CastAndReceive);
            parent.attachChild(wall);
            len = next;
        }
        return parent;
    }

    @Override
    public Spatial visit(Flag flag) {
        final Spatial spatial = gameState.getApp().getAssetManager().loadModel(FLAG_MODEL);
        spatial.setShadowMode(ShadowMode.CastAndReceive);
        spatial.addControl(new FlagControl(flag));
        spatial.setName(FLAG);
        return spatial;
    }

    /**
     *
     * @param dog The dog handed over
     * @return It returns the dog
     */
    @Override
    public Spatial visit(Dog dog){
        final Spatial spatial = gameState.getApp().getAssetManager().loadModel(DOG_MODEL);
        spatial.setShadowMode(ShadowMode.CastAndReceive);
        spatial.addControl(new DogControl(dog));
        spatial.setName(DOG);
        return spatial;
    }

    @Override
    public Spatial visit(Exit exit) {
        return makeCylinder(exit, EXIT_HEIGHT, EXIT_COLOR);
    }

    private Geometry makeCylinder(CircularEntity item, float height, ColorRGBA color) {
        final Cylinder cylMesh = new Cylinder(2, 10, item.getRadius(), height);
        final Geometry cylGeom = new Geometry("cylinder", cylMesh); //NON-NLS
        Material mat = new Material(gameState.getApp().getAssetManager(), UNSHADED); //NON-NLS
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.setColor(COLOR, color); //NON-NLS
        cylGeom.setMaterial(mat);
        cylGeom.rotate(HALF_PI, 0f, 0f);
        cylGeom.setLocalTranslation(modelToViewX(item),
                                    modelToViewY(item) + 0.5f * height,
                                    modelToViewZ(item));
        cylGeom.setShadowMode(ShadowMode.CastAndReceive);
        return cylGeom;
    }
}

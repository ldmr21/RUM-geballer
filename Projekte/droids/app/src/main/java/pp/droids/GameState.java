package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.RectangleMesh;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import pp.droids.model.Droid;
import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.notifications.GameEventAdapter;
import pp.droids.view.MainSynchronizer;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static com.jme3.math.FastMath.cos;
import static com.jme3.math.FastMath.sin;
import static pp.droids.view.CoordinateTransformation.modelToView;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

/**
 * <code>GameState</code> extends the class {@link com.jme3.app.state.AbstractAppState}.
 * <p>
 * This class handles the particular game state of the app.
 */
public class GameState extends AbstractAppState {
    private static final Logger LOGGER = System.getLogger(GameState.class.getName());
    /**
     * The node name of the floor
     */
    public static final String FLOOR_NAME = "Floor"; //NON-NLS
    /**
     * Distance of the camera position behind the droid
     */
    private static final float BEHIND_DROID = 2f;
    /**
     * Distance of the camera position in front of the droid
     */
    private static final float FRONT_DROID = 0.25f;
    /**
     * Height of the camera position above ground
     */
    private static final float ABOVE_GROUND = 2f;
    /**
     * How much the camera looks down
     */
    private static final float INCLINATION = 0.5f;
    /**
     * Die Höhe des Droidsmodels
     */
    private static final float DROID_HEIGHT = 1.25f;

    private static final float ROTATE_SPEED = 0.005f;

    private static final float DECLINE_SPEED = 0.03f;

    /**
     * A private enum containing states for the camera.
     */
    private enum CamState {FIRST, THIRD}

    private enum UpState{UP, DOWN, STOP}

    private DroidsApp app;
    private final Node viewNode = new Node("view"); //NON-NLS
    private final Node itemNode = new Node("items"); //NON-NLS
    private DroidsModel model;
    private final ModelViewSynchronizer synchronizer = new MainSynchronizer(this, itemNode);
    /**
     * Droids camera starts with third person state
     */
    private CamState camState = CamState.THIRD;

    /**
     * Camera Y-Axis state
     */
    private UpState upState = UpState.STOP;

    private float camCurrentAngle = 0f;
    private final float camMaxAngle = 0.5f;
    private final float camMinAngle = -0.5f;

    private float camCurrentHeight = 0f;
    private final float camMaxHeight = 0f;
    private final float camMinHeight = -1.5f;

    /**
     * Returns the droids app.
     *
     * @return droids app
     */
    public DroidsApp getApp() {
        return app;
    }

    /**
     * Returns the model.
     *
     * @return droids model
     */
    public DroidsModel getModel() {
        return model;
    }

    /**
     * Resets the synchronizer for model and view, the camera and the floor.
     */
    public void reset() {
        synchronizer.reset();
        adjustCamera();
        setupFloor();
    }

    /**
     * Sets up the app state.
     * <p>
     * It overrides {@link com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)}
     *
     * @param stateManager The state manager
     * @param application  The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        this.app = (DroidsApp) application;
        model = new DroidsModel(app.getConfig());
        model.loadRandomMap();
        app.getRootNode().attachChild(viewNode);
        viewNode.attachChild(itemNode);
        app.getStateManager().getState(GameSound.class).register(model);
        model.addGameEventListener(new GameEventAdapter() {
            @Override
            public void mapChanged(DroidsMap oldMap, DroidsMap newMap) {
                reset();
            }
        });
        setupLights();
        createLagoonSky();
        reset();
        resetCamera();
        if (isEnabled()) enableState(true);
    }

    /**
     * Cleans up the game state.
     * <p>
     * It overrides {@linkplain com.jme3.app.state.AppState#cleanup()}.
     */
    @Override
    public void cleanup() {
        super.cleanup();
        LOGGER.log(Level.INFO, "called GameState::cleanup"); //NON-NLS
        if (model != null)
            model.shutdown();
    }

    /**
     * Reset the camera's direction when switching between 1st and 3rd person.
     *
     */
    private void resetCamera(){
        camCurrentAngle = 0f;
        camCurrentHeight = 0f;
        }

    public void rotateUp(){
        upState = switch (upState){
            case UP, STOP -> UpState.UP;
            case DOWN -> UpState.STOP;
        };
    }

    public void rotateDown(){
        upState = switch (upState){
            case DOWN, STOP -> UpState.DOWN;
            case UP -> UpState.STOP;
        };
    }

    private float getDeclineSpeed(){
        return switch (upState){
            case UP -> -DECLINE_SPEED;
            case DOWN -> DECLINE_SPEED;
            case STOP -> 0f;
        };
    }

    private float getRotateSpeed() {
        return switch (upState) {
            case UP -> ROTATE_SPEED;
            case DOWN -> -ROTATE_SPEED;
            case STOP -> 0f;
        };
    }

    private float ensureRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Adjusts the camera to see through the droid's eyes.
     */
    private void adjustCamera() {
        final Droid droid = model.getDroidsMap().getDroid();
        final Camera camera = app.getCamera();
        final float angle = droid.getRotation();
        final float cos;
        final float sin;

        if(camState == CamState.FIRST){
            camCurrentAngle = ensureRange(camCurrentAngle + getRotateSpeed(), camMinAngle, camMaxAngle);
            cos = FRONT_DROID * cos(angle);
            sin = FRONT_DROID * sin(angle);
            final float x = droid.getX() - cos;
            final float y = droid.getY() - sin;
            camera.setLocation(new Vector3f(modelToViewX(x, y),
                                            modelToViewY(x, y) + DROID_HEIGHT,
                                            modelToViewZ(x, y)));
            camera.getRotation().lookAt(new Vector3f(modelToViewX(cos, sin),
                                                     camCurrentAngle,
                                                     modelToViewZ(cos, sin)),
                                        Vector3f.UNIT_Y);
        }else{
            camCurrentHeight = ensureRange(camCurrentHeight + getDeclineSpeed(), camMinHeight, camMaxHeight);
            camCurrentAngle = ensureRange(camCurrentAngle + getRotateSpeed() * 3, 0, 2 * camMaxAngle);
            cos = BEHIND_DROID * cos(angle);
            sin = BEHIND_DROID * sin(angle);
            final float x = droid.getX() - cos;
            final float y = droid.getY() - sin;
            camera.setLocation(new Vector3f(modelToViewX(x, y),
                                            camCurrentHeight + ABOVE_GROUND,
                                            modelToViewZ(x, y)));
            camera.getRotation().lookAt(new Vector3f(modelToViewX(cos, sin),
                                                     camCurrentAngle - INCLINATION,
                                                     modelToViewZ(cos, sin)),
                                        Vector3f.UNIT_Y);
        }
        camera.update();
        upState = UpState.STOP;
    }

    public void switchCamStates(){
        camState = switch (camState) {
            case FIRST -> CamState.THIRD;
            case THIRD -> CamState.FIRST;
        };
        resetCamera();
    }

    /**
     * Sets up the lights.
     */
    private void setupLights() {
        var dlsr = new DirectionalLightShadowRenderer(app.getAssetManager(), 2048, 3);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
        app.getViewPort().addProcessor(dlsr);

        var sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1f, -0.7f, 1f).normalizeLocal());
        viewNode.addLight(sun);
        dlsr.setLight(sun);

        var ambientLight = new AmbientLight(new ColorRGBA(0.3f, 0.3f, 0.3f, 0f));
        viewNode.addLight(ambientLight);
    }

    /**
     * Enables or disables the game state.
     * <p>
     * It overrides {@link com.jme3.app.state.AbstractAppState#setEnabled(boolean)}
     *
     * @param enabled activate the game state or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        if (app != null) enableState(enabled);
    }

    /**
     * Permits a game state.
     */
    private void enableState(boolean enabled) {
        final InputManager inputManager = app.getInputManager();
        getTextOverlay().setEnabled(enabled);
        getGameInput().setEnabled(enabled);
        inputManager.setCursorVisible(false);
    }

    /**
     * Updates the synchronizes and the model and enabled the game input, if the game isn't over.
     *
     * @param delta Time since the last call to update(), in seconds.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (!model.isGameOver()) {
            model.update(delta);
            synchronizer.syncWithModel();
            adjustCamera();
        }
        getGameInput().setEnabled(!model.isGameOver());
    }

    /**
     * Sets up floor for this game state.
     */
    private void setupFloor() {
        Material mat = app.getAssetManager().loadMaterial("Textures/Terrain/Sand/Sand.j3m"); //NON-NLS
        final DroidsMap map = model.getDroidsMap();
        final Mesh floorMesh = new RectangleMesh(modelToView(map.getXMin(), map.getYMin()),
                                                 modelToView(map.getXMax(), map.getYMin()),
                                                 modelToView(map.getXMin(), map.getYMax()));
        floorMesh.scaleTextureCoordinates(new Vector2f(0.1f * map.getWidth(), 0.1f * map.getHeight()));
        Geometry floor = new Geometry(FLOOR_NAME, floorMesh);
        TangentBinormalGenerator.generate(floor);
        floor.setMaterial(mat);
        floor.setShadowMode(ShadowMode.Receive);
        itemNode.attachChild(floor);
    }

    /**
     * Loads different textures for the lagoon sky and creates it.
     */
    private void createLagoonSky() {
        final AssetManager assetManager = app.getAssetManager();
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg"); //NON-NLS
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg"); //NON-NLS
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg"); //NON-NLS
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg"); //NON-NLS
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg"); //NON-NLS
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg"); //NON-NLS

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down); //NON-NLS
        sky.setName("Sky"); //NON-NLS
        viewNode.attachChild(sky);
    }

    /**
     * Gives the item node of the game state.
     *
     * @return item node
     */
    Node getItemNode() {
        return itemNode;
    }

    /**
     * Gives the game input from the state manager of the app.
     *
     * @return game input
     */
    private GameInput getGameInput() {
        return app.getStateManager().getState(GameInput.class);
    }

    /**
     * Gives the text overlay from the state manager of the app.
     *
     * @return text overlay
     */
    private TextOverlay getTextOverlay() {
        return app.getStateManager().getState(TextOverlay.class);
    }
}

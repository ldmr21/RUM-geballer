package pp.droids.view.radar;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import pp.droids.DroidsApp;
import pp.droids.GameState;
import pp.droids.ModelViewSynchronizer;
import pp.droids.model.DroidsMap;
import pp.droids.model.MapLevel;
import pp.util.Prefs;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.prefs.Preferences;

/**
 * An AppState implementing a radar view in the top-right corner of the main viewport.
 * It currently shows the droids, all items and the walls.
 */
public class RadarView extends AbstractAppState {
    private static final Logger LOGGER = System.getLogger(RadarView.class.getName());
    private static final Preferences PREFS = Prefs.getPreferences(RadarView.class);
    private static final String ENABLED_PREF = "enabled"; //NON-NLS
    private static final float SIZE = 0.3f;
    static final float SPRITE_SIZE = 11f;
    private DroidsApp app;
    private DroidsMap map;
    private MapLevel level;
    private ModelViewSynchronizer synchronizer;
    private final Node radarGuiNode = new Node("radarGui");
    private final Node radarNode = new Node("radar"); //NON-NLS
    private final Node centerNode = new Node("center"); //NON-NLS
    private final Node itemNode = new Node("radar-items"); //NON-NLS

    /**
     * Figures out, if the preferences are enabled.
     *
     * @return if preferences are enabled
     */
    public static boolean enabledInPreferences() {
        return PREFS.getBoolean(ENABLED_PREF, true);
    }

    /**
     * Sets up the radar view.
     *
     * @param stateManager The state manager
     * @param app          The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        synchronizer = new RadarSynchronizer(getGameState(), itemNode);
        setupNodes();
        setupBackground();
        setupViewPort();
        if (isEnabled())
            enableState();
    }

    /**
     * This method gets the game state.
     *
     * @return game state
     */
    private GameState getGameState() {
        return app.getStateManager().getState(GameState.class);
    }

    /**
     * Sets up the nodes for the radar view.
     */
    private void setupNodes() {
        app.getGuiNode().attachChild(radarGuiNode);
        radarNode.attachChild(centerNode);
        centerNode.attachChild(itemNode);
        final AppSettings settings = app.getContext().getSettings();
        centerNode.setLocalTranslation(0.5f * settings.getWidth(), 0.5f * settings.getHeight(), 0f);
        centerNode.scale(SPRITE_SIZE / SIZE);
    }

    /**
     * Sets up the background of the radar view.
     */
    private void setupBackground() {
        final Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"); //NON-NLS
        mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f)); //NON-NLS
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        final AppSettings settings = app.getContext().getSettings();
        final Geometry background = new Geometry("RadarBackground",
                                                 new Quad(settings.getWidth(), settings.getHeight()));
        background.setMaterial(mat);
        background.setLocalTranslation(0f, 0f, -1f);
        background.setCullHint(CullHint.Never);
        radarNode.attachChild(background);
    }

    /**
     * Sets up the view port of the radar view.
     */
    private void setupViewPort() {
        final Camera guiCam = app.getGuiViewPort().getCamera().clone();
        guiCam.setViewPort(1f - SIZE, 1f, 1f - SIZE, 1f);
        final ViewPort radarGuiVP = app.getRenderManager().createPostView("radar-gui", guiCam); //NON-NLS
        radarGuiVP.setClearFlags(false, false, false);
        radarGuiVP.attachScene(radarGuiNode);
    }

    /**
     * Enables or disables the radar state.
     *
     * @param enabled activate the AppState or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled); //NON-NLS
        LOGGER.log(Level.INFO, "Radar view enabled: {0}", enabled); //NON-NLS
        PREFS.put(ENABLED_PREF, String.valueOf(enabled));
        if (enabled)
            enableState();
        else
            disableState();
    }

    /**
     * Enables the radar state.
     */
    private void enableState() {
        synchronizer.reset();
        radarGuiNode.attachChild(radarNode);
    }

    /**
     * Disables the radar state.
     */
    private void disableState() {
        radarGuiNode.detachChild(radarNode);
    }

    /**
     * Updates the radar view.
     *
     * @param delta Time since the last call to update(), in seconds.
     */
    @Override
    public void update(float delta) {
        final DroidsMap currentMap = getGameState().getModel().getDroidsMap();
        final MapLevel currentLevel = currentMap.getDroid().getLevel();
        if (currentMap != map || currentLevel != level) {
            map = currentMap;
            level = currentLevel;
            synchronizer.reset();
        }
        synchronizer.syncWithModel();
    }
}
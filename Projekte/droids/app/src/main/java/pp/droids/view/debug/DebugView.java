package pp.droids.view.debug;

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
import pp.droids.model.Debugee;
import pp.droids.model.DroidsMap;
import pp.graphics.Draw;
import pp.util.Prefs;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Objects;
import java.util.prefs.Preferences;

import static com.jme3.math.FastMath.TWO_PI;
import static com.jme3.math.FastMath.cos;
import static com.jme3.math.FastMath.sin;
import static com.jme3.math.FastMath.sqr;
import static com.jme3.math.FastMath.sqrt;

/**
 * An AppState implementing a debug view in the top-left corner of the main viewport.
 * When enabled, it either shows the current observation by the droid, or the observation map
 * representing the collected observations. One can switch between these modes (and being disabled)
 * using method {@linkplain #toggleMode()}.
 */
public class DebugView extends AbstractAppState {
    private static final Logger LOGGER = System.getLogger(DebugView.class.getName());
    private static final Preferences PREFS = Prefs.getPreferences(DebugView.class);
    private static final String ENABLED_PREF = "enabled"; //NON-NLS
    private static final String UNSHADED = "Common/MatDefs/Misc/Unshaded.j3md"; //NON-NLS
    private static final float SIZE = 0.5f;
    static final float ZOOM = 25f;
    private Mode mode;
    private DroidsApp app;
    private final Node debugGuiNode = new Node("debugGui");
    private final Node debugNode = new Node("debug"); //NON-NLS
    final Node centerNode = new Node("center"); //NON-NLS
    private DroidsMap oldMap;
    final MapMode mapMode = new MapMode(this);
    final ObservationMode observationMode = new ObservationMode(this);
    private Debugee debugee;

    /**
     * Figures out, if the preferences are enabled.
     *
     * @return if preferences are enabled
     */
    public static boolean enabledInPreferences() {
        return PREFS.getBoolean(ENABLED_PREF, true);
    }

    /**
     * Sets up the debug view.
     *
     * @param stateManager The state manager
     * @param app          The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        debugee = selectDebugee();
        setupNodes();
        setupDecoration();
        setupBackground();
        setupViewPort();
        selectMode(mapMode);
        if (isEnabled())
            enableState();
    }

    /**
     * Returns the debugee whose observations are visualized in this DebugView..
     */
    private Debugee selectDebugee() {
        return getGameState().getModel().getDroidsMap().getDroid();
    }

    void selectMode(Mode mode) {
        Objects.requireNonNull(mode);
        if (this.mode == mode) return;
        if (this.mode != null)
            this.mode.deselect();
        this.mode = mode;
        mode.select();
    }

    Draw getDraw() {
        return app.getDraw();
    }

    /**
     * The method gets the game state.
     *
     * @return game state
     */
    private GameState getGameState() {
        return app.getStateManager().getState(GameState.class);
    }

    Debugee getDebugee() {
        return debugee;
    }

    /**
     * Sets up the nodes for the debug view.
     */
    private void setupNodes() {
        app.getGuiNode().attachChild(debugGuiNode);
        debugNode.attachChild(centerNode);
        final AppSettings settings = app.getContext().getSettings();
        centerNode.setLocalTranslation(0.5f * settings.getWidth(), 0.5f * settings.getHeight(), 0f);
        centerNode.scale(ZOOM);
    }

    /**
     * Sets up the decoration of the debug view.
     */
    private void setupDecoration() {
        final AppSettings s = app.getContext().getSettings();
        centerNode.attachChild(app.getDraw().makeLine(-0.5f * s.getWidth(), 0f, 0.5f * s.getWidth(), 0f, 0f, ColorRGBA.Gray));
        centerNode.attachChild(app.getDraw().makeLine(0f, -0.5f * s.getHeight(), 0f, 0.5f * s.getHeight(), 0f, ColorRGBA.Gray));
        final float viewingArea = app.getConfig().getViewingArea();
        if (viewingArea < TWO_PI) {
            final float len = 0.5f * sqrt(sqr(s.getWidth()) + sqr(s.getHeight()));
            final float sin = len * sin(0.5f * viewingArea);
            final float cos = len * cos(0.5f * viewingArea);
            centerNode.attachChild(app.getDraw().makeLine(0f, 0f, sin, cos, 0f, ColorRGBA.Yellow));
            centerNode.attachChild(app.getDraw().makeLine(0f, 0f, -sin, cos, 0f, ColorRGBA.Yellow));
        }
    }

    /**
     * Sets up the background of the debug view.
     */
    private void setupBackground() {
        final Material mat = new Material(app.getAssetManager(), UNSHADED); //NON-NLS
        mat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f)); //NON-NLS
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        final AppSettings settings = app.getContext().getSettings();
        final Geometry background = new Geometry("DebugBackground",
                                                 new Quad(settings.getWidth(), settings.getHeight()));
        background.setMaterial(mat);
        background.setLocalTranslation(0f, 0f, -10f);
        background.setCullHint(CullHint.Never);
        debugNode.attachChild(background);
    }

    /**
     * Sets up the view port of the debug view.
     */
    private void setupViewPort() {
        final Camera guiCam = app.getGuiViewPort().getCamera().clone();
        guiCam.setViewPort(0f, SIZE, 1f - SIZE, 1f);
        final ViewPort debugGuiVP = app.getRenderManager().createPostView("debug-gui", guiCam); //NON-NLS
        debugGuiVP.setClearFlags(false, false, false);
        debugGuiVP.attachScene(debugGuiNode);
    }

    /**
     * Cycles through being disabled and enables, and within being enabled,
     * between the different modes.
     */
    public void toggleMode() {
        if (isEnabled())
            mode.nextMode();
        else {
            selectMode(mapMode);
            setEnabled(true);
        }
    }

    /**
     * Enables or disables the debug state.
     *
     * @param enabled activate the AppState or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled); //NON-NLS
        LOGGER.log(Level.INFO, "Debug view enabled: {0}", enabled); //NON-NLS
        PREFS.put(ENABLED_PREF, String.valueOf(enabled));
        if (enabled)
            enableState();
        else
            disableState();
    }

    /**
     * Enables the debug state.
     */
    private void enableState() {
        debugGuiNode.attachChild(debugNode);
    }

    /**
     * Disables the debug state.
     */
    private void disableState() {
        debugGuiNode.detachChild(debugNode);
    }

    /**
     * Updates the debug view.
     *
     * @param delta Time since the last call to update(), in seconds.
     */
    @Override
    public void update(float delta) {
        final DroidsMap droidsMap = getGameState().getModel().getDroidsMap();
        if (droidsMap != oldMap) {
            reset();
            oldMap = droidsMap;
        }
        mode.update();
    }

    /**
     * Resets the debug view.
     */
    public void reset() {
        mapMode.reset();
        debugee = selectDebugee();
    }
}

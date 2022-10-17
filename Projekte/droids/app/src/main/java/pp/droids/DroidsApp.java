package pp.droids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;
import pp.droids.view.debug.DebugView;
import pp.droids.view.radar.RadarView;
import pp.graphics.Draw;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.logging.LogManager;

/**
 * <code>DroidsApp</code> extends the class {@link com.jme3.app.SimpleApplication}.
 * This is the main class of the game.
 */


public class DroidsApp extends SimpleApplication {
    private static final Logger LOGGER = System.getLogger(DroidsApp.class.getName());
    private static final String CONFIG_JSON = "config.json";
    public static final String PAUSE = "PAUSE";
    private final DroidsAppConfig config;
    private final Node droidsGuiNode = new Node();
    private Draw draw;

    static {
        // Configure logging
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration(new FileInputStream("logging.properties"));
            LOGGER.log(Level.INFO, "read logging properties"); //NON-NLS
        }
        catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
    }

    /**
     * Main method of the droids app
     *
     * @param args input args
     */
    public static void main(String[] args) {
        new DroidsApp(loadConfig()).start();
    }

    /**
     * Creates a new DroidsApp object.
     *
     * @param config the configuration used in the game.
     */
    private DroidsApp(DroidsAppConfig config) {
        this.config = config;
        setShowSettings(config.getShowSettings() && !isMac());
        setSettings(config.getSettings());
    }

    /**
     * Returns true iff this is running on a Mac computer.
     */
    private static boolean isMac() {
        return System.getProperty("os.name").toUpperCase().contains("MAC");
    }

    /**
     * Closes the application.
     *
     * @param esc If true, the user pressed ESC to close the application.
     */
    @Override
    public void requestClose(boolean esc) {
        // do nothing
    }

    /**
     * Overrides  {@link com.jme3.app.SimpleApplication#simpleInitApp()}.
     * It initializes a simple app by setting up the gui node, the input and the states.
     */
    @Override
    public void simpleInitApp() {
        draw = new Draw(assetManager);
        setupGuiNode();
        setupInput();
        setupStates();
    }

    /**
     * Sets up a gui node for the DroidsApp.
     */
    private void setupGuiNode() {
        guiNode.attachChild(droidsGuiNode);
        guiViewPort.detachScene(guiNode);
        guiViewPort.attachScene(droidsGuiNode);
    }

    /**
     * Sets up the input for the DroidsApp.
     */
    private void setupInput() {
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        inputManager.deleteMapping(INPUT_MAPPING_MEMORY);
        inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(pauseListener, PAUSE);
    }

    /**
     * Initializes the States and disables them.
     */
    private void setupStates() {
        stateManager.detach(stateManager.getState(StatsAppState.class));
        stateManager.detach(stateManager.getState(DebugKeysAppState.class));
        flyCam.setEnabled(false);
        if (config.getShowStatistics()) {
            final BitmapFont normalFont = assetManager.loadFont("Interface/Fonts/Default.fnt"); //NON-NLS
            final StatsAppState stats = new StatsAppState(getDroidsGuiNode(), normalFont);
            stateManager.attach(stats);
        }
        final GameState gameState = new GameState();
        final GameSound gameSound = new GameSound();
        final GameMusic gameMusic = new GameMusic();

        final TextOverlay textOverlay = new TextOverlay();

        final GameInput gameInput = new GameInput();
        final RadarView radarView = new RadarView();
        final DebugView debugView = new DebugView();
        final MenuState menuState = new MenuState();
        stateManager.attachAll(gameState, menuState, textOverlay, gameSound, gameMusic, gameInput, radarView, debugView);
        gameState.setEnabled(false);
        textOverlay.setEnabled(false);
        gameInput.setEnabled(false);
        gameSound.setEnabled(GameSound.enabledInPreferences());
        gameMusic.setEnabled(true);
        radarView.setEnabled(RadarView.enabledInPreferences());
        debugView.setEnabled(DebugView.enabledInPreferences());
    }

    /**
     * Is used to receive an input, when the pause button is pressed and switches States.
     */
    private final ActionListener pauseListener = (name, isPressed, tpf) -> {
        if (PAUSE.equals(name) && isPressed)
            switchStates();
    };

    /**
     * Switches between the MenuState and the GameState.
     */
    void switchStates() {
        final GameState gameState = stateManager.getState(GameState.class);
        final MenuState menuState = stateManager.getState(MenuState.class);
        gameState.setEnabled(!gameState.isEnabled());
        menuState.setEnabled(!menuState.isEnabled());
    }

    /**
     * Is a method to get the config.
     *
     * @return Configuration of the game.
     */
    public DroidsAppConfig getConfig() {
        return config;
    }

    /**
     * Is a method to get the droids gui node.
     *
     * @return Gui node of the droids.
     */
    public Node getDroidsGuiNode() {
        return droidsGuiNode;
    }

    /**
     * Returns the Draw object for drawing graphical primitives.
     */
    public Draw getDraw() {
        return draw;
    }

    /**
     * Loads the game config.
     *
     * @return a new Droids Configuration
     */
    private static DroidsAppConfig loadConfig() {
        final File file = new File(CONFIG_JSON);
        if (file.exists())
            try {
                final DroidsAppConfig conf = new ObjectMapper().readValue(file, DroidsAppConfig.class);
                LOGGER.log(Level.INFO, "read config from {0}", file.getAbsolutePath()); //NON-NLS
                return conf;
            }
            catch (IOException e) {
                LOGGER.log(Level.WARNING, "when trying to read " + file.getAbsolutePath(), e); //NON-NLS
            }
        LOGGER.log(Level.INFO, "using default configuration"); //NON-NLS
        return new DroidsAppConfig();
    }
}

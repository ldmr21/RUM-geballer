package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import pp.droids.model.Droid;
import pp.droids.view.debug.DebugView;
import pp.droids.view.radar.RadarView;
import pp.util.Position;
import pp.util.Segment;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static pp.droids.view.CoordinateTransformation.viewToModel;

/**
 * Handles input by the user as an app state.
 */
class GameInput extends AbstractAppState {
    private static final Logger LOGGER = System.getLogger(GameInput.class.getName());
    private static final String SHOOT = "SHOOT";
    private static final String LEFT = "LEFT";
    private static final String RIGHT = "RIGHT";
    private static final String FORWARD = "FORWARD";
    private static final String BACKWARD = "BACKWARD";
    private static final String MUTE = "MUTE";
    private static final String RADAR_MAP = "RADAR";
    private static final String NAVIGATE = "NAVIGATE";
    private static final String DEBUG = "DEBUG";
    /**7b Step3: die Musik mit Keytrigger wird eingeführt.
     * @param MUSIC initialize the music String for the Keytrigger
     * */
    private static final String MUSIC = "MUSIC";

    private DroidsApp app;
    private Future<List<Segment>> futurePath;

    /**
     * Adds key trigger to different states.
     * <p>
     * It overrides {@link com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)}
     *
     * @param stateManager The state manager
     * @param app          The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        final InputManager inputManager = app.getInputManager();
        /**Step4: der Keytrigger wird der Taste "B" zugewiesen
         */
        inputManager.addMapping(MUSIC, new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping(SHOOT, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(DEBUG, new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping(MUTE, new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping(RADAR_MAP, new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping(NAVIGATE, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        if (isEnabled()) enableState();
    }

    /**
     * Sets the attribute enabled. It overrides {@link com.jme3.app.state.AbstractAppState#setEnabled(boolean)}
     *
     * @param enabled activate the AppState or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        if (app != null) {
            if (enabled)
                enableState();
            else
                disableState();

        }
    }

    /**
     * Permits a GameInput.
     */
    private void enableState() {
        final InputManager inputManager = app.getInputManager();
        inputManager.addListener(analogListener, SHOOT, LEFT, RIGHT, FORWARD, BACKWARD);
        inputManager.addListener(actionListener, MUTE, RADAR_MAP, NAVIGATE, DEBUG, MUSIC);
    }

    /**
     * Disables a GameInput.
     */
    private void disableState() {
        final GameSound music = app.getStateManager().getState(GameSound.class);
        final InputManager inputManager = app.getInputManager();
        inputManager.removeListener(actionListener);
        inputManager.removeListener(analogListener);
        //if(music.isEnabled()) {
        //    inputManager.addListener(actionListener, MUSIC);
        //}
    }

    /**
     * Receives analog events and calls the corresponding method for that droid.
     */
    private final AnalogListener analogListener = (name, value, tpf) -> {
        if (app != null)
            switch (name) {
                case SHOOT -> getDroid().fire();
                case LEFT -> getDroid().turnLeft();
                case RIGHT -> getDroid().turnRight();
                case FORWARD -> getDroid().goForward();
                case BACKWARD -> getDroid().goBackward();
                default -> { /* do nothing */}
            }
    };

    /**
     * Receives input events and calls th corresponding method.
     */
    private final ActionListener actionListener = (name, isPressed, tpf) -> {
        if (isPressed && app != null) {
            switch (name) {
                /**Step6: die Aktion wird dem ein und ausschalten zugewiesen. (mit der Taste "B") */
                case MUSIC -> toggleMusic();
                case MUTE -> toggleMuted();
                case RADAR_MAP -> toggleRadarMap();
                case NAVIGATE -> navigate();
                case DEBUG -> toggleDebugView();
                default -> { /* empty */ }
            }
        }
    };

    /**
     * Enables or disables the debug view.
     */
    private void toggleDebugView() {
        app.getStateManager().getState(DebugView.class).toggleMode();
    }

    /**
     * Enables or disables the radar view.
     */
    private void toggleRadarMap() {
        final RadarView radarView = app.getStateManager().getState(RadarView.class);
        radarView.setEnabled(!radarView.isEnabled());
    }

    /**
     * Turns on or mutes the sound of the game.
     */
    private void toggleMuted() {
        final GameSound sound = app.getStateManager().getState(GameSound.class);
        sound.setEnabled(!sound.isEnabled());
    }

    /**Step5: Methode um Musik an und aus zu stellen
     * Es wird auf die GameSound klasse zugegriffen und mit dem Getter die Musik verändert.
     */
    private void toggleMusic(){
        final GameSound music = app.getStateManager().getState(GameSound.class);
        if(music.getBackground_music().getVolume() > 0){
        music.getBackground_music().setVolume(0);
        }
        else
            music.getBackground_music().setVolume(.5f);
    }

    private void setMusicOn(){
        final GameSound music = app.getStateManager().getState(GameSound.class);
        music.getBackground_music().play();
        music.getBackground_music().setVolume(.5f);
    }

    private void setMusicOff(){
        final GameSound music = app.getStateManager().getState(GameSound.class);
        music.getBackground_music().setVolume(0);
    }

    /**
     * Navigates the droid to the first hit of the floor.
     */
    private void navigate() {
        final Vector3f origin = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0.0f);
        final Vector3f direction = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        final Ray ray = new Ray(origin, direction);
        final CollisionResults results = new CollisionResults();
        getGameState().getItemNode().collideWith(ray, results);
        for (int i = 0; i < results.size(); i++) {
            final CollisionResult collision = results.getCollision(i);
            // look for the first hit of the floor
            if (collision.getGeometry().getName().equals(GameState.FLOOR_NAME)) {
                navigateTo(viewToModel(collision.getContactPoint()));
                return;
            }
        }
    }

    /**
     * Uses the DroidsNavigator for computing an optimal, collision-free path to the specified position in
     * a separate worker thread. The path is retrieved by the {@linkplain #update(float)} method as soon
     * as it will have been computed and then passed to the droid.
     *
     * @param target position where to go
     */
    private void navigateTo(Position target) {
        if (futurePath != null)
            LOGGER.log(Level.WARNING, "There is still a path search running."); //NON-NLS
        else {
            LOGGER.log(Level.INFO, "Navigating to ({0}|{1})", target.getX(), target.getY());  //NON-NLS
            final var navigator = getDroid().getNavigator();
            futurePath = getDroid().getModel().getExecutor().submit(() -> navigator.findPathTo(target));
        }
    }

    /**
     * Gets the game state from the app.
     *
     * @return game state
     */
    private GameState getGameState() {
        return app.getStateManager().getState(GameState.class);
    }

    /**
     * Gets the current Droid.
     *
     * @return droid
     */
    private Droid getDroid() {
        return getGameState().getModel().getDroidsMap().getDroid();
    }

    /**
     * Called to update the AppState. This method will be called every render pass if the AppState is both attached and enabled.
     *
     * @param tpf Time since the last call to update(), in seconds.
     */
    @Override
    public void update(float tpf) {
        super.update(tpf);
        // Check whether a path has been computed after navigateTo(Position) had been called
        if (futurePath != null && futurePath.isDone()) {
            try {
                final List<Segment> newPath = futurePath.get();
                LOGGER.log(Level.TRACE, "found path {0}", newPath); //NON-NLS
                if (newPath.isEmpty())
                    LOGGER.log(Level.DEBUG, "no path found"); //NON-NLS
                // pass the computed path to the droid
                getDroid().setPath(newPath);
            }
            catch (ExecutionException e) {
                LOGGER.log(Level.WARNING, "When calling futurePath.get()", e); //NON-NLS
            }
            catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted!", e); //NON-NLS
                Thread.currentThread().interrupt();
            }
            futurePath = null;
        }
    }
}

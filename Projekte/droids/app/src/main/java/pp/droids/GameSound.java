package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import pp.droids.model.DamageReceiver;
import pp.droids.model.DroidsModel;
import pp.droids.model.Enemy;
import pp.droids.model.Item;
import pp.droids.model.Projectile;
import pp.droids.model.Shooter;
import pp.droids.notifications.GameEventAdapter;
import pp.util.Prefs;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.prefs.Preferences;

/**
 * <code>GameInput</code> extends the class {@link com.jme3.app.state.AbstractAppState}.
 * <p>
 * This class handles the sound of the app.
 */
public class GameSound extends AbstractAppState {
    private static final Logger LOGGER = System.getLogger(GameSound.class.getName());
    private static final Preferences PREFS = Prefs.getPreferences(GameSound.class);
    private static final String ENABLED_PREF = "enabled"; //NON-NLS

    /** background_music initalize AudioNode for background music */
    private AudioNode background_music;
    private DroidsApp app;
    private AudioNode gunSound;
    private AudioNode killedSound;
    private AudioNode hitSound;



    /**
     * Shows, if sound is enabled in the preferences.
     *
     * @return boolean, if sound is enabled
     */
    public static boolean enabledInPreferences() {
        return PREFS.getBoolean(ENABLED_PREF, true);
    }

    /**
     * Turns the sound on or off.
     * It overrides {@link com.jme3.app.state.AbstractAppState#setEnabled(boolean)}
     *
     * @param enabled activate the AppState or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        LOGGER.log(Level.INFO, "Sound enabled: {0}", enabled); //NON-NLS
        PREFS.put(ENABLED_PREF, String.valueOf(enabled));
    }

    /**
     * Initializes different sounds.
     * It overrides {@link com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)}
     * <p>
     * Backgroundmusic wird definiert, die Lautstärke auf eins gesetzt, die Audiodatei wird dauerhaft wiederholt und sie soll Global hörbar sein.
     *
     * @param stateManager The state manager
     * @param app          The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        background_music = loadSound("Sound/Music/PIRATES.wav");//7b step1: background music eingefügt/definiert
        background_music.setLooping(true);
        background_music.setVolume(1);
        background_music.setPositional(false);
        background_music.play();
        gunSound = loadSound("Sound/Effects/Gun.wav"); //NON-NLS
        killedSound = loadSound("Sound/Effects/killed.wav"); //NON-NLS
        hitSound = loadSound("Sound/Effects/hit.wav"); //NON-NLS
    }
    /**Getter, um die Hintergrundmusik abrufen zu können
     * @return background_music vom Typ AudioNode
     */
    public AudioNode getBackground_music(){
        return background_music;
    }

    /**
     * Loads the sound.
     *
     * @param name name of the sound
     * @return sound from type AudioNode
     */
    private AudioNode loadSound(String name) {
        final AudioNode sound = new AudioNode(app.getAssetManager(), name,
                                              AudioData.DataType.Buffer);
        sound.setLooping(false);
        sound.setPositional(false);
        return sound;
    }

    /**
     * Recognizes fired shots, destroyed enemies and hits. If an event is recognized, it calls a method to play the  particular sound.
     *
     * @param model the droids model
     */
    public void register(DroidsModel model) {
        model.addGameEventListener(new GameEventAdapter() {
            @Override
            public void shooterFired(Shooter shooter, Projectile projectile) {
                if (isEnabled()) gunSound.playInstance();
            }

            @Override
            public void enemyDestroyed(Enemy enemy) {
                if (isEnabled()) killedSound.playInstance();
            }

            @Override
            public void hit(DamageReceiver damaged, Item hittingItem) {
                if (isEnabled()) hitSound.playInstance();
            }
        });
    }
}

package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import pp.droids.model.DroidsModel;
import pp.droids.notifications.GameEventAdapter;
import pp.util.Prefs;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.prefs.Preferences;

/**
 * <code>GameInput</code> extends the class {@link com.jme3.app.state.AbstractAppState}.
 * <p>
 * This class handles the music of the app.
 */
public class GameMusic extends AbstractAppState {
    private static final Logger LOGGER = System.getLogger(GameMusic.class.getName());
    private static final Preferences PREFS = Prefs.getPreferences(GameMusic.class);
    private static final String ENABLED_PREF = "enabled"; //NON-NLS

    /** background_music initalize AudioNode for background music */
    private AudioNode background_music;
    private DroidsApp app;


    /**
     * Shows, if sound is enabled in the preferences.
     *
     * @return boolean, if sound is enabled
     */
    public static boolean enabledInPreferences() {
        return PREFS.getBoolean(ENABLED_PREF, true);
    }

    /**
     * Turns the music on or off.
     * It overrides {@link com.jme3.app.state.AbstractAppState#setEnabled(boolean)}
     *
     * @param enabled activate the AppState or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        muteBackgroundMusic();
        LOGGER.log(Level.INFO, "Music enabled: {0}", enabled); //NON-NLS
        PREFS.put(ENABLED_PREF, String.valueOf(enabled));
    }

    /**
     * Initializes different music tracks.
     * It overrides {@link com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)}
     * <p>
     * Backgroundmusic wird über loadMusic definiert
     *
     * @param stateManager The state manager
     * @param app          The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        background_music = loadMusic("Sound/Music/PIRATES.ogg");
    }

    /**ändert volume von Backgroundmusic auf 0 oder 1 um sie ein- oder auszuschalten
     *
     */
    public void muteBackgroundMusic(){
        if(background_music.getVolume() > 0){
            background_music.setVolume(0);
        }
        else
            background_music.setVolume(1);
    }

    /**
     * Loads the Music. die Lautstärke wird auf eins gesetzt, die Audiodatei wird dauerhaft wiederholt und sie soll Global hörbar sein.
     *
     * @param name name of the music track
     * @return music from type AudioNode
     */
    private AudioNode loadMusic(String name) {
        final AudioNode music = new AudioNode(app.getAssetManager(), name,
                                              AudioData.DataType.Stream);
        music.setLooping(true);
        music.setVolume(1);
        music.setPositional(false);
        music.play();
        return music;
    }

    /**
     * Recognizes nothing. If an event is recognized, it calls a method to play the  particular music.
     *
     * @param model the droids model
     */
    public void register(DroidsModel model) {
        model.addGameEventListener(new GameEventAdapter() {
            //public void background_music(){if (isEnabled()) background_music.play();}

        });
    }


}

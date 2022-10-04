package pp.droids.model;

import pp.droids.model.external.ExternalMap;
import pp.droids.notifications.GameEvent;
import pp.droids.notifications.GameEventListener;
import pp.droids.notifications.MapChangedEvent;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents the game model.
 */
public class DroidsModel {
    /**
     * The logger of this droids model, primary used for debugging
     */
    private static final Logger LOGGER = System.getLogger(DroidsModel.class.getName());

    /**
     * A list of all game event listeners added to this model.
     */
    private final List<GameEventListener> listeners = new ArrayList<>();

    /**
     * The configuration used in this game model.
     */
    private final DroidsConfig config;
    /**
     * The game map.
     */
    private DroidsMap droidsMap;
    /**
     * The winner or null.
     */
    private FlagCaptor winner;
    /**
     * Executor service for multi-threading.
     */
    private ExecutorService executor;

    /**
     * Creates a game model with a default configuration.
     */
    public DroidsModel() {
        this(new DroidsConfig());
    }

    /**
     * Creates a game model.
     *
     * @param config the configuration of this game
     */
    public DroidsModel(DroidsConfig config) {
        this.config = config;
        setDroidsMap(new DroidsMap(this, config.getWidth(), config.getHeight()));
    }

    /**
     * Returns the configuration of this game.
     */
    public DroidsConfig getConfig() {
        return config;
    }

    /**
     * Returns the current game map
     */
    public DroidsMap getDroidsMap() {
        return droidsMap;
    }

    /**
     * Returns the executor service used for multi-threading.
     */
    public ExecutorService getExecutor() {
        if (executor == null)
            executor = Executors.newCachedThreadPool();
        return executor;
    }

    public void shutdown() {
        LOGGER.log(Level.INFO, "called DroidsModel::shutdown"); //NON-NLS
        if (executor != null)
            executor.shutdown();
    }

    /**
     * Sets the specified game map as the current one.
     *
     * @param droidsMap droids map
     */
    public void setDroidsMap(DroidsMap droidsMap) {
        final DroidsMap oldMap = this.droidsMap;
        this.droidsMap = droidsMap;
        winner = null;
        notifyListeners(new MapChangedEvent(oldMap, droidsMap));
    }

    /**
     * Generates and sets a random game map.
     */
    public void loadRandomMap() {
        setDroidsMap(RandomMapGenerator.createMap(this));
    }

    /**
     * Loads a game map from the specified json file and uses it as the current one.
     *
     * @param file json file representing a droids map
     * @throws IOException if any IO error occurs.
     */
    public void loadMap(File file) throws IOException {
        setDroidsMap(ExternalMap.readFromJsonFile(file).toMap(this));
    }

    /**
     * Loads a game map from the specified json stream and uses it as the current one.
     *
     * @param stream json stream representing a droids map
     * @throws IOException if any IO error occurs.
     */
    public void loadMap(InputStream stream) throws IOException {
        setDroidsMap(ExternalMap.readFromJsonStream(stream).toMap(this));
    }

    /**
     * Loads a game map from the specified json String and uses it as the current one.
     *
     * @param string json String representing a droids map
     * @throws IOException if any IO error occurs.
     */
    public void loadMap(String string) throws IOException {
        setDroidsMap(ExternalMap.readFromJsonString(string).toMap(this));
    }

    /**
     * Saves the current game map to the specified file.
     *
     * @param file json file representing where the droids map is written to.
     * @throws IOException if any IO error occurs.
     */
    public void saveMap(File file) throws IOException {
        new ExternalMap(getDroidsMap()).writeToFile(file);
    }

    /**
     * Called once per frame. This method triggers any update of the game model based on the elapsed time.
     *
     * @param deltaTime time in seconds since the last update call
     */
    public void update(float deltaTime) {
        droidsMap.update(deltaTime);
    }

    /**
     * Adds the specified receiver to the list of all event notification subscribers.
     *
     * @param receiver the receiver to add
     * @see #notifyListeners(pp.droids.notifications.GameEvent)
     */
    public void addGameEventListener(GameEventListener receiver) {
        LOGGER.log(Level.DEBUG, "add listener {0}", receiver); //NON-NLS
        listeners.add(receiver);
    }

    /**
     * Removes the specified receiver from the list of all event notification subscribers.
     *
     * @param receiver the game event listener to remove
     * @see #notifyListeners(pp.droids.notifications.GameEvent)
     */
    public void removeGameEventListener(GameEventListener receiver) {
        LOGGER.log(Level.DEBUG, "remove listener {0}", receiver); //NON-NLS
        listeners.remove(receiver);
    }

    /**
     * Notifies every registered
     * {@linkplain pp.droids.notifications.GameEventListener}.
     *
     * @param event The notification event communicated to every registered listener.
     * @see #addGameEventListener(pp.droids.notifications.GameEventListener)
     */
    public void notifyListeners(GameEvent event) {
        for (GameEventListener listener : new ArrayList<>(listeners))
            event.notify(listener);
    }

    /**
     * Returns true if and only if the droid is the winner.
     */
    public boolean isGameLost() {
        return winner != null && winner != getDroidsMap().getDroid();
    }

    /**
     * Returns true if and only if there is a winner, but it is not the droid.
     */
    public boolean isGameWon() {
        return winner == getDroidsMap().getDroid();
    }

    /**
     * Returns true if the game terminated either by winning or losing.
     */
    public boolean isGameOver() {
        return isGameLost() || isGameWon();
    }

    /**
     * Called when a flag captor reaches the exit. If the captor is the
     * winner if it holds a flag.
     */
    public void reachedExit(FlagCaptor captor) {
        if (captor.getCapturedFlag() != null)
            winner = captor;
    }
}

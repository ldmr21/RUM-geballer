package pp.droids.notifications;


/**
 * An interface used for all game events.
 */
public interface GameEvent {

    /**
     * Abstract method to notify the game event listener of any event.
     *
     * @param listener the game event listener
     */
    void notify(GameEventListener listener);
}

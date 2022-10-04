package pp.droids.notifications;

import pp.droids.model.DroidsMap;


/**
 * Event when the map is changed.
 *
 * @param oldMap the map before the change
 * @param newMap the new map
 */
public record MapChangedEvent(DroidsMap oldMap, DroidsMap newMap) implements GameEvent {

    /**
     * Notifies the game event listener of this event.
     *
     * @param listener the game event listener
     */
    @Override
    public void notify(GameEventListener listener) {
        listener.mapChanged(oldMap, newMap);
    }
}

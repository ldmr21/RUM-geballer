package pp.droids.notifications;

import pp.droids.model.DamageReceiver;
import pp.droids.model.Item;

/**
 * Event when an item is hit.
 *
 * @param damaged     the item receiving damage
 * @param hittingItem the item causing damage
 */
public record HitEvent(DamageReceiver damaged, Item hittingItem) implements GameEvent {

    /**
     * Notifies the game event listener of this event.
     *
     * @param listener the game event listener
     */
    @Override
    public void notify(GameEventListener listener) {
        listener.hit(damaged, hittingItem);
    }
}

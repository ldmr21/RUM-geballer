package pp.droids.notifications;

import pp.droids.model.Enemy;

/**
 * Event when an enemy gets destroyed.
 *
 * @param enemy the destroyed enemy
 */
public record EnemyDestroyedEvent(Enemy enemy) implements GameEvent {

    /**
     * Notifies the game event listener of this event.
     *
     * @param listener the game event listener
     */
    @Override
    public void notify(GameEventListener listener) {
        listener.enemyDestroyed(enemy);
    }
}

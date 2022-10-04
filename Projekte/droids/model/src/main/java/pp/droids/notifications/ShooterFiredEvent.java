package pp.droids.notifications;

import pp.droids.model.Projectile;
import pp.droids.model.Shooter;

/**
 * Event when a shooter fired a projectile.
 *
 * @param shooter    the shooter that fired
 * @param projectile the fired projectile
 */
public record ShooterFiredEvent(Shooter shooter, Projectile projectile) implements GameEvent {

    /**
     * Notifies the game event listener of this event.
     *
     * @param listener the game event listener
     */
    @Override
    public void notify(GameEventListener listener) {
        listener.shooterFired(shooter, projectile);
    }
}

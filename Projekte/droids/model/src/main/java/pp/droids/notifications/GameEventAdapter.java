package pp.droids.notifications;

import pp.droids.model.DamageReceiver;
import pp.droids.model.DroidsMap;
import pp.droids.model.Enemy;
import pp.droids.model.Item;
import pp.droids.model.Projectile;
import pp.droids.model.Shooter;

/**
 * Listener class for all events implemented by subclasses of {@linkplain GameEvent} implementing {@link pp.droids.notifications.GameEventListener}.
 */
public class GameEventAdapter implements GameEventListener {

    /**
     * Indicates that the game map has changed
     */
    @Override
    public void mapChanged(DroidsMap oldMap, DroidsMap newMap) { /* empty implementation */ }

    /**
     * Indicates that the droid has fired
     */
    @Override
    public void shooterFired(Shooter shooter, Projectile projectile) { /* empty implementation */}

    /**
     * Indicates that an enemy has been destroyed
     */
    @Override
    public void enemyDestroyed(Enemy enemy) { /* empty implementation */}

    /**
     * Indicates that an item has been hit.
     */
    @Override
    public void hit(DamageReceiver damaged, Item hittingItem) { /* empty implementation */}

    @Override
    public void background_music(){}
}

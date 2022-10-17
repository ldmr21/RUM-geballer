package pp.droids.notifications;

import pp.droids.model.DamageReceiver;
import pp.droids.model.DroidsMap;
import pp.droids.model.Enemy;
import pp.droids.model.Item;
import pp.droids.model.Projectile;
import pp.droids.model.Shooter;

/**
 * Listener interface for all events implemented by subclasses of {@linkplain GameEvent}.
 */
public interface GameEventListener {
    /**
     * Indicates that the game map has changed
     *
     * @param oldMap the map before the change
     * @param newMap the map after the change
     */
    void mapChanged(DroidsMap oldMap, DroidsMap newMap);

    /**
     * Indicates that the droid has fired
     *
     * @param shooter    the item shooting
     * @param projectile the item shot
     */
    void shooterFired(Shooter shooter, Projectile projectile);

    /**
     * Indicates that an enemy has been destroyed
     * @param enemy the destroyed enemy
     */
    void enemyDestroyed(Enemy enemy);

    /**
     * Indicates that an item has been hit.
     * @param damaged the item receiving damage
     * @param hittingItem the item giving damage
     */
    void hit(DamageReceiver damaged, Item hittingItem);

    void background_music();
}

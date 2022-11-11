package pp.npc;

import pp.util.Position;
import pp.util.Segment;
import pp.util.map.Observation;
import pp.util.navigation.Navigable;

import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * The interface of any non-player character (NPC).
 */
public interface NonPlayerCharacter extends Navigable<Segment> {

    /**
     * Sets the position of a npc.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    void setPos(float x, float y);

    /**
     * Sets the position of a npc.
     *
     * @param pos a position object
     */
    default void setPos(Position pos) {
        setPos(pos.getX(), pos.getY());
    }

    /**
     * Returns the rotation of a npc.
     */
    float getRotation();

    /**
     * returns X coordinate of the npc
     */
    float getX();

    /**
     * returns Y coordinate of the npc
     */
    float getY();

    /**
     * Sets the rotation of a npc.
     *
     * @param rotation the rotation the npc will be given
     */
    void setRotation(float rotation);

    /**
     * Returns the lives of the NPC.
     */
    int getLives();

    /**
     * Returns the number of collected coins of the NPC.
     */
    int getCoins();

    /**
     * Returns whether the NPC is currently carrying the flag
     */
    boolean hasFlag();

    /**
     * Returns the time since a npc was last hit by a projectile of any type.
     */
    float getTimeSinceLastHit();

    /**
     * Indicates whether a npc collides with any other item
     *
     * @return true, if a collision happens
     */
    boolean collidesWithAnyOtherItem();

    /**
     * Returns what this item can observe from its current position and looking
     * its way. Only items that a droid etc. can collide with and additionally
     * those whose category is contained in the specified set are visible.
     * The observation encapsulates a sequence of triangles
     * ({@linkplain pp.util.map.Triangle}). Each triangle represents any other
     * item or any wall,depending on the specified of type of segments that shall
     * be observed.
     *
     * @param visible a set of categories that controls which items shall be considered visible,
     *                additionally to all those items a droid etc. would collide with.
     * @return the observation of this item.
     */
    Observation getObservation(Set<String> visible);

    /**
     * Function is called when a npc shoots a projectile.
     */
    void fire();

    /**
     * Indicates whether a npc is reloading their weapon.
     *
     * @return true, if npc is currently reloading
     */
    boolean isReloading();

    /**
     * Returns the name of the level where the NPC is.
     */
    String getLevelName();


    /**
     * Returns the executor service used for multi-threading.
     */
    ExecutorService getExecutor();
}

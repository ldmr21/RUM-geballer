package pp.npc;

import pp.util.Position;
import pp.util.Segment;
import pp.util.map.Observation;
import pp.util.map.ObservationMap;
import pp.util.map.Triangle;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import static pp.util.Angle.normalizeAngle;
import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatMath.PI;
import static pp.util.FloatMath.atan2;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * This class implements the behavior of a non-player character.
 */
public class NonPlayerCharacterBehavior {

    /**
     * The categories of all entities that are collected in observation maps.
     */
    private final Set<String> MAP_CATEGORIES = Set.of("WALL", "COIN", "FLAG", "EXIT",
                                                             "OBSTACLE", "OUTER_WALL");

    /**
     * the categories of movable entities that are collected in observation maps.
     */
    private final Set<String> MOVING_CATEGORIES = Set.of("ENEMY", "DROID");

    /**
     * The navigation path. The droid will follow this path to its end
     * as long as it doesn't collide on its way. Following the path is immediately
     * stopped if the droid's movement is controlled "manually".
     */
    private final List<Position> path = new LinkedList<>();

    private Future<List<Segment>> futurePath;

    /**
     * The movement speed of the npc.
     */
    private final float MOVE_SPEED = 2.5f;

    /**
     * The turn speed of the npc.
     */
    private final float TURN_SPEED = 1.5f;

    /**
     * The npc whose behavior is implemented.
     */
    private final NonPlayerCharacter npc;

    /**
     * A private enum to represent the different movement states of the npc.
     */
    private enum MoveState {FORWARD, STOP}

    /**
     * A private enum to represent the different turn states of the npc.
     */
    private enum TurnState {LEFT, RIGHT, STOP}

    /**
     * Maps each level to an ObservationMap of its own.
     */
    private final Map<String, ObservationMap> observationMap = new HashMap<>();


    /**
     * Creates a new npc-behavior
     *
     * @param npc the npc to use the behavior
     */
    public NonPlayerCharacterBehavior(NonPlayerCharacter npc) {
        this.npc = npc;
    }

    /**
     * the starting movement state of the npc
     */
    private MoveState moveState = MoveState.STOP;

    /**
     * the starting turn state of the npc
     */
    private TurnState turnState = TurnState.STOP;

    /**
     * the latest observation of the npc
     */
    private Observation latestObservation;

    /**
     * Returns the movement speed of the npc depending on the movement state.
     * @return the movement speed of the npc
     */
    private float getMoveSpeed(){
        return switch (moveState) {
            case FORWARD -> MOVE_SPEED;
            case STOP -> 0;
        };
    }

    /**
     * Returns the turn speed of the npc depending on the turn state.
     * @return the turn speed of the npc
     */
    private float getTurnSpeed(){
        return switch (turnState) {
            case LEFT -> -TURN_SPEED;
            case RIGHT -> TURN_SPEED;
            case STOP -> 0;
        };
    }

    /**
     * Specifies the actual behavior.
     *
     * @param delta time in seconds since the last update
     */
    public void update(float delta) {
        updatePosition(delta);
        observe();
        search();
    }

    private void search(){
        if(seesDroid()) {
            npc.fire();
        }
    }

    private boolean seesDroid(){
        for (Triangle triangle : latestObservation.getTriangles()) {
            if(triangle.getEntity() == null){
                continue;
            }
            if (triangle.getEntity().cat().equals("DROID")) {
                return true;
            }
        }
        return false;
    }


    private void navigateTo(Position target){
        if (futurePath == null || futurePath.isDone()) {
            final var navigator = npc.getNavigator();
            futurePath = npc.getExecutor().submit(() -> navigator.findPathTo(target));
        }
    }

    /**
     * Updates the position of the npc.
     *
     * @param delta time in seconds since the last update
     */
    private void updatePosition(float delta) {
        if(npc.getX() < 0){
            npc.setPos(0 ,npc.getY());
        }
        if(npc.getY() < 0){
            npc.setPos(npc.getX(), 0);
        }
        if(getMoveSpeed() != 0){
            setPosAvoidingCollisions(npc.getX() + getMoveSpeed() * delta * cos(npc.getRotation()),
                    npc.getY() + getMoveSpeed() * delta * sin(npc.getRotation()));
        }
        if(getTurnSpeed() != 0){
            npc.setRotation(npc.getRotation() + getTurnSpeed() * delta);
        }

    }

    /**
     * Sets the droid to the specified position if it doesn't collide with anything
     * else there. The droid is not moved if there were a collision.
     *
     * @param newX x-coordinate of the new position
     * @param newY y-coordinate of the new position
     * @return true if the droid has been moved to the new position
     */
    private boolean setPosAvoidingCollisions(float newX, float newY) {
        final float oldX = npc.getX();
        final float oldY = npc.getY();
        npc.setPos(newX, newY);
        if (npc.collidesWithAnyOtherItem()) {
            npc.setPos(oldX, oldY);
            return false;
        }
        return true;
    }

    /**
     * Sets the droid to the specified position if it doesn't collide with anything
     * else there. The droid is not moved if there were a collision.
     *
     * @param p the new position
     * @return true if the droid has been moved to the new position
     * @see #setPosAvoidingCollisions(float, float)
     */
    private boolean setPosAvoidingCollisions(Position p) {
        return setPosAvoidingCollisions(p.getX(), p.getY());
    }

    /**
     * Coordinates the following of a predefined path.
     */
    private float followPath(float delta) {
        final Position target = path.get(0);
        if (npc.distanceTo(target) < FLT_EPSILON) {
            setPosAvoidingCollisions(target);
            path.remove(0);
            return delta;
        }

        final float bearing = atan2(target.getY() - npc.getY(),
                                    target.getX() - npc.getX());
        float needToTurnBy = normalizeAngle(bearing - npc.getRotation());
        // we need to turn the droid such that its rotation coincides with the bearing of the next path point
        if (Math.abs(needToTurnBy) >= delta * TURN_SPEED) {
            // we are turning during the rest of this time slot
            npc.setRotation(npc.getRotation() + Math.signum(needToTurnBy) * delta * TURN_SPEED);
            return 0f;
        }

        // we first turn the droid
        npc.setRotation(bearing);
        // and there is some time left in this time slot
        delta -= Math.abs(needToTurnBy) / TURN_SPEED;
        final float distanceToGo = npc.distanceTo(target);
        if (distanceToGo >= delta * MOVE_SPEED) {
            // we do not reach the next path point in this time slot
            final float newX = npc.getX() + MOVE_SPEED * delta * cos(npc.getRotation());
            final float newY = npc.getY() + MOVE_SPEED * delta * sin(npc.getRotation());
            if (!setPosAvoidingCollisions(newX, newY))
                path.clear();
            return 0f;
        }

        // we have reached the next path point in this time slot
        if (!setPosAvoidingCollisions(target)) {
            path.clear();
            return 0f;
        }
        path.remove(0);
        // and there is some time left in this time slot
        return delta - distanceToGo / MOVE_SPEED;
    }

    /**
     * Returns the path the droid shall follow.
     * The path is set by {@linkplain #setPath(java.util.List)}.
     */
    public List<Position> getPath() {
        return Collections.unmodifiableList(path);
    }

    /**
     * Sets a navigation path. The droid will then follow this path to its end
     * as long as it doesn't collide on its way. Following the path is immediately
     * stopped if the droid's movement is controlled "manually".
     *
     * @param newPath the path the droid shall follow
     */
    public void setPath(List<Segment> newPath) {
        path.clear();
        newPath.stream().map(Segment::to).forEach(path::add);
    }

    private ObservationMap getMap(){
        return observationMap.computeIfAbsent(npc.getLevelName(), l -> new ObservationMap(MAP_CATEGORIES));
    }

    private void observe() {
        latestObservation = npc.getObservation(MOVING_CATEGORIES);
        latestObservation.getTriangles().forEach(getMap()::add);
    }
}

package pp.droids.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import pp.util.Position;
import pp.util.Segment;
import pp.util.map.Observation;
import pp.util.map.ObservationMap;
import pp.util.navigation.Navigable;
import pp.util.navigation.Navigator;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pp.util.Angle.normalizeAngle;
import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatMath.PI;
import static pp.util.FloatMath.atan2;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * Represents a droid
 */
public class Droid extends Shooter implements Navigable<Segment>, Debugee {


    /**
     * A private enum containing states for moving forward, backwards or not moving.
     */
    private enum ForwardState {STOP, FORWARD, BACKWARD}

    /**
     * A private enum containing states for side stepping left, right or not moving.
     */
    private enum SidestepState {STOP, LEFT, RIGHT}

    /**
     * A private enum containing states for moving left, right or not moving.
     */
    private enum TurnState {STOP, LEFT, RIGHT}

    /**
     * The standard bounding radius of a droid.
     */
    public static final float BOUNDING_RADIUS = .45f;

    /**
     * The turn speed of the droid in radians per second.
     */
    private static final float TURN_SPEED = 3.5f;

    /**
     * The forward speed of the droid in length units per second.
     */
    private static final float FORWARD_SPEED = 4f;

    /**
     * The side speed of the droid in length units per second.
     */
    private static final float SIDE_SPEED = 4f;

    /**
     * The categories of all entities that are collected in observation maps.
     */
    private static final Set<String> MAP_CATEGORIES = Set.of(Category.WALL, Category.OUTER_WALL,
                                                             Category.OBSTACLE, Category.EXIT);
    /**
     * The categories that are contained in observations,
     * additionally to all those items a droid etc. would collide with.
     *
     * @see BoundedItem#getObservation(java.util.Set)
     */
    private static final Set<String> CAPTURING_CATS = Set.of(Category.FLAG, Category.EXIT);

    /**
     * The navigation path. The droid will follow this path to its end
     * as long as it doesn't collide on its way. Following the path is immediately
     * stopped if the droid's movement is controlled "manually".
     */
    private final List<Position> path = new LinkedList<>();

    /**
     * The current turn state of the droid.
     */
    private TurnState turnState;

    /**
     * The current forward state of the droid.
     */
    private ForwardState forwardState;

    /**
     * The current sidestep state of the droid.
     */
    private SidestepState sidestepState;

    /**
     * Maps each level to an ObservationMap of its own.
     */
    private final Map<MapLevel, ObservationMap> observationMap = new HashMap<>();

    /**
     * The latest observation by the droid
     */
    private Observation latestObservation;

    /**
     * Creates a droid.
     *
     * @param model          the game model
     * @param boundingRadius the size of this item in terms of the radius of its bounding circle
     * @param initialLives   the number of lives that this item initially has
     * @param reloadTime     the time in seconds that must pass before this item can shoot again
     */
    public Droid(DroidsModel model, float boundingRadius, int initialLives, float reloadTime) {
        super(model, boundingRadius, initialLives, reloadTime);
        resetState();
    }

    /**
     * Creates a droid with standard parameters and variable numbers of lives.
     *
     * @param model the game model that has this droid.
     */

    public Droid(DroidsModel model) {
        this(model, BOUNDING_RADIUS, model.getConfig().getDroidLives(), STANDARD_RELOAD_TIME);
    }

    /**
     * Returns the category of an obstacle.
     *
     * @see pp.droids.model.Category
     */
    @Override
    public String cat() {
        return Category.CHARACTER;
    }

    /**
     * Sets forward state, turn state and side step state of the droid to stop.
     */
    private void resetState() {
        forwardState = ForwardState.STOP;
        turnState = TurnState.STOP;
        sidestepState = SidestepState.STOP;
    }

    /**
     * Handles a forward command.
     */
    public void goForward() {
        forwardState = switch (forwardState) {
            case FORWARD, STOP -> ForwardState.FORWARD;
            case BACKWARD -> ForwardState.STOP;
        };
    }

    /**
     * Handles a backward command.
     */
    public void goBackward() {
        forwardState = switch (forwardState) {
            case BACKWARD, STOP -> ForwardState.BACKWARD;
            case FORWARD -> ForwardState.STOP;
        };
    }

    /**
     * Handles a step left command.
     */
    public void stepLeft(){
        sidestepState = switch(sidestepState){
            case LEFT, STOP -> SidestepState.LEFT;
            case RIGHT -> SidestepState.STOP;
        };
    }

    /**
     * Handles a step right command.
     */
    public void stepRight(){
        sidestepState = switch(sidestepState){
            case RIGHT, STOP -> SidestepState.RIGHT;
            case LEFT -> SidestepState.STOP;
        };
    }

    /**
     * Handles a turn left command.
     */
    public void turnLeft() {
        turnState = switch (turnState) {
            case LEFT, STOP -> TurnState.LEFT;
            case RIGHT -> TurnState.STOP;
        };
    }

    /**
     * Handles a turn right command.
     */
    public void turnRight() {
        turnState = switch (turnState) {
            case RIGHT, STOP -> TurnState.RIGHT;
            case LEFT -> TurnState.STOP;
        };
    }

    /**
     * Called once per frame. The method updates the droid position depending on the elapsed time passed
     * as the argument.
     *
     * @param delta time in seconds since the last update call
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        updateMovement(delta);
        observe();
        resetState();
    }

    /**
     * Returns the specific forward speed of the droid.
     */
    private float getSpeed() {
        return switch (forwardState) {
            case FORWARD -> FORWARD_SPEED;
            case BACKWARD -> -FORWARD_SPEED;
            case STOP -> 0f;
        };
    }

    /**
     * Returns the specific side speed of the droid.
     */
    private float getSideSpeed() {
        return switch (sidestepState) {
            case RIGHT -> SIDE_SPEED;
            case LEFT -> -SIDE_SPEED;
            case STOP -> 0f;
        };
    }

    /**
     * Returns the specific turn speed of the droid.
     */
    private float getTurnSpeed() {
        return switch (turnState) {
            case LEFT -> TURN_SPEED;
            case RIGHT -> -TURN_SPEED;
            case STOP -> 0f;
        };
    }

    /**
     * Actually moves the droid
     * Die Methode kann nun die SideStep-Bewegung berechnen und den Droid bewegen.
     *
     * @param delta time in seconds since the last update call
     */
    private void updateMovement(float delta) {
        if (getSpeed() != 0f) {
            setPosAvoidingCollisions(getX() + getSpeed() * delta * cos(getRotation()),
                                     getY() + getSpeed() * delta * sin(getRotation()));
            path.clear();
        }
        if (getSideSpeed() != 0f) {
            setPosAvoidingCollisions(getX() + getSideSpeed() * delta * cos(getRotation() - PI/2),
                                     getY() + getSideSpeed() * delta * sin(getRotation() - PI/2));
            path.clear();
        }
        if (getTurnSpeed() != 0f) {
            setRotation(getRotation() + getTurnSpeed() * delta);
            path.clear();
        }
        // as long as there is a path to follow and this time slot has still some time left
        while (delta > 0f && !path.isEmpty())
            delta = followPath(delta);
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
     * Sets the droid to the specified position if it doesn't collide with anything
     * else there. The droid is not moved if there were a collision.
     *
     * @param newX x-coordinate of the new position
     * @param newY y-coordinate of the new position
     * @return true if the droid has been moved to the new position
     * @see #setPosAvoidingCollisions(pp.util.Position)
     */
    private boolean setPosAvoidingCollisions(float newX, float newY) {
        final float oldX = getX();
        final float oldY = getY();
        setPos(newX, newY);
        if (collidesWithAnyOtherItem()) {
            setPos(oldX, oldY);
            return false;
        }
        return true;
    }

    /**
     * Coordinates the following of a predefined path.
     */
    private float followPath(float delta) {
        final Position target = path.get(0);
        if (distanceTo(target) < FLT_EPSILON) {
            // we are so close... let's jump to the next path point
            setPosAvoidingCollisions(target);
            path.remove(0);
            return delta;
        }

        final float bearing = atan2(target.getY() - getY(),
                                    target.getX() - getX());
        float needToTurnBy = normalizeAngle(bearing - getRotation());
        // we need to turn the droid such that its rotation coincides with the bearing of the next path point
        if (Math.abs(needToTurnBy) >= delta * TURN_SPEED) {
            // we are turning during the rest of this time slot
            setRotation(getRotation() + Math.signum(needToTurnBy) * delta * TURN_SPEED);
            return 0f;
        }

        // we first turn the droid
        setRotation(bearing);
        // and there is some time left in this time slot
        delta -= Math.abs(needToTurnBy) / TURN_SPEED;
        final float distanceToGo = distanceTo(target);
        if (distanceToGo >= delta * FORWARD_SPEED) {
            // we do not reach the next path point in this time slot
            final float newX = getX() + FORWARD_SPEED * delta * cos(getRotation());
            final float newY = getY() + FORWARD_SPEED * delta * sin(getRotation());
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
        return delta - distanceToGo / FORWARD_SPEED;
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

    /**
     * Returns a navigator that can be used to compute an optimal path for this
     * item to any postion.
     *
     * @return a new navigator
     */
    public Navigator<Segment> getNavigator() {
        return new DroidsNavigator(this, FORWARD_SPEED, TURN_SPEED);
    }

    /**
     * Returns the ObservationMap for the level where the droid currently is.
     */
    @Override
    public ObservationMap getMap() {
        return observationMap.computeIfAbsent(getLevel(), l -> new ObservationMap(MAP_CATEGORIES));
    }

    /**
     * Returns the latest observation by the droid.
     */
    @Override
    public Observation getLatestObservation() {
        return latestObservation;
    }

    /**
     * This method lets the droid observe its surrounding in its current level.
     * Results can be obtained by {@linkplain #getObservation()} and
     * {@linkplain #getMap()}.
     */
    private void observe() {
        latestObservation = getObservation(CAPTURING_CATS);
        latestObservation.getTriangles().forEach(getMap()::add);
    }

    /**
     * Accept method of the visitor pattern.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    /**
     * Accept method of the {@link pp.droids.model.VoidVisitor}.
     */
    @Override
    public void accept(VoidVisitor v) {
        v.visit(this);
    }

}

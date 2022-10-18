package pp.droids.model;

import pp.util.Position;
import pp.util.Segment;
import pp.util.map.Observation;
import pp.util.map.ObservationMap;
import pp.util.navigation.Navigator;

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

public class Dog extends BoundedItem{

    /**
     * A private enum containing states for searching or following.
     */
    private enum FollowState {STOP, FOLLOW}

    /**
     * A private enum containing states for moving left, right or not moving.
     */
    private enum TurnState {STOP, LEFT, RIGHT}

    /**
     * The turn speed of the droid in radians per second.
     */
    private static final float TURN_SPEED = 3.5f;

    /**
     * The forward speed of the droid in length units per second.
     */
    private static final float FORWARD_SPEED = 4f;

    /**
     * The categories of all entities that are collected in observation maps.
     */
    private static final Set<String> MAP_CATEGORIES = Set.of(Category.WALL, Category.OUTER_WALL,
                                                             Category.OBSTACLE, Category.EXIT, Category.CHARACTER);

    /**
     * The categories that are contained in observations,
     * additionally to all those items a droid etc. would collide with.
     *
     * @see BoundedItem#getObservation(java.util.Set)
     */
    private static final Set<String> FOLLOWING_CATS = Set.of(Category.CHARACTER);

    /**
     * Maps each level to an ObservationMap of its own.
     */
    private Map<MapLevel, ObservationMap> observationMap = new HashMap<>();

    /**
     * The navigation path. The dog will follow this path to its end
     * as long as it doesn't collide on its way.
     */
    private final List<Position> path = new LinkedList<>();

    private static final float BOUNDING_RADIUS = 0.4f;


    private TurnState turnState;
    private FollowState followState;

    /**
     * The latest observation by the dog
     */
    private Observation latestObservation;


    public Dog(DroidsModel model) {
        super(model, BOUNDING_RADIUS);
        resetState();
    }

    @Override
    public float getX() {
        return super.getX();
    }

    @Override
    public float getY() {
        return super.getY();
    }

    @Override
    public MapLevel getLevel() {
        // flag follows captor
        return super.getLevel();
    }

    /**
     * Returns the category of the dog.
     *
     * @see pp.droids.model.Category
     */
    @Override
    public String cat() {return Category.DOG;}

    public void clearObservationMap(){
        observationMap = new HashMap<>();
    }

    /**
     * Sets following state of the dog to stop.
     */
    public void resetState() {
        followState = FollowState.STOP;
        turnState = TurnState.STOP;
    }

    /**
     * Handles a forward command.
     */
    public void switchFollowState() {
        followState = switch (followState) {
            case STOP -> FollowState.FOLLOW;
            case FOLLOW -> FollowState.STOP;
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
     * Returns the specific forward speed of the droid.
     */
    private float getSpeed() {
        return switch (followState) {
            case FOLLOW -> FORWARD_SPEED;
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

    @Override
    public void update(float delta) {
        updateMovement(delta);
        observe();
        resetState();
    }

    /**
     * Actually moves the dog
     *
     * @param delta time in seconds since the last update call
     */
    private void updateMovement(float delta) {
        if (getSpeed() != 0f) {
            setPosAvoidingCollisions(getX() + getSpeed() * delta * cos(getRotation()),
                                     getY() + getSpeed() * delta * sin(getRotation()));
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
    public ObservationMap getMap() {
        return observationMap.computeIfAbsent(getLevel(), l -> new ObservationMap(MAP_CATEGORIES));
    }


    /**
     * Returns the latest observation by the droid.
     */
    public Observation getLatestObservation() {
        return latestObservation;
    }


    /**
     * This method lets the droid observe its surrounding in its current level.
     * Results can be obtained by {@linkplain #getObservation()} and
     * {@linkplain #getMap()}.
     */
    private void observe() {
        latestObservation = getObservation(FOLLOWING_CATS);
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

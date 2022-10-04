package pp.droids.model;

import pp.droids.model.collisions.CollisionPredicate;
import pp.util.FloatMath;
import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.Segment;
import pp.util.navigation.AbstractNavigator;
import pp.util.navigation.Navigator;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pp.util.Angle.normalizeAngle;

/**
 * A class that allows to search for an optimal path between two points in the {@linkplain pp.droids.model.DroidsMap}.
 * Note that the search space does not consist of just points in the plane, but rather of line segments between
 * points in the plane. That way, a path is a sequence of segments, and costs for walking and turning the
 * droid can be computed as well as minimized using the A* algorithm.
 */
class DroidsNavigator extends AbstractNavigator<Segment> implements Navigator<Segment> {

    /**
     * The logger of the droids' navigator, primary used for debugging.
     */
    private static final Logger LOGGER = System.getLogger(DroidsNavigator.class.getName());

    /**
     * Static deviation value to treat rounding errors.
     */
    private static final float EPS2 = 1e-8f;

    /**
     * The item that shall be navigated.
     */
    private final BoundedItem item;

    /**
     * Copy of the item that shall be navigated.
     */
    private final BoundedItem itemCopy;

    /**
     * The target point of the navigation.
     */
    private Position target;
    /**
     * A list of copies of all items that may be in the way
     * when navigating item.
     */
    private final List<Item> itemListCopy;
    private final float turnSpeed;
    private final float forwardSpeed;

    /**
     * Creates an instance of this class to search an optimal (i.e., least expensive) path for an item in the
     * specified map to the specified target point.
     *
     * @param item         the item that shall be navigated to the target position.
     * @param forwardSpeed the item's speed when moving forward
     * @param turnSpeed    the item's speed when turning. A non-positive value means that turning does not take time.
     */
    public DroidsNavigator(BoundedItem item, float forwardSpeed, float turnSpeed) {
        this.forwardSpeed = forwardSpeed;
        this.turnSpeed = turnSpeed;
        this.item = item.copy();
        this.itemCopy = item.copy();
        // Work on a copy of all items that may be in the way
        itemListCopy = new ArrayList<>();
        for (Item it : item.getLevel())
            if (it != item && CollisionPredicate.INSTANCE.test(it))
                itemListCopy.add(it.copy());
    }

    /**
     * Computes a minimal cost path of the item to the specified position. The path is
     * represented by a list of consecutive segments from a start position to the target,
     * or an empty list if there is no such path.
     *
     * @return the path from a start position to an end position if it exists,
     * or an empty list if there is no such path.
     */
    @Override
    public List<Segment> findPathTo(Position target) {
        this.target = target;
        LOGGER.log(Level.TRACE,
                   () -> String.format("look for path from (%f|%f) to (%f|%f)",  //NON-NLS
                                       item.getX(), item.getY(), target.getX(), target.getY()));
        final List<Segment> path = computePath();
        LOGGER.log(Level.TRACE,
                   () -> "found path: " + //NON-NLS
                         path.stream()
                             .map(s -> String.format("[(%f|%f)-(%f|%f)]", //NON-NLS
                                                     s.from().getX(), s.from().getY(),
                                                     s.to().getX(), s.to().getY()))
                             .collect(Collectors.joining(", ")));
        return path;
    }

    private List<Segment> computePath() {
        if (item.distanceSquaredTo(target) < EPS2) // target position is the start position
            return Collections.emptyList();
        return makeOptionalSegment(item, target).map(Collections::singletonList).orElseGet(this::findPath);
    }

    /**
     * Returns the start position of the droids' navigation.
     */
    @Override
    protected Collection<Segment> getStartPositions() {
        // return the empty list if the start and/ord the target
        // position are invalid
        if (isInvalidPosition(item) || collisionAt(item) ||
            isInvalidPosition(target) || collisionAt(target))
            return Collections.emptyList();
        return outgoingSegments(item);
    }

    /**
     * Indicates whether a position is the target position.
     *
     * @param pos the position to check
     * @return true, if the checked and target position are equal
     */
    @Override
    protected boolean isTargetPosition(Segment pos) {
        LOGGER.log(Level.TRACE, "check best position {0}", pos); //NON-NLS
        return pos.to().equals(target);
    }

    /**
     * Computes the cost for a step between two segments.
     *
     * @param prevSegment the position at the beginning of the step (may be null)
     * @param nextSegment the position after the step
     */
    @Override
    protected float costsForStep(Segment prevSegment, Segment nextSegment) {
        final float currentAngle = prevSegment == null ? item.getRotation() : prevSegment.angle();
        return turnCosts(nextSegment.angle() - currentAngle) + walkCosts(nextSegment.length());
    }

    /**
     * Estimates the costs for walking from a given segment to the target segment.
     *
     * @param segment the segment to check
     */
    @Override
    protected float estimateCostsToTarget(Segment segment) {
        final float dx = target.getX() - segment.to().getX();
        final float dy = target.getY() - segment.to().getY();
        return walkCosts(FloatMath.sqrt(dx * dx + dy * dy));
    }

    /**
     * Returns a collection of reachable positions from a given segment.
     *
     * @param segment the segment to check
     */
    @Override
    protected Collection<Segment> reachablePositions(Segment segment) {
        return outgoingSegments(segment.to());
    }

    /**
     * Computes the costs for walking the specified distance.
     *
     * @param distance the length of the walk
     * @return costs for walking
     */
    private float walkCosts(float distance) {
        return distance / forwardSpeed;
    }

    /**
     * Returns the costs for turning the specified angle in degrees.
     *
     * @return costs for turning
     */
    private float turnCosts(float delta) {
        return turnSpeed <= 0f ? 0f : Math.abs(normalizeAngle(delta) / turnSpeed);
    }

    /**
     * Returns all valid outgoing segments of the specified position.
     *
     * @param p a position
     * @return a list of segments
     */
    private Collection<Segment> outgoingSegments(Position p) {
        final List<Segment> outgoing = new ArrayList<>(9);
        final int x = Math.round(p.getX());
        final int y = Math.round(p.getY());
        for (int toX = x - 1; toX <= x + 1; toX++)
            for (int toY = y - 1; toY <= y + 1; toY++)
                makeOptionalSegment(p, new FloatPoint(toX, toY)).ifPresent(outgoing::add);
        makeOptionalSegment(p, target).ifPresent(outgoing::add);
        LOGGER.log(Level.ALL, "outgoing of {0} : {1}", p, outgoing); //NON-NLS
        return outgoing;
    }

    /**
     * Creates a line segment between the specified positions if they have an
     * appropriate distance, and if it describes a movement of the droid that
     * does neither move the droid out of the bound determined by the
     * union of the map and the droid's source and target position nor cause
     * a collision with an enemy or an obstacle.
     * It returns {@linkplain Optional#empty()} otherwise.
     *
     * @param from position where to start
     * @param to   position where to go
     * @return an optional segment or {@linkplain Optional#empty()}
     */
    private Optional<Segment> makeOptionalSegment(Position from, Position to) {
        if (isInvalidPosition(to))
            return Optional.empty();
        final float dx = to.getX() - from.getX();
        final float dy = to.getY() - from.getY();
        final float d = dx * dx + dy * dy;
        final Segment seg = new Segment(from, to);
        // we check collisions only at the segment end point and in its midpoint
        if (d < EPS2 || collision(from, to))
            return Optional.empty();
        else
            return Optional.of(seg);
    }

    /**
     * Checks whether the item, when moved to the specified position, would collide with anything else.
     *
     * @return true, if a collision would happen
     */
    private boolean collisionAt(Position p) {
        itemCopy.setPos(p);
        return itemListCopy.stream().anyMatch(itemCopy::overlapsWith);
    }

    private boolean collision(Position from, Position to) {
        itemCopy.setPos(from);
        return itemListCopy.stream().anyMatch(it -> itemCopy.overlapsWhenMoving(to, it));
    }

    /**
     * Checks whether a given position is valid, i.e., whether the item at
     * this position would be completely within the map borders.
     *
     * @param p the position to check.
     * @return true if position is invalid
     */
    private boolean isInvalidPosition(Position p) {
        final DroidsMap map = item.getModel().getDroidsMap();
        final float r = item.getRadius();
        return p.getX() - r < map.getXMin() ||
               p.getX() + r > map.getXMax() ||
               p.getY() - r < map.getYMin() ||
               p.getY() + r > map.getYMax();
    }
}
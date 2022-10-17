package pp.droids.model.observation;

import pp.droids.model.BoundedItem;
import pp.droids.model.Category;
import pp.droids.model.Dog;
import pp.droids.model.Droid;
import pp.droids.model.DroidsMap;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Item;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.VoidVisitor;
import pp.droids.model.collisions.CollisionPredicate;
import pp.util.CircularEntity;
import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.TypedSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static pp.util.FloatMath.ZERO_TOLERANCE;

/**
 * Visitor for obtaining a collection of segments that represent the current setting
 * from an observing item.
 */
class SegmentCollector implements VoidVisitor {
    /**
     * The observer of the segment collector.
     */
    private final Position observer;

    /**
     * List of all contained segments.
     */
    private final List<TypedSegment> segments = new ArrayList<>();

    private final Map<TypedSegment, CircularEntity> entityMap = new HashMap<>();

    private final Set<String> visible;

    /**
     * Creates a new SegmentCollector that collects segments of items that a droid
     * etc. would collide with and the outer walls of the map. If other segments
     * shall be collected as well, the specified set must contain
     * their categories. All other segments are not collected, i.e., they are effectively
     * invisible.
     *
     * @param observer the observer
     * @param visible  indicates segments by their categories which are potentially visible as well.
     */
    public SegmentCollector(BoundedItem observer, Set<String> visible) {
        this.observer = Objects.requireNonNull(observer);
        this.visible = Objects.requireNonNull(visible);
        addOuterWalls(observer.getModel().getDroidsMap());
    }

    /**
     * Checks whether segments of the specified item are potentially visible to
     * the observer.
     *
     * @param item An item
     * @return true if the segments of this item shall be collected.
     */
    private boolean isVisible(Item item) {
        return CollisionPredicate.INSTANCE.test(item) ||
               visible.contains(item.cat());
    }

    private void addOuterWalls(DroidsMap map) {
        segments.add(wall(map.getXMin(), map.getYMin(), map.getXMin(), map.getYMax()));
        segments.add(wall(map.getXMin(), map.getYMax(), map.getXMax(), map.getYMax()));
        segments.add(wall(map.getXMax(), map.getYMax(), map.getXMax(), map.getYMin()));
        segments.add(wall(map.getXMax(), map.getYMin(), map.getXMin(), map.getYMin()));
    }

    private static TypedSegment wall(float x1, float y1, float x2, float y2) {
        return new TypedSegment(new FloatPoint(x1, y1), new FloatPoint(x2, y2), Category.OUTER_WALL);
    }

    /**
     * Returns the segments that represent (or at least approximate) all other
     * items in the current model.
     */
    public List<TypedSegment> getSegments() {
        return segments;
    }

    public Map<TypedSegment, CircularEntity> getEntityMap() {
        return entityMap;
    }

    /**
     * Adds an approximation of the specified item to the list of all segments.
     *
     * @param item a bounded item
     */
    private void add(BoundedItem item) {
        if (item == observer || item.isDestroyed() || !isVisible(item)) return;
        final float dist = item.distanceTo(observer);
        if (dist <= ZERO_TOLERANCE) return;
        // create a segment from the left to the right border of the item from the observer's view
        final float x = item.getX();
        final float y = item.getY();
        final float factor = item.getRadius() / dist;
        final float dx = (x - observer.getX()) * factor;
        final float dy = (y - observer.getY()) * factor;
        final TypedSegment s = new TypedSegment(new FloatPoint(x - dy, y + dx),
                                                new FloatPoint(x + dy, y - dx),
                                                item.cat());
        segments.add(s);
        entityMap.put(s, item);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Droid}.
     */
    @Override
    public void visit(Droid droid) {
        add(droid);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Obstacle}.
     */
    @Override
    public void visit(Obstacle obstacle) {
        add(obstacle);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Enemy}.
     */
    @Override
    public void visit(Enemy enemy) {
        add(enemy);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Projectile}.
     */
    @Override
    public void visit(Projectile proj) {
        add(proj);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Rocket}.
     */
    @Override
    public void visit(Rocket rocket) {
        add(rocket);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Maze}.
     */
    @Override
    public void visit(Maze maze) {
        if (isVisible(maze))
            segments.addAll(maze.getSegments());
    }

    @Override
    public void visit(Flag flag) {
        // Don't add a segment for a flag if it is captured.
        // Reason: This segment would overlap with the captor's segment.
        if (flag.getCaptor() == null)
            add(flag);
    }

    @Override
    public void visit(Exit exit) {
        add(exit);
    }

    @Override
    public void visit(Dog dog){add(dog);}
}

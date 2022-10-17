package pp.droids.model.collisions;

import pp.droids.model.BoundedItem;
import pp.droids.model.Dog;
import pp.droids.model.Droid;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.Visitor;
import pp.util.Position;
import pp.util.Segment;
import pp.util.SegmentLike;
import pp.util.TypedSegment;

import static pp.util.FloatMath.sqr;

class MoveBoundedItemVisitor implements Visitor<Boolean> {
    private final BoundedItem item;
    private final Position to;

    public MoveBoundedItemVisitor(BoundedItem item, Position to) {
        this.item = item;
        this.to = to;
    }

    private Boolean overlap(BoundedItem other) {
        if (item != other &&
            !item.isDestroyed() &&
            !other.isDestroyed() &&
            SegmentLike.distance(item, to, other) <= item.getRadius() + other.getRadius())
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(Droid droid) {
        return overlap(droid);
    }

    @Override
    public Boolean visit(Obstacle obstacle) {
        return overlap(obstacle);
    }

    @Override
    public Boolean visit(Enemy enemy) {
        return overlap(enemy);
    }

    @Override
    public Boolean visit(Projectile proj) {
        return overlap(proj);
    }

    @Override
    public Boolean visit(Rocket rocket) {
        return overlap(rocket);
    }

    @Override
    public Boolean visit(Maze maze) {
        if (item.isDestroyed() || maze.isDestroyed())
            return Boolean.FALSE;
        final Segment seg = new Segment(item, to);
        final float dist2 = sqr(item.getRadius());
        for (TypedSegment cur : maze.getSegments())
            if (seg.minDistanceSquared(cur) <= dist2)
                return Boolean.TRUE;
        return Boolean.FALSE;
    }


    @Override
    public Boolean visit(Flag flag) {
        return overlap(flag);
    }

    @Override
    public Boolean visit(Exit exit) {
        return overlap(exit);
    }

    @Override
    public Boolean visit(Dog dog) {
        return overlap(dog);
    }
}

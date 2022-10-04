package pp.droids.model.collisions;

import pp.droids.model.BoundedItem;
import pp.droids.model.Droid;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.Visitor;

import static pp.util.FloatMath.sqr;

class BoundedItemOverlapVisitor implements Visitor<Boolean> {
    private final BoundedItem item;

    public BoundedItemOverlapVisitor(BoundedItem item) {
        this.item = item;
    }

    private boolean overlap(BoundedItem other) {
        return item != other &&
               !item.isDestroyed() &&
               !other.isDestroyed() &&
               item.distanceSquaredTo(other) <= sqr(item.getRadius() + other.getRadius());
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
        return MazeOverlapVisitor.overlap(maze, item);
    }


    @Override
    public Boolean visit(Flag flag) {
        return overlap(flag);
    }

    @Override
    public Boolean visit(Exit exit) {
        return overlap(exit);
    }
}

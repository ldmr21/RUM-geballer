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
import pp.util.TypedSegment;

class MazeOverlapVisitor implements Visitor<Boolean> {
    private final Maze maze;

    public static Boolean overlap(Maze maze, BoundedItem other) {
        if (!maze.isDestroyed() && !other.isDestroyed())
            for (TypedSegment cur : maze.getSegments())
                if (cur.distanceTo(other) <= other.getRadius())
                    return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public MazeOverlapVisitor(Maze maze) {
        this.maze = maze;
    }

    @Override
    public Boolean visit(Droid droid) {
        return overlap(maze, droid);
    }

    @Override
    public Boolean visit(Obstacle obstacle) {
        return overlap(maze, obstacle);
    }

    @Override
    public Boolean visit(Enemy enemy) {
        return overlap(maze, enemy);
    }

    @Override
    public Boolean visit(Projectile proj) {
        return overlap(maze, proj);
    }

    @Override
    public Boolean visit(Rocket rocket) {
        return overlap(maze, rocket);
    }

    @Override
    public Boolean visit(Maze maze) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(Flag flag) {
        return overlap(maze, flag);
    }

    @Override
    public Boolean visit(Exit exit) {
        return overlap(maze, exit);
    }

    @Override
    public Boolean visit(Dog dog){return overlap(maze, dog);}
}

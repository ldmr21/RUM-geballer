package pp.droids.model.collisions;

import pp.droids.model.Droid;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.Visitor;

public class OverlapVisitor implements Visitor<Visitor<Boolean>> {
    public static final OverlapVisitor INSTANCE = new OverlapVisitor();

    @Override
    public Visitor<Boolean> visit(Droid droid) {
        return new BoundedItemOverlapVisitor(droid);
    }

    @Override
    public Visitor<Boolean> visit(Obstacle obstacle) {
        return new BoundedItemOverlapVisitor(obstacle);
    }

    @Override
    public Visitor<Boolean> visit(Enemy enemy) {
        return new BoundedItemOverlapVisitor(enemy);
    }

    @Override
    public Visitor<Boolean> visit(Projectile proj) {
        return new BoundedItemOverlapVisitor(proj);
    }

    @Override
    public Visitor<Boolean> visit(Rocket rocket) {
        return new BoundedItemOverlapVisitor(rocket);
    }

    @Override
    public Visitor<Boolean> visit(Maze maze) {
        return new MazeOverlapVisitor(maze);
    }


    @Override
    public Visitor<Boolean> visit(Flag flag) {
        return new BoundedItemOverlapVisitor(flag);
    }

    @Override
    public Visitor<Boolean> visit(Exit exit) {
        return new BoundedItemOverlapVisitor(exit);
    }
}

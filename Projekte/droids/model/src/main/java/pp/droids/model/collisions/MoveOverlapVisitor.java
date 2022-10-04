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
import pp.util.Position;

public class MoveOverlapVisitor implements Visitor<Visitor<Boolean>> {
    private final Position to;

    public MoveOverlapVisitor(Position to) {
        this.to = to;
    }

    @Override
    public Visitor<Boolean> visit(Droid droid) {
        return new MoveBoundedItemVisitor(droid, to);
    }

    @Override
    public Visitor<Boolean> visit(Obstacle obstacle) {
        return new MoveBoundedItemVisitor(obstacle, to);
    }

    @Override
    public Visitor<Boolean> visit(Enemy enemy) {
        return new MoveBoundedItemVisitor(enemy, to);
    }

    @Override
    public Visitor<Boolean> visit(Projectile proj) {
        return new MoveBoundedItemVisitor(proj, to);
    }

    @Override
    public Visitor<Boolean> visit(Rocket rocket) {
        return new MoveBoundedItemVisitor(rocket, to);
    }

    @Override
    public Visitor<Boolean> visit(Maze maze) {
        throw new UnsupportedOperationException("cannot move a maze");
    }


    @Override
    public Visitor<Boolean> visit(Flag flag) {
        return new MoveBoundedItemVisitor(flag, to);
    }

    @Override
    public Visitor<Boolean> visit(Exit exit) {
        return new MoveBoundedItemVisitor(exit, to);
    }
}

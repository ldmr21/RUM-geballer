package pp.droids.model.collisions;

import pp.droids.model.Dog;
import pp.droids.model.Droid;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Item;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.Visitor;

import java.util.function.Predicate;

/**
 * A predicate that is true for all items that can collide with droids etc.
 * if they are close enough.
 */
public class CollisionPredicate implements Visitor<Boolean>, Predicate<Item> {
    /**
     * The only instance of TouchPredicate.
     */
    public static final CollisionPredicate INSTANCE = new CollisionPredicate();

    @Override
    public boolean test(Item item) {
        return item.accept(this);
    }

    private CollisionPredicate() { /* singleton */ }

    @Override
    public Boolean visit(Droid droid) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean visit(Obstacle obstacle) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean visit(Enemy enemy) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean visit(Projectile proj) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(Rocket rocket) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(Maze maze) {
        return Boolean.TRUE;
    }


    @Override
    public Boolean visit(Flag flag) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(Exit exit) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(Dog dog) {
        return Boolean.TRUE;
    }
}

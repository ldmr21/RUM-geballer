package pp.droids.model;

/**
 * Interface for an item visitor following the <em>visitor design pattern</em>.
 */
public interface Visitor<T> {
    /**
     * Convenience method calling item.accept(this)
     */
    default T accept(Item item) {
        return item.accept(this);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Droid}.
     */
    T visit(Droid droid);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Obstacle}.
     */
    T visit(Obstacle obstacle);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Enemy}.
     */
    T visit(Enemy enemy);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Projectile}.
     */
    T visit(Projectile proj);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Rocket}.
     */
    T visit(Rocket rocket);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Maze}.
     */
    T visit(Maze maze);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Flag}.
     */
    T visit(Flag flag);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Exit}.
     */
    T visit(Exit exit);

    T visit(Dog dog);       //7h fuer dog
}

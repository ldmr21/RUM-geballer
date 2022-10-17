package pp.droids.model;

/**
 * Interface for an item visitor following the <em>visitor design pattern</em>.
 */
public interface VoidVisitor {
    /**
     * Convenience method calling item.accept(this)
     */
    default void accept(Item item) {
        item.accept(this);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.Droid}.
     */
    void visit(Droid droid);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Obstacle}.
     */
    void visit(Obstacle obstacle);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Enemy}.
     */
    void visit(Enemy enemy);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Projectile}.
     */
    void visit(Projectile proj);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Rocket}.
     */
    void visit(Rocket rocket);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Maze}.
     */
    void visit(Maze maze);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Flag}.
     */
    void visit(Flag flag);

    /**
     * Visit method for any object of the type {@link pp.droids.model.Exit}.
     */
    void visit(Exit exit);


    void visit(Dog dog);
}

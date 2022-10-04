package pp.droids.model.external;

/**
 * A visitor for external item according to the visitor pattern
 */
interface Visitor {
    /**
     * Convenience method that just calls item.accept(this)
     */
    default void accept(ExternalItem item) {
        item.accept(this);
    }

    void visit(ExternalDroid item);

    void visit(ExternalEnemy item);

    void visit(ExternalObstacle item);

    void visit(ExternalRocket item);

    void visit(ExternalMaze item);

    void visit(ExternalExit item);

    void visit(ExternalFlag item);
}

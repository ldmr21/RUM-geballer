package pp.droids.model;

import pp.util.Position;
import pp.util.TypedSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a maze.
 */
public class Maze extends Item {
    /**
     * List of contained typed segments.
     */
    private final List<TypedSegment> segments = new ArrayList<>();

    /**
     * The length of the maze.
     */
    private final float length;

    /**
     * The height of the maze.
     */
    public static final float HEIGHT = 2.5f;

    /**
     * Creates a maze with the specified polygon defining its walls.
     * The points of the defining polygon must be ordered so that
     * one always has the interior of the walls to the left when
     * walking from point to point along the polygon.
     *
     * @param model  the model
     * @param points the points of the polygon defining the walls
     */
    public Maze(DroidsModel model, List<Position> points) {
        super(model);
        float len = 0f;
        Position from = points.get(points.size() - 1);
        for (Position cur : points) {
            final TypedSegment seg = new TypedSegment(from, cur, cat());
            len += seg.length();
            segments.add(seg);
            from = cur;
        }
        this.length = len;
    }

    @Override
    public String cat() {
        return Category.WALL;
    }

    /**
     * Returns the segments contained in a maze.
     */
    public List<TypedSegment> getSegments() {
        return segments;
    }

    /**
     * Returns the length of a maze.
     */
    public float getLength() {
        return length;
    }

    /**
     * Returns the height of a maze.
     */
    public float getHeight() {
        return HEIGHT;
    }

    /**
     * Updates the maze.
     *
     * @param delta time in seconds since the last update call
     */
    @Override
    public void update(float delta) {
        // do nothing
    }

    /**
     * Accept method of the visitor pattern.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    /**
     * Accept method of the {@link pp.droids.model.VoidVisitor}.
     */
    @Override
    public void accept(VoidVisitor v) {
        v.visit(this);
    }
}

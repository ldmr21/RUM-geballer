package pp.droids.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import pp.droids.model.Item;
import pp.droids.model.Maze;
import pp.util.TypedSegment;

import java.util.Map;

/**
 * External representation of a maze
 */
class ExternalMaze extends ExternalItem {
    /**
     * the array of coordinates specifying the maze walls.
     */
    @JsonProperty
    float[] coords;

    /**
     * Default constructor just for Jackson
     */
    private ExternalMaze() { /* empty */ }

    /**
     * Creates a new external maze based on an existing non-external maze.
     *
     * @param item the existing non-external maze to use.
     */
    ExternalMaze(Maze item, Map<Item, String> idMap) {
        super(item, idMap);
        this.coords = makeCoords(item);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Creates the coordinate array from the specified maze.
     *
     * @param maze the model maze
     */
    private static float[] makeCoords(Maze maze) {
        float[] coords = new float[2 * maze.getSegments().size()];
        int i = 0;
        for (TypedSegment seg : maze.getSegments()) {
            coords[i++] = seg.to().getX();
            coords[i++] = seg.to().getY();
        }
        return coords;
    }
}

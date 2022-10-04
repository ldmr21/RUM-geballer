package pp.droids.model.external;

import pp.droids.model.Item;
import pp.droids.model.Obstacle;

import java.util.Map;

/**
 * External representation of an obstacle.
 */
class ExternalObstacle extends ExternalBoundedItem {
    private ExternalObstacle() { /* default constructor just for Jackson */ }

    /**
     * Creates a new external obstacle based on an existing non-external one.
     *
     * @param item the existing non-external obstacle
     */
    ExternalObstacle(Obstacle item, Map<Item, String> idMap) {
        super(item, idMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

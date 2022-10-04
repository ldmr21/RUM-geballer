package pp.droids.model.external;

import pp.droids.model.Flag;
import pp.droids.model.Item;

import java.util.Map;

/**
 * External representation of a flag.
 */
class ExternalFlag extends ExternalBoundedItem {
    private ExternalFlag() { /* default constructor just for Jackson */ }

    /**
     * Creates a new external medipack based on an existing non-external one.
     *
     * @param item the existing non-external obstacle
     */
    ExternalFlag(Flag item, Map<Item, String> idMap) {
        super(item, idMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

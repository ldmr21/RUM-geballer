package pp.droids.model.external;

import pp.droids.model.Exit;
import pp.droids.model.Item;

import java.util.Map;

/**
 * External representation of an exit
 */
class ExternalExit extends ExternalBoundedItem {
    private ExternalExit() { /* default constructor just for Jackson */ }

    /**
     * Creates a new external gateway based on an existing non-external gateway.
     *
     * @param item the existing non-external maze to use.
     */
    ExternalExit(Exit item, Map<Item, String> idMap) {
        super(item, idMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
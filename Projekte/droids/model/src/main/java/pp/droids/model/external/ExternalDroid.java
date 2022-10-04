package pp.droids.model.external;

import pp.droids.model.Droid;
import pp.droids.model.Item;

import java.util.Map;

/**
 * External representation of a droid
 */
class ExternalDroid extends ExternalShooter {
    private ExternalDroid() { /* default constructor just for Jackson */ }

    /**
     * Creates a new external droid item based on an existing non-external droid.
     *
     * @param item the existing non-external droid item
     */
    ExternalDroid(Droid item, Map<Item, String> idMap) {
        super(item, idMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

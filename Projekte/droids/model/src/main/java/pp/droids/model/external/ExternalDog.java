package pp.droids.model.external;

import pp.droids.model.Dog;
import pp.droids.model.Item;

import java.util.Map;

public class ExternalDog extends ExternalBoundedItem {
    private ExternalDog() { /* default constructor just for Jackson */ }

    /**
     * Creates a new external enemy based on an existing non-external enemy item.
     *
     * @param item the non-external enemy
     */
    ExternalDog(Dog item, Map<Item, String> idMap) {
        super(item, idMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

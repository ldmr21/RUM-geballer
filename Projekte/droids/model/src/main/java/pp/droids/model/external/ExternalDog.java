package pp.droids.model.external;

import pp.droids.model.Dog;
import pp.droids.model.Item;
import pp.droids.model.Obstacle;

import java.util.Map;

public class ExternalDog extends ExternalBoundedItem{

    private ExternalDog() { /* default constructor just for Jackson */ }

    /**
     * Creates a new external obstacle based on an existing non-external one.
     *
     * @param item the existing non-external obstacle
     */
    ExternalDog(Dog item, Map<Item, String> idMap) {
        super(item, idMap);
    }

    @Override
    void accept(Visitor visitor) {visitor.visit(this);
    }
}

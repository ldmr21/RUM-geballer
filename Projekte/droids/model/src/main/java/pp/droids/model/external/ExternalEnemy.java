package pp.droids.model.external;

import pp.droids.model.Enemy;
import pp.droids.model.Item;

import java.util.Map;

/**
 * External representation of an enemy
 */
class ExternalEnemy extends ExternalShooter {
    private ExternalEnemy() { /* default constructor just for Jackson */ }

    /**
     * Creates a new external enemy based on an existing non-external enemy item.
     *
     * @param item the non-external enemy
     */
    ExternalEnemy(Enemy item, Map<Item, String> idMap) {
        super(item, idMap);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

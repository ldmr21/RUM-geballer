package pp.droids.model.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import pp.droids.model.Item;
import pp.droids.model.Rocket;
import pp.util.FloatPoint;
import pp.util.Position;

import java.util.Map;

/**
 * External representation of a rocket.
 */
class ExternalRocket extends ExternalBoundedItem {
    /**
     * the x-coordinate of the target position of the rocket
     */
    @JsonProperty
    float targetX;
    /**
     * the y-coordinate of the target position of the rocket
     */
    @JsonProperty
    float targetY;

    /**
     * Default constructor just for Jackson
     */
    private ExternalRocket() { /* empty */ }

    /**
     * Creates a new external rocket based on an existing non-external one.
     *
     * @param item the non-external rocket
     */
    ExternalRocket(Rocket item, Map<Item, String> idMap) {
        super(item, idMap);
        this.targetX = item.getTarget().getX();
        this.targetY = item.getTarget().getY();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the target of the external rocket.
     */
    @JsonIgnore
    Position getTarget() {
        return new FloatPoint(targetX, targetY);
    }
}

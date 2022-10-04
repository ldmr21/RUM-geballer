package pp.droids.model.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import pp.droids.model.BoundedItem;
import pp.droids.model.Item;
import pp.util.FloatPoint;
import pp.util.Position;

import java.util.Map;

/**
 * Abstract class for representations of bounded items.
 */
abstract class ExternalBoundedItem extends ExternalItem {
    @JsonProperty
    float x;

    @JsonProperty
    float y;

    @JsonProperty
    float angle;

    @JsonProperty
    float radius;

    /**
     * Default constructor just for Jackson
     */
    ExternalBoundedItem() { /* empty */ }

    ExternalBoundedItem(BoundedItem item, Map<Item, String> idMap) {
        super(item, idMap);
        this.x = item.getX();
        this.y = item.getY();
        this.angle = item.getRotation();
        this.radius = item.getRadius();
    }

    /**
     * Returns the position of the external bounded item.
     */
    @JsonIgnore
    Position getPos() {
        return new FloatPoint(x, y);
    }
}
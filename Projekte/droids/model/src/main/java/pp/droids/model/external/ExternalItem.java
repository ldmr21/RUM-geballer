package pp.droids.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import pp.droids.model.Item;

import java.util.Map;

/**
 * Abstract class for external items
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalDroid.class, name = "droid"), //NON-NLS
        @JsonSubTypes.Type(value = ExternalObstacle.class, name = "obstacle"), //NON-NLS
        @JsonSubTypes.Type(value = ExternalEnemy.class, name = "enemy"), //NON-NLS
        @JsonSubTypes.Type(value = ExternalRocket.class, name = "rocket"), //NON-NLS
        @JsonSubTypes.Type(value = ExternalFlag.class, name = "flag"), //NON-NLS
        @JsonSubTypes.Type(value = ExternalExit.class, name = "exit"), //NON-NLS
        @JsonSubTypes.Type(value = ExternalMaze.class, name = "maze") //NON-NLS
})
abstract class ExternalItem {
    @JsonProperty
    String id;

    /**
     * the level of this item.
     */
    @JsonProperty
    String level;

    @JsonProperty
    boolean destroyed;

    /**
     * Default constructor just for Jackson
     */
    ExternalItem() { /* empty */ }

    ExternalItem(Item item, Map<Item, String> idMap) {
        id = idMap.get(item);
        level = item.getLevel().getName();
        this.destroyed = item.isDestroyed();
    }

    /**
     * Accept method of the visitor pattern.
     *
     * @param visitor the visitor
     */
    abstract void accept(Visitor visitor);
}

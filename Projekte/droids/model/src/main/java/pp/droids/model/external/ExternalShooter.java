package pp.droids.model.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pp.droids.model.Item;
import pp.droids.model.Shooter;

import java.util.Map;

/**
 * Abstract class for representations of shooters.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class ExternalShooter extends ExternalBoundedItem {
    /**
     * the number of remaining lives of the item.
     */
    @JsonProperty
    int lives;

    /**
     * the reloading time of the shooter.
     */
    @JsonProperty
    float reloadTime;

    /**
     * The id of the captured flag or null
     */
    @JsonProperty
    String flag;

    /**
     * Default constructor just for Jackson
     */
    ExternalShooter() { /* empty */ }

    protected ExternalShooter(Shooter item, Map<Item, String> idMap) {
        super(item, idMap);
        this.lives = item.getLives();
        this.reloadTime = item.getReloadTime();
        this.flag = item.getCapturedFlag() == null ? null : idMap.get(item.getCapturedFlag());
    }
}

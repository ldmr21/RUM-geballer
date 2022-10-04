package pp.droids.model;

import pp.util.CircularEntity;
import pp.util.Position;
import pp.util.map.Observation;
import pp.util.map.ObservationMap;

import java.util.List;

/**
 * An entity that provides access to information collected from observations,
 * primarily for debugging reasons (see class pp.droids.view.debug.DebugView).
 */
public interface Debugee extends CircularEntity {
    /**
     * Returns the latest observation of the observing entity.
     */
    Observation getLatestObservation();

    /**
     * Returns the observation map collected by the observing entity so far.
     */
    ObservationMap getMap();

    /**
     * Returns the latest navigation path computed for the observing entity.
     */
    List<Position> getPath();
}

package pp.util.navigation;

import pp.util.CircularEntity;

/**
 * Represents any navigable entity.
 *
 * @param <T> the type of individual path segments when computing navigation paths.
 */
public interface Navigable<T> extends CircularEntity {
    /**
     * Returns a navigator that can be used to compute an optimal path for this
     * entity to any position.
     *
     * @return a new navigator
     */
    Navigator<T> getNavigator();
}

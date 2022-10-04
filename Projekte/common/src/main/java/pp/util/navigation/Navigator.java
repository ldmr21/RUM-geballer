package pp.util.navigation;

import pp.util.Position;

import java.util.List;

/**
 * Supports searching for an optimal path of a character to another position.
 *
 * @param <T> the type of individual path segments when computing navigation paths.
 */
public interface Navigator<T> {
    /**
     * Computes a minimal cost path of the item to the specified position. The path is
     * represented by a list of consecutive segments from a start position to the target,
     * or an empty list if there is no such path.
     *
     * @return the path from a start position to an end position if it exists,
     * or an empty list if there is no such path.
     */
    List<T> findPathTo(Position target);
}

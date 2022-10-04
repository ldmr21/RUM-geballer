package pp.util.navigation;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Base class for searching an optimal path between a start and a target position in the search space
 * using the A* algorithm. A position can be anything, for instance a point, a line segment, etc.
 * The type of positions is represented by the type parameter P of this class.
 *
 * @param <P> the type of positions in the search space.
 */
public abstract class AbstractNavigator<P> {

    /**
     * The logger of the navigator, mainly used for debugging.
     */
    private static final Logger LOGGER = System.getLogger(AbstractNavigator.class.getName());

    /**
     * A hashMap containing all nodes.
     */
    private final Map<P, Node> nodes = new HashMap<>();

    /**
     * The open queue of the navigator.
     */
    private final Queue<Node> openQueue = new PriorityQueue<>(Comparator.comparing(Node::getOverallCost));

    /**
     * The closed set used of the navigator.
     */
    private final Set<P> closedSet = new HashSet<>();

    /**
     * Returns the collection of start positions.
     *
     * @return collection of start positions
     */
    protected abstract Collection<P> getStartPositions();

    /**
     * Checks whether the specified position is a target position.
     *
     * @param pos a position in the search space
     * @return true iff the position is a target position
     */
    protected abstract boolean isTargetPosition(P pos);

    /**
     * Computes all positions that are directly reachable from the specified one.
     *
     * @param pos the specified position
     */
    protected abstract Collection<P> reachablePositions(P pos);

    /**
     * Computes the costs for a step from one position to another one.
     * Note that the first position may be null. The second position must be a start position then,
     * and the costs are the initial costs of this start position.
     *
     * @param prevPos the position at the beginning of the step (may be null)
     * @param nextPos the position after the step
     * @return the additional costs to go from prevPos to nextNode.
     */
    protected abstract float costsForStep(P prevPos, P nextPos);

    /**
     * Computes a lower bound of the costs of any path from the specified position to any target position.
     *
     * @param node the target node
     */
    protected abstract float estimateCostsToTarget(P node);

    /**
     * Computes a minimal cost path from any start position to any target position using the A* algorithm. The
     * path is represented by a list of consecutive positions from a start position to an end position,
     * or an empty list if there is no such path.
     *
     * @return the path from a start position to an end position if it exists,
     * or an empty list if there is no such path.
     */
    protected List<P> findPath() {
        final Collection<P> startPositions = getStartPositions();
        LOGGER.log(Level.TRACE, "find path from {0}", startPositions); //NON-NLS
        nodes.clear();
        openQueue.clear();
        closedSet.clear();

        try {
            for (P pos : startPositions)
                nodes.put(pos, new Node(pos, null));
            while (!openQueue.isEmpty()) {
                final Node bestNode = openQueue.poll();
                LOGGER.log(Level.ALL, "open queue: {0}\nclosedSet: {1}\nbest node: {2}", openQueue, closedSet, bestNode); //NON-NLS
                closedSet.add(bestNode.pos);
                if (isTargetPosition(bestNode.pos))
                    return buildPath(bestNode);
                for (P reachable : reachablePositions(bestNode.pos))
                    if (!closedSet.contains(reachable)) {
                        final Node successor = nodes.get(reachable);
                        if (successor == null)
                            nodes.put(reachable, new Node(reachable, bestNode));
                        else
                            successor.tryNewPredecessor(bestNode);
                    }
            }

            // no path to target
            return Collections.emptyList();
        }
        finally {
            LOGGER.log(Level.DEBUG, "navigator produced {0} and checked {1} positions", //NON-NLS
                       openQueue.size() + closedSet.size(),
                       closedSet.size());
        }
    }

    /**
     * Builds the path by following backward pointers from the target position to the start position
     *
     * @param end node representing the target position reached by the path
     * @return the path as a list of positions from its start to its target position
     */
    private List<P> buildPath(Node end) {
        List<P> plan = new LinkedList<>();
        for (Node n = end; n != null; n = n.predecessor)
            plan.add(0, n.pos);
        return plan;
    }

    /**
     * Represents a node in a path.
     */
    private class Node {
        /**
         * position in the search space
         */
        final P pos;
        /**
         * lower bound for the costs of the remaining path.
         */
        final float costToEnd;
        /**
         * Costs for the path ending at this path node
         */
        float costFromStart;
        /**
         * predecessor in path
         */
        Node predecessor;

        /**
         * Creates a path node
         *
         * @param pos position in the search space
         */
        Node(P pos, Node predecessor) {
            this.pos = pos;
            this.predecessor = predecessor;
            this.costToEnd = estimateCostsToTarget(pos);
            if (predecessor == null)
                costFromStart = costsForStep(null, pos);
            else
                costFromStart = predecessor.costFromStart + costsForStep(predecessor.pos, pos);
            openQueue.add(this);
        }

        /**
         * Try to use the specified node is a better predecessor of this node
         */
        void tryNewPredecessor(Node node) {
            final float newCosts = node.costFromStart + costsForStep(node.pos, pos);
            if (newCosts < costFromStart) {
                LOGGER.log(Level.TRACE, "better path to {0} via {1}", this, node); //NON-NLS
                openQueue.remove(this);
                predecessor = node;
                costFromStart = newCosts;
                openQueue.add(this);
            }
        }

        /**
         * Returns the estimated overall costs.
         *
         * @return overall costs
         */
        float getOverallCost() {
            return costFromStart + costToEnd;
        }

        @Override
        public String toString() {
            if (predecessor == null)
                return pos + "<-null"; //NON-NLS
            else
                return pos + "<-" + predecessor.pos;
        }
    }

    /**
     * Returns the closed set of the A* algorithm, i.e., the collection of
     * all positions that have been checked for being member of the
     * optimal path.
     *
     * @return a collection of P elements.
     */
    public Collection<P> getClosedSet() {
        return closedSet;
    }

    /**
     * Returns the remaining positions in the open queue of the A* algorithm, i.e.,
     * the collection of all positions that are reachable from any position
     * in the closed set, but that have not been checked for being member of the
     * optimal path (because they cannot be on the optimal path).
     *
     * @return a collection of P elements.
     */
    public Collection<P> getOpenQueue() {
        return openQueue.stream()
                        .map(n -> n.pos)
                        .toList();
    }
}

package pp.droids.model;

import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.RandomPositionIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static pp.util.FloatMath.TWO_PI;

/**
 * Class for generating random game maps.
 */
public class RandomMapGenerator {
    /**
     * The prefix used for each level name
     */
    private static final String LEVEL_PREFIX = "Level "; //NON-NLS

    /**
     * The list of positions defining a standard maze.
     */
    private static final List<Position> POSITIONS =
            List.of(p(5.85f, 12.15f), p(2.85f, 12.15f), p(2.85f, 9f), p(3.15f, 9f),
                    p(3.15f, 11.85f), p(9f, 11.85f), p(9f, 12.15f), p(6.15f, 12.15f),
                    p(6.15f, 15.15f), p(-0.15f, 15.15f), p(-0.15f, -0.15f), p(15.15f, -0.15f),
                    p(15.15f, 15.15f), p(9f, 15.15f), p(9f, 14.85f), p(14.85f, 14.85f),
                    p(14.85f, 12.15f), p(11.85f, 12.15f), p(11.85f, 9.15f), p(6f, 9.15f),
                    p(6f, 8.85f), p(12.15f, 8.85f), p(12.15f, 11.85f), p(14.85f, 11.85f),
                    p(14.85f, 3.15f), p(12.15f, 3.15f), p(12.15f, 6.15f), p(3f, 6.15f),
                    p(3f, 5.85f), p(8.85f, 5.85f), p(8.85f, 3f), p(9.15f, 3f),
                    p(9.15f, 5.85f), p(11.85f, 5.85f), p(11.85f, 2.85f), p(14.85f, 2.85f),
                    p(14.85f, 0.15f), p(6.15f, 0.15f), p(6.15f, 3f), p(5.85f, 3f),
                    p(5.85f, 0.15f), p(0.15f, 0.15f), p(0.15f, 2.85f), p(3f, 2.85f),
                    p(3f, 3.15f), p(0.15f, 3.15f), p(0.15f, 14.85f), p(5.85f, 14.85f));

    /**
     * The game model of the random map.
     */
    private final DroidsModel model;

    /**
     * A new {@link Random} object.
     */
    private final Random random = new Random();
    private final int numLevels;

    /**
     * The new map created in {@linkplain #createMap()}.
     */
    private DroidsMap map;

    /**
     * Creates a random map generator for the specified model.
     *
     * @param model the game model.
     */
    private RandomMapGenerator(DroidsModel model) {
        this.model = model;
        this.numLevels = max(1, model.getConfig().getNumLevels());
    }

    /**
     * Creates and returns a new random game map.
     */
    private DroidsMap createMap() {
        final DroidsConfig config = model.getConfig();
        map = new DroidsMap(model, config.getWidth(), config.getHeight());
        for (int i = 1; i <= numLevels; i++) {
            final MapLevel level = new MapLevel(map, LEVEL_PREFIX + i);
            new LevelGenerator(level, i == 1).makeLevel();
        }
        return map;
    }

    /**
     * Creates and returns a new random game map for the specified model.
     *
     * @param model the game model.
     */
    public static DroidsMap createMap(DroidsModel model) {
        return new RandomMapGenerator(model).createMap();
    }

    /**
     * A generator for a specific level
     */
    private class LevelGenerator {
        /**
         * This level.
         */
        private final MapLevel level;
        /**
         * Indicates whether the droid shall be positioned in this level.
         */
        private final boolean droidLevel;
        /**
         * Iterates over a random permutation of all positions in the level.
         */
        private final Iterator<Position> it;

        /**
         * Creates a level generator for the specified level
         */
        public LevelGenerator(MapLevel level, boolean droidLevel) {
            this.level = level;
            this.droidLevel = droidLevel;
            this.it = new RandomPositionIterator(map.getWidth(), map.getHeight());
        }

        /**
         * Generates items in the level
         */
        private void makeLevel() {
            final DroidsConfig config = model.getConfig();

            if (config.hasMaze()) {
                // add maze
                map.register(new Maze(model, POSITIONS), level);
                map.addRegisteredItems();
            }

            // add obstacles at random positions
            for (int i = 0; i < config.getNumObstacles(); i++)
                addBoundedItem(new Obstacle(model));

            // add enemies at random positions
            for (int i = 0; i < config.getNumEnemies(); i++)
                addBoundedItem(new Enemy(model));

            // add rockets at fixed positions
            for (int i = 0; i < config.getNumRockets(); i++)
                addRocket(new Rocket(model), i);

            // just in the first level
            if (droidLevel) {
                addBoundedItem(new Exit(model));
                for (int i = 0; i < config.getNumFlags(); i++)
                    addBoundedItem(new Flag(model));
                setDroid();
                setDog();
            }
        }

        private void setDog(){
            final Dog dog = new Dog(model);
            addBoundedItem(dog);
            map.setDog(dog);
            // add a dog at random position
        }

        /**
         * Sets the droid on next available position of the level.
         */
        private void setDroid() {
            final Droid droid = new Droid(model);
            map.setDroid(droid, level);
            droid.setRotation(random.nextFloat() * TWO_PI);
            while (it.hasNext()) {
                final Position pos = it.next();
                droid.setPos(pos);
                if (validPosition(droid))
                    return;
            }
        }

        /**
         * Adds a BoundedItem to the level.
         */
        private void addBoundedItem(BoundedItem item) {
            item.setRotation(random.nextFloat() * TWO_PI);
            while (it.hasNext()) {
                final Position pos = it.next();
                item.setPos(pos);
                item.setLevel(level);
                if (validPosition(item)) {
                    map.register(item, level);
                    map.addRegisteredItems();
                    return;
                }
            }
        }

        /**
         * Adds a rocket to the level.
         *
         * @param rocket type of rocket
         * @param i      number of rockets
         */
        private void addRocket(Rocket rocket, int i) {
            final int y1 = i * (map.getHeight() / model.getConfig().getNumRockets());
            final int y2 = (i + 1) * (map.getHeight() / model.getConfig().getNumRockets());
            rocket.setPos(map.getWidth() - i, y1);
            rocket.setTarget(2f, y2);
            map.register(rocket, level);
            map.addRegisteredItems();
        }

        /**
         * Checks whether the specified item may be placed at its current position in
         * this level, i.e., it is within the map borders and does not overlap with
         * any other item in this level.
         */
        private boolean validPosition(BoundedItem item) {
            final float x = item.getX();
            final float y = item.getY();
            final float r = item.getRadius();
            return x - r >= map.getXMin() &&
                   x + r <= map.getXMax() &&
                   y - r >= map.getYMin() &&
                   y + r <= map.getYMax() &&
                   !item.overlapsWithAnyOtherItem(i -> i.getLevel() == level);
        }
    }

    /**
     * Convenience method for creating positions.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     */
    private static Position p(float x, float y) {
        return new FloatPoint(x, y);
    }
}

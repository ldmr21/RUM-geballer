package pp.droids.model;

import pp.util.map.Triangle;

/**
 * A class defining the different object categories.
 */
public class Category {
    /**
     * Default constructor. This should not be used.
     */
    private Category() { /* do not instantiate */ }

    /**
     * The unspecified category of an object.
     */
    public static final String UNSPECIFIED = "UNSPECIFIED";

    /**
     * The category of all characters, e.g., droid and enemies.
     */
    public static final String CHARACTER = "CHARACTER";

    /**
     * The category used for outer walls around the map.
     */
    public static final String OUTER_WALL = "OUTER_WALL"; //NON-NLS

    /**
     * The category used for walls.
     */
    public static final String WALL = "WALL";

    /**
     * The category used for obstacles.
     */
    public static final String OBSTACLE = "OBSTACLE";

    /**
     * The category used for coins.
     */
    public static final String COIN = "COIN";

    /**
     * The category used for medipacks.
     */
    public static final String MEDIPACK = "MEDIPACK";

    /**
     * The category used for the flag.
     */
    public static final String FLAG = "FLAG";

    /**
     * The category used for the exit.
     */
    public static final String EXIT = "EXIT";

    /**
     * The category used for the dog.
     */
    public static final String DOG = "DOG";

    /**
     * The category indicating that this triangle is out of the observer's viewing area.
     */
    public static final String OUT_OF_VIEWING_AREA = Triangle.OUT_OF_VIEWING_AREA;

    /**
     * The category indicating that this triangle provides an infinite view.
     */
    public static final String INFINITY = Triangle.INFINITY;
}

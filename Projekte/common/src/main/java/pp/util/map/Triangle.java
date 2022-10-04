package pp.util.map;

import pp.util.Angle;
import pp.util.Circle;
import pp.util.Interval;
import pp.util.TypedSegment;

import static pp.util.FloatMath.ZERO_TOLERANCE;

/**
 * A visibility triangle. The tip of the triangle is at the observer's position,
 * the opposite side is either a (partial) segment or infinity if there is no
 * visible segment. The left and right sides of the triangle are defined
 * by polar coordinate angles (as unit vectors) where 0 is in the direction of the x-axis.
 * Note that the segment is not publicly accessible in order not to reveal
 * information beyond its visible part.
 */
public class Triangle {
    /**
     * The category indicating that this triangle is out of the observer's viewing area.
     */
    public static final String OUT_OF_VIEWING_AREA = "OUT_OF_VIEWING_AREA"; //NON-NLS
    /**
     * The category indicating that this triangle provides an infinite view.
     */
    public static final String INFINITY = "INFINITY";
    final TypedSegment segment;
    final Circle entity;

    final Interval interval;
    private final Angle leftAngle;
    private final Angle rightAngle;
    private final float leftDist;
    private final float rightDist;

    /**
     * Creates a new visibility triangle
     *
     * @param segment    the segment that is partially visible
     * @param entity     the circular entity whose segment is (partially) visible.
     * @param leftAngle  the angle of the left side of the triangle
     * @param rightAngle the angle of the right side of the triangle
     * @param leftDist   length of the left side or {@linkplain Float#POSITIVE_INFINITY}
     * @param rightDist  length of the right side or {@linkplain Float#POSITIVE_INFINITY}
     * @param interval   the visible part of the segment as sub-interval of [0, 1].
     *                   0 means the left end point of the segment where it starts,
     *                   1 its right end point
     */
    Triangle(TypedSegment segment, Circle entity, Angle leftAngle, Angle rightAngle, float leftDist, float rightDist, Interval interval) {
        this.segment = segment;
        this.entity = entity;
        this.interval = interval;
        this.leftAngle = leftAngle;
        this.rightAngle = rightAngle;
        this.leftDist = leftDist;
        this.rightDist = rightDist;
    }

    /**
     * Returns the segment that is partially visible.
     */
    TypedSegment getSegment() {
        return segment;
    }

    /**
     * Returns the circular entity whose segment is (partially) visible, or null if
     * the segment does not belong to a circular entity.
     *
     * @return the circular entity or null.
     */
    public Circle getEntity() {
        return entity;
    }

    /**
     * Returns the visible part of the segment as sub-interval of [0, 1].
     * 0 means the left end point of the segment where it starts,
     * 1 its right end point.
     */
    Interval getInterval() {
        return interval;
    }

    /**
     * Returns the angle (as unit vector) of the left side of the triangle where 0 degrees
     * is in the direction of the x-axis.
     */
    public Angle leftAngle() {
        return leftAngle;
    }

    /**
     * Returns the angle (as unit vector) of the right side of the triangle where 0 degrees
     * is in the direction of the x-axis.
     */
    public Angle rightAngle() {
        return rightAngle;
    }

    /**
     * Returns the category of the segment visible within this triangle, or null if
     * there is no segment.
     * This is the only information of the segment that is made publicly available.
     *
     * @return the segment category or null
     */
    public String cat() {
        return segment == null ? INFINITY : segment.cat();
    }

    /**
     * Returns the length of the left triangle side.
     */
    public float leftDist() {
        return leftDist;
    }

    /**
     * Returns the length of the right triangle side.
     */
    public float rightDist() {
        return rightDist;
    }

    @Override
    public String toString() {
        return String.format("Triangle[leftAngle=%s, rightAngle=%s, leftDist=%s, rightDist=%s, cat=%s]", //NON-NLS
                             leftAngle, rightAngle, leftDist, rightDist, cat());
    }

    /**
     * Checks whether the specified point lies within this triangle where the tip of this
     * triangle is the origin of the coordinate system. The method returns false if at least
     * one of the two sides has infinite length. The method uses barycentric coordinates.
     *
     * @param x the points x value
     * @param y the points y value
     */
    public boolean contains(float x, float y) {
        if (leftDist == Float.POSITIVE_INFINITY || rightDist == Float.POSITIVE_INFINITY)
            return false;
        final float det = leftAngle.y * rightAngle.x - leftAngle.x * rightAngle.y;
        final float lambda1 = (leftAngle.y * x - leftAngle.x * y) / (rightDist * det);
        if (lambda1 < -ZERO_TOLERANCE) return false;
        final float lambda2 = (rightAngle.x * y - rightAngle.y * x) / (leftDist * det);
        return lambda2 >= -ZERO_TOLERANCE && lambda1 + lambda2 - 1f <= ZERO_TOLERANCE;
    }
}

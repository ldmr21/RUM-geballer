package pp.util.map;

import pp.util.Angle;
import pp.util.Circle;
import pp.util.Interval;
import pp.util.Position;
import pp.util.TypedSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.Float.POSITIVE_INFINITY;

/**
 * A class for computing and representing all segments that are at least partially visible
 * from a viewer position in the plane and that lie within a certain viewing area (which is
 * possible the complete circle). Partially visible segments are represented by {@linkplain
 * Triangle} instances. The tip of each triangle is at the viewer position. The left and the
 * right side of the triangle define the left and right end point of the sub-segment that is
 * visible from the viewer position. The entire viewing circle is partitioned into such
 * triangles.
 */
public class Observation {
    private final Position observer;
    private final List<Triangle> triangles = new ArrayList<>();
    private final Map<TypedSegment, Circle> entityMap;

    /**
     * Creates the observation for the specified collection of segments.
     * The viewing area specifies how much the
     * viewer can observe: its right border is 0.5*viewingArea to the right
     * of its viewing direction, and its left border is 0.5*viewingArea to
     * the left of its viewing area. A viewingArea value larger than
     * 2*pi is truncated to 2*pi.
     *
     * @param observer    the viewer position
     * @param direction   the viewer direction (in radians)
     * @param viewingArea represents the viewing area (in radians).
     * @param segments    the collection of all segments
     * @param entityMap   maps segments to the shape of those BoundedItems
     *                    that generated these segments
     */
    public Observation(Position observer, float direction, float viewingArea,
                       Collection<TypedSegment> segments,
                       Map<TypedSegment, Circle> entityMap) {
        this.observer = observer;
        this.entityMap = entityMap;
        final Visibility vis = new Visibility(observer, direction, viewingArea, segments);
        if (vis.getSteps().isEmpty()) return;
        // note that the first step is also the last step
        Step prevStep = null;
        for (Step curStep : vis.getSteps()) {
            if (prevStep != null)
                triangles.add(makeTriangle(prevStep.leftSegment(), curStep.angle(), prevStep.angle()));
            prevStep = curStep;
        }
    }

    private Triangle makeTriangle(TypedSegment segment, Angle leftAngle, Angle rightAngle) {
        if (segment == null)
            return new Triangle(null, null, leftAngle, rightAngle, POSITIVE_INFINITY, POSITIVE_INFINITY, null);
        final Interval interval = segment.interval(observer, leftAngle, rightAngle);
        final float leftDist = segment.quotientDist(observer, interval.from());
        final float rightDist = segment.quotientDist(observer, interval.to());
        return new Triangle(segment, entityMap.get(segment), leftAngle, rightAngle, leftDist, rightDist, interval);
    }

    /**
     * Returns the sequence of triangles, i.e., segment parts visible from the viewer position.
     * The triangles are ordered from right to left, starting at the first angle within the viewing
     * area. The viewing area starts on the right or, if the viewing area is not restricted, at the
     * 6 o'clock position for the observer.
     */
    public List<Triangle> getTriangles() {
        return triangles;
    }
}

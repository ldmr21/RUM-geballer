package pp.util.map;

import pp.util.Angle;
import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.TypedSegment;

import java.util.Comparator;

/**
 * A comparator used to keep segments in the heap sorted by their distance from the observer.
 */
class HeapComparator implements Comparator<TypedSegment> {
    private static final float EPS = 1e-4f;
    private static final float FACTOR = 10f;
    private static final Position ZERO = new FloatPoint(0f, 0f);
    /**
     * A dummy segment that is closer to the observer than any other segment.
     */
    static final TypedSegment OUT_OF_VIEWING_AREA_SEG = new TypedSegment(ZERO, ZERO, Triangle.OUT_OF_VIEWING_AREA);
    private final Position observer;
    private Angle angle;

    HeapComparator(Position observer, Angle angle) {
        this.observer = observer;
        setAngle(angle);
    }

    void setAngle(Angle angle) {
        this.angle = angle;
    }

    @Override
    public int compare(TypedSegment o1, TypedSegment o2) {
        if (o1 == o2)
            return 0;
        if (o1 == OUT_OF_VIEWING_AREA_SEG)
            return -1;
        if (o2 == OUT_OF_VIEWING_AREA_SEG)
            return 1;
        final float dist1 = o1.dist(observer, angle);
        final float dist2 = o2.dist(observer, angle);
        if (dist1 < EPS || dist2 < EPS)
            throw new RuntimeException("observer too close to segment");
        final float diff = dist1 - dist2;
        if (diff < -EPS)
            return -1;
        if (diff > EPS)
            return 1;
        // Distances are too close. We compare distances of those points
        // that are reached when going a distance FACTOR along the
        // segments to the left. That might be wrong when end previously,
        // but then the segments either intersect (which is not allowed),
        // or they are removed from the heap next.
        return Float.compare(nextDist(o1, dist1), nextDist(o2, dist2));
    }

    private float nextDist(TypedSegment seg, float dist) {
        final float f = FACTOR / seg.length();
        final float x = angle.x * dist - seg.diffX() * f;
        final float y = angle.y * dist - seg.diffY() * f;
        return x * x + y * y;
    }
}
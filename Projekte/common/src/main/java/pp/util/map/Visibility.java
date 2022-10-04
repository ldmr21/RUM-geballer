package pp.util.map;

import pp.util.Angle;
import pp.util.Position;
import pp.util.TypedSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.lang.Float.min;
import static pp.util.FloatMath.PI;
import static pp.util.FloatMath.TWO_PI;
import static pp.util.map.HeapComparator.OUT_OF_VIEWING_AREA_SEG;

/**
 * A class for determining all visible segment parts using a rotational sweep-line algorithm.
 */
class Visibility {

    /**
     * The observer position.
     */
    private final Position observer;
    /**
     * The rightmost angle of the viewing area as unit vector,
     * or the observer's 6 o'clock position if the viewing area is not restricted.
     */
    private final Angle startAngle;
    /**
     * The angle range of the viewing area as unit vector or null if the viewing area
     * is not restricted.
     */
    private final Angle viewingArea;
    /**
     * Maps angles (as unit vectors) to borders where visibility of segments may change.
     * Angles are represented by unit vectors with 0 degrees corresponding to the right
     * border of the viewing area, or at the observer's 6 o'clock position if the viewing
     * area is not restricted.
     */
    private Map<Angle, Border> borderMap;
    /**
     * The borders sorted by their angles.
     */
    private List<Border> borders;
    /**
     * The sorted set of all segments that currently intersect the sweep line,
     * sorted by their distance from the observer.
     */
    private TreeSet<TypedSegment> heap;
    /**
     * The comparator used for keeping the heap sorted.
     */
    private HeapComparator comparator;
    /**
     * The sequence of steps where visibility of segments changes.
     */
    private final List<Step> steps = new ArrayList<>();
    /**
     * The segments that are the initial heap members when the sweep-line
     * algorithm starts.
     */
    private final List<TypedSegment> initialSegments = new ArrayList<>();

    /**
     * Computes the visibility of all segments that are visible from the specified
     * observer with the specified viewing area.
     *
     * @param observer    the observer position
     * @param direction   where the observer looks
     * @param viewingArea The angle range of the viewing area. Any value >= 2*pi means an
     *                    unrestricted viewing area.
     * @param segments    the collection of all segments
     */
    public Visibility(Position observer, float direction, float viewingArea, Collection<TypedSegment> segments) {
        this.observer = observer;
        this.startAngle = Angle.fromRadians(direction - min(PI, 0.5f * viewingArea));
        this.viewingArea = viewingArea >= TWO_PI ? null : Angle.fromRadians(viewingArea);
        if (!segments.isEmpty()) {
            makeBorderMap(segments);
            borders = new ArrayList<>(borderMap.values());
            makeComparator();
            heap = new TreeSet<>(comparator);
            heap.addAll(initialSegments);
            rotationalSweep();
            // add first step also as last step for easier creation of triangles in makeTriangles()
            if (!steps.isEmpty())
                steps.add(steps.get(0));
        }
    }

    private void makeBorderMap(Collection<TypedSegment> segments) {
        borderMap = new TreeMap<>();
        if (viewingArea != null) {
            // if viewing area is restricted, add pseudo segment
            // that hides everything outside the viewing area
            borderMap.computeIfAbsent(viewingArea, this::makeBorder).left.add(OUT_OF_VIEWING_AREA_SEG);
            borderMap.computeIfAbsent(Angle.ZERO, this::makeBorder).right.add(OUT_OF_VIEWING_AREA_SEG);
            initialSegments.add(OUT_OF_VIEWING_AREA_SEG);
        }
        for (TypedSegment seg : segments) {
            final Angle leftAngle = angle(seg.from());
            final Angle rightAngle = angle(seg.to());
            if (correctOrientation(leftAngle, rightAngle) && inViewingArea(leftAngle, rightAngle)) {
                borderMap.computeIfAbsent(leftAngle, this::makeBorder).right.add(seg);
                borderMap.computeIfAbsent(rightAngle, this::makeBorder).left.add(seg);
                if (leftAngle.compareTo(rightAngle) < 0)
                    initialSegments.add(seg);
            }
        }
    }

    /**
     * Returns true iff leftAngle is left of rightAngle.
     */
    private static boolean correctOrientation(Angle leftAngle, Angle rightAngle) {
        return leftAngle.minus(rightAngle).compareTo(Angle.PI) < 0;
    }

    /**
     * Returns true iff the segment from leftAngle to rightAngle lies at least partially
     * within the viewing area.
     */
    private boolean inViewingArea(Angle leftAngle, Angle rightAngle) {
        return viewingArea == null ||
               leftAngle.compareTo(viewingArea) <= 0 ||
               rightAngle.compareTo(viewingArea) <= 0 ||
               leftAngle.compareTo(rightAngle) < 0;
    }

    private Border makeBorder(Angle angle) {
        return new Border(angle.plus(startAngle));
    }

    /**
     * Returns the polar coordinate angle of the specified vector in the range [0, 2*pi)
     * where 0 is the right border of the viewing area (or 6 o'clock if the viewing
     * area is not restricted).
     */
    private Angle angle(Position p) {
        final float x = p.getX() - observer.getX();
        final float y = p.getY() - observer.getY();
        return Angle.fromVector(x, y).minus(startAngle);
    }

    private void rotationalSweep() {
        TypedSegment visible = visibleSegment();
        for (Border border : borders) {
            border.right.forEach(heap::remove);
            comparator.setAngle(border.angle);
            heap.addAll(border.left);
            final TypedSegment newVisible = visibleSegment();
            if (visible != newVisible)
                steps.add(new Step(border.angle, newVisible));
            visible = newVisible;
        }
    }

    private TypedSegment visibleSegment() {
        if (heap.isEmpty())
            return null;
        final TypedSegment first = heap.first();
        // the pseudo segment does not really exist
        return first == OUT_OF_VIEWING_AREA_SEG ? null : first;
    }

    private void makeComparator() {
        if (borders.isEmpty())
            comparator = new HeapComparator(observer, Angle.ZERO);
        else {
            final Border last = borders.get(borders.size() - 1);
            comparator = new HeapComparator(observer, last.angle);
        }
    }

    /**
     * Returns sequence of steps where visibility of segments changes.
     * If this sequence is not empty, its first element is also its last one.
     */
    public List<Step> getSteps() {
        return steps;
    }
}
package pp.util.map;

import pp.util.Angle;
import pp.util.TypedSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an angle from the observer's view with all segments that
 * end or start at this angle.
 */
class Border {
    /**
     * The angle as unit vector where 0 degrees is along the x-axis.
     */
    final Angle angle;
    /**
     * All segments that have their end point on this border, i.e.,
     * the segments are to the left of this border.
     */
    final List<TypedSegment> left = new ArrayList<>();
    /**
     * All segments that have their starting point on this border, i.e.,
     * the segments are to the right of this border.
     */
    final List<TypedSegment> right = new ArrayList<>();

    Border(Angle angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return angle.toString();
    }
}
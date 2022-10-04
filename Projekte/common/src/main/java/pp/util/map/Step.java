package pp.util.map;

import pp.util.Angle;
import pp.util.TypedSegment;

/**
 * A step that means a change of visibility
 *
 * @param angle       the angle from the observer's view as unit vector
 *                    where 0 degrees is along the x-axis
 * @param leftSegment the segment that is visible to the left of this angle,
 *                    but that is not visible to the right of this angle.
 */
record Step(Angle angle, TypedSegment leftSegment) {}

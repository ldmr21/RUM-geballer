package pp.util.map;

import pp.util.Interval;
import pp.util.IntervalSeq;
import pp.util.TypedSegment;

import java.util.Iterator;

import static pp.util.FloatMath.ZERO_TOLERANCE;

/**
 * Represents a segment that has been partially observed.
 *
 * @param segment     the segment
 * @param intervalSeq the sequence of intervals that have been observed so far.
 */
record ObservedSegment(TypedSegment segment, IntervalSeq intervalSeq) implements Iterable<TypedSegment> {
    /**
     * Returns the intervals of this segment that have been observed so far as segments of their own. These
     * segments have the same category as this segment.
     */
    @Override
    public Iterator<TypedSegment> iterator() {
        return new Iterator<>() {
            private final Iterator<Interval> it = intervalSeq.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public TypedSegment next() {
                final Interval i = it.next();
                final float q1 = i.from();
                final float q2 = i.to();
                if (q1 > q2 || q1 < -ZERO_TOLERANCE || q2 - 1f > ZERO_TOLERANCE)
                    throw new IllegalArgumentException("Invalid subsegment [" + q1 + "; " + q2 + "]");
                return new TypedSegment(segment.pointAt(q1), segment.pointAt(q2), segment.cat());
            }
        };
    }
}

package pp.util;

import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatMath.abs;

/**
 * Represents an interval. The start value must not be greater than the end value.
 *
 * @param from the start value of the interval
 * @param to   the end value of the interval
 */
public record Interval(float from, float to) {

    /**
     * Creates a new interval between two specified values.
     *
     * @param from the specified start value
     * @param to   the specified end value
     */
    public Interval {
        if (from > to)
            throw new IllegalArgumentException(from + " > " + to);
    }

    /**
     * Checks whether this interval has length 0, i.e., from and to are equal.
     */
    public boolean isEmpty() {
        return to == from;
    }

    /**
     * Checks whether the specified value is contained in this interval. Note that an empty interval may
     * contain the value if the value is the start and the end value of the interval.
     *
     * @param value the specified value to check
     */
    public boolean contains(float value) {
        return from - value <= ZERO_TOLERANCE && value - to <= ZERO_TOLERANCE;
    }

    /**
     * Checks whether the specified interval is contained as a sub-interval.
     *
     * @param other the potential sub-interval
     */
    public boolean contains(Interval other) {
        return from - other.from < ZERO_TOLERANCE && other.to - to < ZERO_TOLERANCE;
    }

    /**
     * Returns a string representation of this interval.
     */
    @Override
    public String toString() {
        return "[" + from + "; " + to + "]";
    }

    /**
     * Checks whether the specified interval is almost equal to this
     * interval up to the specified epsilon value.
     * @param other the other interval to check
     * @param eps the allowed epsilon value
     */
    public boolean matches(Interval other, float eps) {
        return abs(from - other.from) < eps &&
               abs(to - other.to) < eps;
    }
}

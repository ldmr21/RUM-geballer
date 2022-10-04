package pp.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Float.max;
import static java.lang.Float.min;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static pp.util.FloatMath.ZERO_TOLERANCE;

/**
 * A  non-overlapping sequence of intervals.
 */
public class IntervalSeq implements Iterable<Interval> {
    /**
     * The empty sequence of intervals.
     */
    public static final IntervalSeq EMPTY = new IntervalSeq();
    private final List<Interval> intervalList;

    IntervalSeq(List<Interval> intervalList) {
        this.intervalList = intervalList;
    }

    private IntervalSeq() {
        this(emptyList());
    }

    /**
     * Creates an interval sequence consisting of just the specified interval.
     *
     * @param interval the specified interval
     */
    public IntervalSeq(Interval interval) {
        this(singletonList(interval));
    }

    /**
     * Adds the specified interval and updates the sequence of intervals so that
     * intervals do not overlap. This method does not change this object but
     * rather returns the obtained sequence of intervals as result. The method
     * returns this (unchanged) object if adding the specified interval does not
     * change the sequence of intervals (because the interval is already contained
     * as a sub-interval of some interval in this sequence).
     *
     * @param interval the interval to add
     */
    public IntervalSeq add(Interval interval) {
        if (interval.isEmpty() || contains(interval)) return this;
        final List<Interval> result = new ArrayList<>();
        for (Interval own : intervalList) {
            if (interval == null || interval.from() - own.to() > ZERO_TOLERANCE)
                result.add(own);
            else if (own.from() - interval.to() > ZERO_TOLERANCE) {
                result.add(interval);
                interval = null;
                result.add(own);
            }
            else
                interval = new Interval(min(interval.from(), own.from()),
                                        max(interval.to(), own.to()));
        }
        if (interval != null)
            result.add(interval);
        return new IntervalSeq(result);
    }

    /**
     * Adds all intervals of the specified interval sequence and updates this
     * sequence of intervals so that intervals do not overlap.
     *
     * @param other the other Interval Sequence to add
     */
    public IntervalSeq add(IntervalSeq other) {
        IntervalSeq current = this;
        for (Interval interval : other)
            current = current.add(interval);
        return current;
    }

    /**
     * Returns an iterator that iterates over all intervals of this sequence.
     */
    @Override
    public Iterator<Interval> iterator() {
        return intervalList.iterator();
    }

    /**
     * Checks whether any interval in this sequence of intervals contains the specified value.
     *
     * @param value the value to check for
     */
    public boolean contains(float value) {
        return intervalList.stream().anyMatch(i -> i.contains(value));
    }

    /**
     * Checks whether any interval in this sequence of intervals contains the specified interval as a
     * sub-interval.
     *
     * @param interval the interval to check for
     */
    public boolean contains(Interval interval) {
        return intervalList.stream().anyMatch(i -> i.contains(interval));
    }

    /**
     * Returns a string representation of this sequence of intervals.
     */
    @Override
    public String toString() {
        return intervalList.toString();
    }

    /**
     * Checks whether this interval sequence consists of just a single interval,
     * and this interval is almost equal to the specified interval up to the
     * specified epsilon value.
     *
     * @param interval the other interval to check
     * @param eps      the allowed epsilon value
     */
    public boolean matches(Interval interval, float eps) {
        return intervalList.size() == 1 && intervalList.get(0).matches(interval, eps);
    }

    /**
     * Checks whether this sequence of intervals is almost equal to the specified
     * sequence of intervals where values of corresponding start and end points,
     * respectively, must be equal up to the specified epsilon value.
     *
     * @param other the other interval sequence to check
     * @param eps   the allowed epsilon value
     */
    public boolean matches(IntervalSeq other, float eps) {
        if (this.intervalList.size() != other.intervalList.size())
            return false;
        final Iterator<Interval> it1 = this.iterator();
        final Iterator<Interval> it2 = other.iterator();
        while (it1.hasNext() && it2.hasNext())
            if (!it1.next().matches(it2.next(), eps))
                return false;
        assert !it1.hasNext() && !it2.hasNext();
        return true;
    }
}

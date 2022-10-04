package pp.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pp.util.FloatMath.ZERO_TOLERANCE;

public class IntervalSeqTest {
    public IntervalSeq ints1;
    public IntervalSeq ints2;
    public IntervalSeq ints3;
    public IntervalSeq ints4;
    public IntervalSeq ints5;

    @Before
    public void setUp() {
        ints1 = new IntervalSeq(List.of(new Interval(0f, 0.1f), new Interval(0.5f, 0.9f)));
        ints2 = new IntervalSeq(List.of(new Interval(0f, 0.1f), new Interval(0.2f, 0.4f), new Interval(0.5f, 0.9f)));
        ints3 = new IntervalSeq(List.of(new Interval(0f, 0.4f), new Interval(0.5f, 0.9f)));
        ints4 = new IntervalSeq(List.of(new Interval(0f, 1f)));
        ints5 = new IntervalSeq(List.of(new Interval(0.05f, 0.6f), new Interval(0.8f, 1f)));
    }

    @Test
    public void add() {
        assertTrue(ints1.add(new Interval(0.2f, 0.4f)).matches(ints2, ZERO_TOLERANCE));
        assertFalse(ints1.add(new Interval(0.2f, 0.3f)).matches(ints2, ZERO_TOLERANCE));
        assertTrue(ints2.add(new Interval(0f, 0.3f)).matches(ints3, ZERO_TOLERANCE));
        assertTrue(ints2.add(new Interval(0.05f, 0.3f)).matches(ints3, ZERO_TOLERANCE));
        assertTrue(ints2.add(new Interval(0.1f, 0.3f)).matches(ints3, ZERO_TOLERANCE));
        assertTrue(ints2.add(new Interval(0.05f, 1f)).matches(ints4, ZERO_TOLERANCE));
        assertTrue(ints2.add(new Interval(0f, 1f)).matches(ints4, ZERO_TOLERANCE));
    }

    @Test
    public void testAdd() {
        assertTrue(ints1.add(ints5).matches(ints4, ZERO_TOLERANCE));
    }

    @Test
    public void iterator() {
        final Iterator<Interval> it = ints2.iterator();
        assertTrue(it.next().matches(new Interval(0f, 0.1f), ZERO_TOLERANCE));
        assertTrue(it.next().matches(new Interval(0.2f, 0.4f), ZERO_TOLERANCE));
        assertTrue(it.next().matches(new Interval(0.5f, 0.9f), ZERO_TOLERANCE));
        assertFalse(it.hasNext());
    }

    @Test
    public void contains() {
        assertTrue(ints2.contains(0.05f));
        assertTrue(ints2.contains(0.4f));
        assertFalse(ints2.contains(0.45f));
    }
}
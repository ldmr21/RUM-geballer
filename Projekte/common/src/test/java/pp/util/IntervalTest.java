package pp.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pp.util.FloatMath.ZERO_TOLERANCE;

public class IntervalTest {
    public Interval INTERVAL;

    @Before
    public void setUp() {
        INTERVAL = new Interval(0f, 1f);
    }

    @Test
    public void contains() {
        assertTrue(INTERVAL.contains(0.5f));
        assertTrue(INTERVAL.contains(0f));
        assertTrue(INTERVAL.contains(1f));
        assertFalse(INTERVAL.contains(1.5f));
        assertFalse(INTERVAL.contains(-0.5f));
    }

    @Test
    public void matches() {
        assertTrue(INTERVAL.matches(new Interval(0f, 1f), ZERO_TOLERANCE));
        assertFalse(INTERVAL.matches(new Interval(0f, 0.99f), ZERO_TOLERANCE));
    }
}
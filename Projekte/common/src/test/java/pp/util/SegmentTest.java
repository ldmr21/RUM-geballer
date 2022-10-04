package pp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatMath.PI;
import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sqr;
import static pp.util.FloatMath.sqrt;
import static pp.util.FloatMath.tan;

public class SegmentTest {
    private static final float SQRT2 = sqrt(2f);
    private static final Segment segment1 = new Segment(new FloatPoint(1f, -1f), new FloatPoint(1f, 1f));
    private static final Segment segment2 = new Segment(new FloatPoint(SQRT2, 0f), new FloatPoint(0f, SQRT2));
    private static final Segment segment3 = new Segment(new FloatPoint(2f, -1f), new FloatPoint(2f, 1f));
    private static final Segment segment4 = new Segment(new FloatPoint(SQRT2, -1f), new FloatPoint(SQRT2, 1f));
    private static final Segment segment5 = new Segment(new FloatPoint(-SQRT2, 2f * SQRT2), new FloatPoint(2f * SQRT2, -SQRT2));
    private static final Segment segment6 = new Segment(new FloatPoint(0f, 0f), new FloatPoint(SQRT2, 1f));
    private static final FloatPoint ZERO = new FloatPoint(0f, 0f);
    public static final FloatPoint ONE_UP = new FloatPoint(0f, 1f);
    public static final FloatPoint ONE_DOWN = new FloatPoint(0f, -1f);

    @Test
    public void dist1() {
        assertEquals(1f, segment1.dist(ZERO, 0f), FLT_EPSILON);
        assertEquals(1f, segment1.dist(ONE_UP, 0f), FLT_EPSILON);
        assertEquals(1f, segment1.dist(ONE_DOWN, 0f), FLT_EPSILON);
        assertEquals(1f / cos(0.125f * PI), segment1.dist(ZERO, 0.125f * PI), FLT_EPSILON);
        assertEquals(1f / cos(0.125f * PI), segment1.dist(ONE_UP, 0.125f * PI), FLT_EPSILON);
        assertEquals(1f / cos(0.125f * PI), segment1.dist(ONE_DOWN, 0.125f * PI), FLT_EPSILON);
        assertEquals(1f / cos(0.125f * PI), segment1.dist(ZERO, -0.125f * PI), FLT_EPSILON);
        assertEquals(1f / cos(0.125f * PI), segment1.dist(ONE_UP, -0.125f * PI), FLT_EPSILON);
        assertEquals(1f / cos(0.125f * PI), segment1.dist(ONE_DOWN, -0.125f * PI), FLT_EPSILON);
        assertEquals(SQRT2, segment1.dist(ZERO, 0.25f * PI), FLT_EPSILON);
        assertEquals(SQRT2, segment1.dist(ZERO, -0.25f * PI), FLT_EPSILON);
    }

    @Test
    public void dist2() {
        assertEquals(1f, segment2.dist(ZERO, 0.25f * PI), FLT_EPSILON);
        assertEquals(SQRT2, segment2.dist(ZERO, 0f), FLT_EPSILON);
        assertEquals(SQRT2, segment2.dist(ZERO, 0.5f * PI), FLT_EPSILON);
        assertEquals(1f / cos(0.125f * PI), segment2.dist(ZERO, 0.375f * PI), FLT_EPSILON);
        assertEquals(SQRT2, segment2.dist(ZERO, PI / 2f), FLT_EPSILON);
    }

    @Test
    public void quotient1() {
        assertEquals(0.5f, segment1.quotient(ZERO, 0f), FLT_EPSILON);
        assertEquals(1f, segment1.quotient(ONE_UP, 0f), FLT_EPSILON);
        assertEquals(0f, segment1.quotient(ONE_DOWN, 0f), FLT_EPSILON);
        assertEquals(0.5f * tan(0.125f * PI) + 0.5f, segment1.quotient(ZERO, 0.125f * PI), FLT_EPSILON);
        assertEquals(0.5f * tan(0.125f * PI) + 1f, segment1.quotient(ONE_UP, 0.125f * PI), FLT_EPSILON);
        assertEquals(0.5f * tan(0.125f * PI), segment1.quotient(ONE_DOWN, 0.125f * PI), FLT_EPSILON);
        assertEquals(0.5f - 0.5f * tan(0.125f * PI), segment1.quotient(ZERO, -0.125f * PI), FLT_EPSILON);
        assertEquals(1f - 0.5f * tan(0.125f * PI), segment1.quotient(ONE_UP, -0.125f * PI), FLT_EPSILON);
        assertEquals(-0.5f * tan(0.125f * PI), segment1.quotient(ONE_DOWN, -0.125f * PI), FLT_EPSILON);
        assertEquals(1f, segment1.quotient(ZERO, 0.25f * PI), FLT_EPSILON);
        assertEquals(0f, segment1.quotient(ZERO, -0.25f * PI), FLT_EPSILON);
    }

    @Test
    public void quotient2() {
        assertEquals(0.5f, segment2.quotient(ZERO, 0.25f * PI), FLT_EPSILON);
        assertEquals(0f, segment2.quotient(ZERO, 0f), FLT_EPSILON);
        assertEquals(1f, segment2.quotient(ZERO, 0.5f * PI), FLT_EPSILON);
        assertEquals(0.5f * SQRT2, segment2.quotient(ZERO, 0.375f * PI), FLT_EPSILON);
        assertEquals(1f - 0.5f * SQRT2, segment2.quotient(ZERO, 0.125f * PI), FLT_EPSILON);
    }

    @Test
    public void project() {
        assertEquals(0.5f, segment1.project(ZERO), FLT_EPSILON);
        assertEquals(0.5f, segment2.project(ZERO), FLT_EPSILON);
    }

    @Test
    public void minDistanceSquared1() {
        assertEquals(0f, segment1.minDistanceSquared(segment1), ZERO_TOLERANCE);

        assertEquals(0f, segment1.minDistanceSquared(segment2), ZERO_TOLERANCE);
        assertEquals(0f, segment2.minDistanceSquared(segment1), ZERO_TOLERANCE);

        assertEquals(1f, segment1.minDistanceSquared(segment3), ZERO_TOLERANCE);
        assertEquals(1f, segment3.minDistanceSquared(segment1), ZERO_TOLERANCE);

        assertEquals(sqr(SQRT2 - 1f), segment1.minDistanceSquared(segment4), ZERO_TOLERANCE);
        assertEquals(sqr(SQRT2 - 1f), segment4.minDistanceSquared(segment1), ZERO_TOLERANCE);

        assertEquals(0f, segment1.minDistanceSquared(segment5), ZERO_TOLERANCE);
        assertEquals(0f, segment5.minDistanceSquared(segment1), ZERO_TOLERANCE);

        assertEquals(0f, segment1.minDistanceSquared(segment6), ZERO_TOLERANCE);
        assertEquals(0f, segment6.minDistanceSquared(segment1), ZERO_TOLERANCE);

        assertEquals(0f, segment2.minDistanceSquared(segment2), ZERO_TOLERANCE);

        assertEquals(sqr(2f - SQRT2), segment2.minDistanceSquared(segment3), ZERO_TOLERANCE);
        assertEquals(sqr(2f - SQRT2), segment3.minDistanceSquared(segment2), ZERO_TOLERANCE);

        assertEquals(0f, segment2.minDistanceSquared(segment4), ZERO_TOLERANCE);
        assertEquals(0f, segment4.minDistanceSquared(segment2), ZERO_TOLERANCE);

        assertEquals(0f, segment2.minDistanceSquared(segment5), ZERO_TOLERANCE);
        assertEquals(0f, segment5.minDistanceSquared(segment2), ZERO_TOLERANCE);

        assertEquals(0f, segment2.minDistanceSquared(segment6), ZERO_TOLERANCE);
        assertEquals(0f, segment6.minDistanceSquared(segment2), ZERO_TOLERANCE);

        assertEquals(0f, segment3.minDistanceSquared(segment3), ZERO_TOLERANCE);

        assertEquals(sqr(2f - SQRT2), segment3.minDistanceSquared(segment4), ZERO_TOLERANCE);
        assertEquals(sqr(2f - SQRT2), segment4.minDistanceSquared(segment3), ZERO_TOLERANCE);

        assertEquals(0f, segment3.minDistanceSquared(segment5), ZERO_TOLERANCE);
        assertEquals(0f, segment5.minDistanceSquared(segment3), ZERO_TOLERANCE);

        assertEquals(sqr(2f - SQRT2), segment3.minDistanceSquared(segment6), ZERO_TOLERANCE);
        assertEquals(sqr(2f - SQRT2), segment6.minDistanceSquared(segment3), ZERO_TOLERANCE);

        assertEquals(0f, segment4.minDistanceSquared(segment4), ZERO_TOLERANCE);

        assertEquals(0f, segment4.minDistanceSquared(segment5), ZERO_TOLERANCE);
        assertEquals(0f, segment5.minDistanceSquared(segment4), ZERO_TOLERANCE);

        assertEquals(0f, segment4.minDistanceSquared(segment6), ZERO_TOLERANCE);
        assertEquals(0f, segment6.minDistanceSquared(segment4), ZERO_TOLERANCE);

        assertEquals(0f, segment5.minDistanceSquared(segment5), ZERO_TOLERANCE);

        assertEquals(0f, segment5.minDistanceSquared(segment6), ZERO_TOLERANCE);
        assertEquals(0f, segment6.minDistanceSquared(segment5), ZERO_TOLERANCE);

        assertEquals(0f, segment6.minDistanceSquared(segment6), ZERO_TOLERANCE);
    }

    @Test
    public void minDistanceSquared2() {
        final Segment s1 = new Segment(new FloatPoint(0f, 0f), new FloatPoint(2f, 1f));
        for (int i = -20; i <= 40; i++) {
            final float x = i * 0.1f;
            final Segment s2 = new Segment(new FloatPoint(x, 2f), new FloatPoint(x + 2f, -2f));
            final float dist;
            if (i <= -10)
                dist = 0.8f * sqr(1f + x);
            else if (i <= 15)
                dist = 0f;
            else
                dist = 0.8f * sqr(x - 1.5f);
            assertEquals("x = " + x, dist, s1.minDistanceSquared(s2), ZERO_TOLERANCE);
            assertEquals("x = " + x, dist, s2.minDistanceSquared(s1), ZERO_TOLERANCE);
        }
    }

    @Test
    public void minDistanceSquared3() {
        final Segment s1 = new Segment(new FloatPoint(0f, 0f), new FloatPoint(2f, 1f));
        for (float i = -30; i <= 30; i++) {
            final float x = i * 0.1f;
            final Segment s2 = new Segment(new FloatPoint(x, 0.5f * x), new FloatPoint(x + 2f, 0.5f * x + 1f));
            final float dist;
            if (i <= -20)
                dist = 1.25f * sqr(2f + x);
            else if (i <= 20)
                dist = 0f;
            else
                dist = 1.25f * sqr(x - 2f);
            assertEquals("x = " + x, dist, s1.minDistanceSquared(s2), ZERO_TOLERANCE);
            assertEquals("x = " + x, dist, s2.minDistanceSquared(s1), ZERO_TOLERANCE);
        }
    }

    @Test
    public void minDistanceSquared4() {
        final Segment s1 = new Segment(new FloatPoint(0f, 0f), new FloatPoint(3f, 1.5f));
        for (float i = -30; i <= 50; i++) {
            final float x = i * 0.1f;
            final float y = 1f - 0.5f * x;
            final Segment s2 = new Segment(new FloatPoint(x, 1f), new FloatPoint(x, 1f));
            final float dist;
            if (i <= -5)
                dist = sqr(x) + 1f;
            else if (i <= 32)
                dist = 0.2f * sqr(x - 2f);
            else
                dist = sqr(x - 3f) + 0.25f;
            assertEquals("x = " + x, dist, s1.minDistanceSquared(s2), ZERO_TOLERANCE);
            assertEquals("x = " + x, dist, s2.minDistanceSquared(s1), ZERO_TOLERANCE);
        }
    }
}
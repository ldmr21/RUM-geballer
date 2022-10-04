package pp.util.map;

import org.junit.Test;
import pp.util.Angle;
import pp.util.FloatPoint;
import pp.util.Interval;
import pp.util.Position;
import pp.util.TypedSegment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pp.util.FloatMath.sqrt;

public class TriangleTest {
    private static final float SQRT2 = sqrt(2f);
    private static final String CAT = "CAT"; //NON-NLS
    private static final TypedSegment S1 = new TypedSegment(p(2f, 2f), p(2.5f, 0f), CAT);
    private static final TypedSegment S2 = new TypedSegment(p(-1f, 3f), p(2f, 2f), CAT);
    private static final Triangle T1 = new Triangle(S1, null, Angle.fromDegrees(45f), Angle.ZERO,
                                                    2 * SQRT2, 2.5f, new Interval(0f, 1f));
    private static final Triangle T2 = new Triangle(S1, null, Angle.fromVector(-1f, 3f), Angle.fromVector(2f, 2f),
                                                    sqrt(10f), sqrt(8f), new Interval(0f, 1f));

    private static Position p(float x, float y) {
        return new FloatPoint(x, y);
    }

    @Test
    public void contains1() {
        assertTrue(T1.contains(0f, 0f));
        assertTrue(T1.contains(1f, 0f));
        assertTrue(T1.contains(2f, 0f));
        assertTrue(T1.contains(1f, 1f));
        assertTrue(T1.contains(2f, 1f));
        assertFalse(T1.contains(1f, 2f));
        assertFalse(T1.contains(2.5f, 2f));
        assertFalse(T1.contains(0f, 1f));
        assertFalse(T1.contains(0f, 2f));
        assertFalse(T1.contains(-0.5f, 1f));
        assertFalse(T1.contains(-0.5f, 2f));
        assertFalse(T1.contains(-0.5f, 1.5f));
        assertFalse(T1.contains(-1f, 3f));
        assertFalse(T1.contains(-2f, -1f));
        assertFalse(T1.contains(-2f, 0f));
        assertFalse(T1.contains(-2f, 1f));
    }

    @Test
    public void contains2() {
        assertTrue(T2.contains(0f, 0f));
        assertFalse(T2.contains(1f, 0f));
        assertFalse(T2.contains(2f, 0f));
        assertTrue(T2.contains(1f, 1f));
        assertFalse(T2.contains(2f, 1f));
        assertTrue(T2.contains(1f, 2f));
        assertFalse(T2.contains(2.5f, 2f));
        assertTrue(T2.contains(2f, 2f));
        assertTrue(T2.contains(0f, 1f));
        assertTrue(T2.contains(0f, 2f));
        assertFalse(T2.contains(-0.5f, 1f));
        assertTrue(T2.contains(-0.5f, 2f));
        assertTrue(T2.contains(-0.5f, 1.5f));
        assertTrue(T2.contains(-1f, 3f));
        assertFalse(T2.contains(-2f, -1f));
        assertFalse(T2.contains(-2f, 0f));
        assertFalse(T2.contains(-2f, 1f));
    }
}
package pp.util.map;

import org.junit.Test;
import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.TypedSegment;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static pp.util.FloatMath.PI;
import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatMath.atan2;

public class ObservationTest {
    private static final float EPS = ZERO_TOLERANCE;
    public static final String WALL = "WALL"; //NON-NLS

    private static TypedSegment seg(Position f, Position t) {
        return new TypedSegment(f, t, WALL);
    }

    public static final List<TypedSegment> SEGMENTS1 = List.of(seg(p(-1f, 1f), p(1f, 1f)),
                                                               seg(p(1f, -1.5f), p(-1f, -1.5f)),
                                                               seg(p(1f, -2.5f), p(-1f, -2.5f)),
                                                               seg(p(2.5f, -2f), p(-1f, -2f)),
                                                               seg(p(1f, 0.5f), p(1f, -1f)),
                                                               seg(p(0f, -1f), p(-1.5f, 0.5f)));
    public static final float[][] ANGLES1 = {
            {
                    0.25f * PI,
                    0.75f * PI,
            },
            {
                    0.25f * PI,
                    0.75f * PI,
                    atan2(-1.5f, -1f),
                    atan2(-1.5f, 1f),
            },
            {
                    0.25f * PI,
                    0.75f * PI,
                    atan2(-1.5f, -1f),
                    atan2(-1.5f, 1f),
            },
            {
                    0.25f * PI,
                    0.75f * PI,
                    atan2(-1.5f, -1f),
                    atan2(-1.5f, 1f),
                    atan2(-2f, 2.5f),
            },
            {
                    atan2(0.5f, 1f),
                    0.25f * PI,
                    0.75f * PI,
                    atan2(-1.5f, -1f),
                    atan2(-1.5f, 1f),
                    -0.25f * PI,
            },
            {
                    atan2(0.5f, 1f),
                    0.25f * PI,
                    0.75f * PI,
                    atan2(0.5f, -1.5f),
                    -0.5f * PI,
                    atan2(-1.5f, 1f),
                    -0.25f * PI,
            }
    };

    public static final int[][] SEGS1 = {
            {0, -1},
            {0, -1, 1, -1},
            {0, -1, 1, -1},
            {0, -1, 1, 3, -1},
            {-1, 0, -1, 1, 3, 4},
            {-1, 0, -1, 5, 1, 3, 4}
    };
    public static final List<TypedSegment> SEGMENTS2 = List.of(seg(p(-1f, 1f), p(1f, 1f)),
                                                               seg(p(1f, -1f), p(-1f, -1f)),
                                                               seg(p(1f, -2f), p(-1f, -2f)),
                                                               seg(p(2.5f, -1.5f), p(-1f, -1.5f)),
                                                               seg(p(1f, 0.5f), p(1f, -1f)),
                                                               seg(p(0f, -1f), p(-1.5f, 0.5f)),
                                                               seg(p(-1.5f, 0.5f), p(-1.5f, 1.5f)),
                                                               seg(p(-1.5f, 0.5f), p(-1f, 1f)));
    public final float[][] ANGLES2 = {
            {
                    0.25f * PI,
                    0.75f * PI,
            },
            {
                    0.25f * PI,
                    0.75f * PI,
                    -0.75f * PI,
                    -0.25f * PI,
            },
            {
                    0.25f * PI,
                    0.75f * PI,
                    -0.75f * PI,
                    -0.25f * PI,
            },
            {
                    0.25f * PI,
                    0.75f * PI,
                    -0.75f * PI,
                    -0.25f * PI,
                    atan2(-1.5f, 2.5f),
            },
            {
                    atan2(0.5f, 1f),
                    0.25f * PI,
                    0.75f * PI,
                    -0.75f * PI,
                    -0.25f * PI,
            },
            {
                    atan2(0.5f, 1f),
                    0.25f * PI,
                    0.75f * PI,
                    atan2(0.5f, -1.5f),
                    -0.5f * PI,
                    -0.25f * PI,
            },
            {
                    atan2(0.5f, 1f),
                    0.25f * PI,
                    0.75f * PI,
                    atan2(0.5f, -1.5f),
                    -0.5f * PI,
                    -0.25f * PI,
            },
            {
                    atan2(0.5f, 1f),
                    0.25f * PI,
                    0.75f * PI,
                    atan2(0.5f, -1.5f),
                    -0.5f * PI,
                    -0.25f * PI,
            }
    };

    public static final int[][] SEGS2 = {
            {0, -1},
            {0, -1, 1, -1},
            {0, -1, 1, -1},
            {0, -1, 1, 3, -1},
            {-1, 0, -1, 1, 4},
            {-1, 0, -1, 5, 1, 4},
            {-1, 0, 6, 5, 1, 4},
            {-1, 0, 7, 5, 1, 4}
    };

    public static final List<TypedSegment> SEGMENTS3 = List.of(seg(p(-1f, 1f), p(1f, 1f)),
                                                               seg(p(1f, 1f), p(1f, -1f)),
                                                               seg(p(1f, -1f), p(-1f, -1f)),
                                                               seg(p(-1f, -1f), p(-1f, 1f)));
    public static final float[] ANGLES3 = {
            0.25f * PI, 0.75f * PI, -0.75f * PI, -0.25f * PI,
    };

    public static final int[] SEGS3 = {0, 3, 2, 1};

    @Test
    public void analyze1() {
        checkAll(ANGLES1, SEGS1, SEGMENTS1);
    }

    @Test
    public void analyze2() {
        checkAll(ANGLES2, SEGS2, SEGMENTS2);
    }

    @Test
    public void analyze3() {
        check(ANGLES3, SEGS3, SEGMENTS3, 3, WALL);
    }

    private void checkAll(float[][] angles, int[][] segs, List<TypedSegment> segments) {
        assertEquals(angles.length, segs.length);
        assertEquals(angles.length, segments.size());
        for (int i = 0; i < angles.length; i++) {
            check(angles[i], segs[i], segments.subList(0, i + 1), i, null);
        }
    }

    private static Position p(float x, float y) {
        return new FloatPoint(x + 0.5f, y + 1.7f);
    }

    private void check(float[] angles, int[] segs, List<TypedSegment> segments, int no, String cat) {
        assertEquals(angles.length, segs.length);
        final Observation obs = new Observation(p(0, 0), PI, 10f, segments, Collections.emptyMap());
        final List<Triangle> triangles = obs.getTriangles();
        assertEquals("num angles at " + no, angles.length, (Object) triangles.size());
        final Iterator<Triangle> it = triangles.iterator();
        for (int i = 0; i < angles.length; i++) {
            final Triangle t = it.next();
            final TypedSegment seg = segs[i] < 0 ? null : segments.get(segs[i]);
            assertEquals(no + "/" + i, seg, t.getSegment());
            assertEquals(no + "/" + i + " right", normalize(angles[i]), t.rightAngle().radians(), EPS);
            assertEquals(no + "/" + i + " left", normalize(angles[(i + 1) % angles.length]), t.leftAngle().radians(), EPS);
            if (cat != null)
                assertEquals(cat, t.cat());
        }
    }

    private static float normalize(float angle) {
        return angle;
    }
}
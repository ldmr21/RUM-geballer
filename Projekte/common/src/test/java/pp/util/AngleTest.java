package pp.util;

import org.junit.Test;

import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;
import static pp.util.FloatMath.DEG_TO_RAD;
import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

public class AngleTest {
    @Test
    public void compareAngles() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                assertEquals("compare " + i + "° and " + j + "°", Integer.compare(i, j), (Object) u.compareTo(v));
            }
        }
    }

    @Test
    public void addAngles() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                final Angle sum = u.plus(v);
                assertEquals(i + "° + " + j + "°, x coordinate", cos((i + j) * DEG_TO_RAD), sum.x, ZERO_TOLERANCE);
                assertEquals(i + "° + " + j + "°, y coordinate", sin((i + j) * DEG_TO_RAD), sum.y, ZERO_TOLERANCE);
            }
        }
    }

    @Test
    public void subtractAngles() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                final Angle diff = u.minus(v);
                assertEquals(i + "° - " + j + "°, x coordinate", cos((i - j) * DEG_TO_RAD), diff.x, ZERO_TOLERANCE);
                assertEquals(i + "° - " + j + "°, y coordinate", sin((i - j) * DEG_TO_RAD), diff.y, ZERO_TOLERANCE);
            }
        }
    }

    @Test
    public void minAngle() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                final Angle diff = Angle.min(u, v);
                assertEquals(i + "° - " + j + "°, x coordinate", cos(min(i, j) * DEG_TO_RAD), diff.x, ZERO_TOLERANCE);
                assertEquals(i + "° - " + j + "°, y coordinate", sin(min(i, j) * DEG_TO_RAD), diff.y, ZERO_TOLERANCE);
            }
        }
    }
}
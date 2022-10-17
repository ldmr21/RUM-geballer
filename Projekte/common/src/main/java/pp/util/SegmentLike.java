package pp.util;

import static java.lang.Float.max;
import static java.lang.Float.min;
import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatMath.abs;
import static pp.util.FloatMath.atan2;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;
import static pp.util.FloatMath.sqr;
import static pp.util.FloatMath.sqrt;

/**
 * Interface of geometrical objects like segments, i.e., having a start and an end position.
 */
public interface SegmentLike {
    /**
     * Returns the start position of the segment.
     */
    Position from();

    /**
     * Returns the end position of the segment.
     */
    Position to();

    /**
     * Returns the length, i.e., the distance between the start and the end point of this segment.
     *
     * @return length of the line segment
     */
    default float length() {
        return sqrt(lengthSquared());
    }

    /**
     * Returns the length squared where the length is the distance between the start and the
     * end point of this segment.
     *
     * @return length squared of the line segment
     */
    default float lengthSquared() {
        final float dx = to().getX() - from().getX();
        final float dy = to().getY() - from().getY();
        return dx * dx + dy * dy;
    }

    /**
     * Returns the angle of this line segment with the x-axis.
     *
     * @return angle with the x-axis
     */
    default float angle() {
        final float dx = to().getX() - from().getX();
        final float dy = to().getY() - from().getY();
        return atan2(dy, dx);
    }

    /**
     * Returns the distance of the specified point from this segment.
     * The distance is the length of the shortest connection between the specified
     * point and any point of the line segment.
     *
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     * @return the distance
     */
    default float distanceTo(float x, float y) {
        return distance(from().getX(), from().getY(), to().getX(), to().getY(), x, y);
    }

    /**
     * Returns the distance of the point from the segment between the specified points.
     * The distance is the length of the shortest connection between the specified
     * point and any point of the line segment.
     *
     * @param from the segment start point
     * @param to   the segment end point
     * @param p    the point
     * @return the distance
     */
    static float distance(Position from, Position to, Position p) {
        return distance(from.getX(), from.getY(), to.getX(), to.getY(), p.getX(), p.getY());
    }

    /**
     * Returns the distance of the point (x,y) from the segment from (x1,y1) to (x2,y2)
     * The distance is the length of the shortest connection between the specified
     * point and any point of the line segment.
     *
     * @param x1 x-coordinate of the segment start point
     * @param y1 y-coordinate of the segment start point
     * @param x2 x-coordinate of the segment end point
     * @param y2 y-coordinate of the segment end point
     * @param x  x-coordinate of the point
     * @param y  y-coordinate of the point
     * @return the distance
     */
    static float distance(float x1, float y1, float x2, float y2, float x, float y) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float dx1 = x - x1;
        final float dy1 = y - y1;
        if (dx * dx1 + dy * dy1 <= 0f)
            return sqrt(dx1 * dx1 + dy1 * dy1);
        final float dx2 = x - x2;
        final float dy2 = y - y2;
        if (dx * dx2 + dy * dy2 >= 0f)
            return sqrt(dx2 * dx2 + dy2 * dy2);
        final float len = sqrt(dx * dx + dy * dy);
        return FloatMath.abs(dx1 * dy - dy1 * dx) / len;
    }

    /**
     * Returns the distance of the specified position from this segment.
     * This is just a convenience method for {@linkplain #distanceTo(float, float)}.
     *
     * @param pos a position
     * @return the distance
     */
    default float distanceTo(Position pos) {
        return distanceTo(pos.getX(), pos.getY());
    }

    /**
     * Returns the point of this segment with the specified quotient, i.e., q*from()+(1-q)*to().
     *
     * @param q the quotient
     */
    default Position pointAt(float q) {
        if (q == 0f) return from();
        if (q == 1f) return to();
        return new FloatPoint((1f - q) * from().getX() + q * to().getX(),
                              (1f - q) * from().getY() + q * to().getY());
    }

    /**
     * Shoots a ray from the specified position in the direction of the specified angle and returns the distance
     * of the specified position from the intersection point of the ray with the straight line determined by the
     * end points of this segment. Returns {@linkplain Float#NaN} if there is no intersection.
     *
     * @param pos   the specified position
     * @param angle the specified angle
     */
    default float dist(Position pos, float angle) {
        return quotientDist(pos, quotient(pos, angle));
    }

    /**
     * Shoots a ray from the specified position in the direction of the specified angle and returns the distance
     * of the specified position from the intersection point of the ray with the straight line determined by the
     * end points of this segment. Returns {@linkplain Float#NaN} if there is no intersection.
     *
     * @param pos   the specified position
     * @param angle the specified angle
     */
    default float dist(Position pos, Angle angle) {
        return quotientDist(pos, quotient(pos, angle.x, angle.y));
    }

    /**
     * Shoots a ray from the specified position in the direction of the point of this segment with the specified
     * quotient, i.e., the point at q*from()+(1-q)*to(), and returns the distance
     * of the specified position from the intersection point of the ray with the straight line determined by the
     * end points of this segment. Returns {@linkplain Float#NaN} if there is no intersection.
     *
     * Ich habe die Getter-Methode von from().getX in float dy auf from().getY() geändert.
     *
     * @param pos the specified position
     * @param q   the specified quotient
     */
    default float quotientDist(Position pos, float q) {
        final float dx = (1f - q) * from().getX() + q * to().getX() - pos.getX();
        final float dy = (1f - q) * from().getY() + q * to().getY() - pos.getY();
        return sqrt(dx * dx + dy * dy);
    }

    /**
     * Shoots a ray from the specified position in the direction of the specified angle and returns the
     * quotient q such that the intersection point of the ray with the straight line determined by the
     * end points of this segment is at q*from()+(1-q)*to().
     *
     * @param pos   the specified position
     * @param angle the specified angle
     */
    default float quotient(Position pos, float angle) {
        final float ux = cos(angle);
        final float uy = sin(angle);
        return quotient(pos, ux, uy);
    }

    /**
     * Shoots a ray from the specified position in the direction of the specified vector and returns the
     * quotient q such that the intersection point of the ray with the straight line determined by the
     * end points of this segment is at q*from()+(1-q)*to().
     *
     * @param pos the specified position
     * @param ux  the vectors x value
     * @param uy  the vectors y value
     */
    private float quotient(Position pos, float ux, float uy) {
        final float nom = nominator(pos, ux, uy);
        final float det = determinant(ux, uy);
        // the following is for dealing with floating point imprecision
        if (abs(det) > FLT_EPSILON)
            return nom / det;
        if (abs(nom) > FLT_EPSILON)
            return Float.NaN;
        final float q = project(pos);
        if (q > -FLT_EPSILON && q - 1f < FLT_EPSILON)
            // pos lies (almost) within the segment
            return q;
        final float distFrom = isCandidate(pos, ux, uy, from());
        final float distTo = isCandidate(pos, ux, uy, to());
        if (distFrom >= 0f) {
            if (distTo >= 0f)
                return distFrom < distTo ? 0f : 1f;
            else
                return 0f;
        }
        if (distTo >= 0f)
            return 1f;
        return Float.NaN;
    }

    /**
     * Returns the determinant of a specified vector.
     *
     * @param ux the vectors x value
     * @param uy the vectors y value
     */
    private float determinant(float ux, float uy) {
        return diffX() * uy - diffY() * ux;
    }

    /**
     * Returns the nominator of the specified vector starting at a specified position.
     *
     * @param pos the specified position
     * @param ux  the vectors x value
     * @param uy  the vectors y value
     */
    private float nominator(Position pos, float ux, float uy) {
        final float dx = pos.getX() - from().getX();
        final float dy = pos.getY() - from().getY();
        return dx * uy - dy * ux;
    }

    /**
     * Checks whether the (ux,uy) ray starting at pos hits (or almost hits) target.
     *
     * @param pos    the specified start position
     * @param ux     the rays x value
     * @param uy     the rays y value
     * @param target the specified target position
     */
    private float isCandidate(Position pos, float ux, float uy, Position target) {
        final float lambda = lambda(pos, target, ux, uy);
        if (lambda < -FLT_EPSILON) return -1f;
        final float dx = target.getX() - pos.getX() - lambda * ux;
        final float dy = target.getY() - pos.getY() - lambda * uy;
        return dx * dx + dy * dy;
    }

    private float lambda(Position p1, Position p2, float ux, float uy) {
        return ux * (p2.getX() - p1.getX()) + uy * (p2.getY() - p1.getY());
    }

    /**
     * Returns the quotient q such that the specified point projected onto this
     * segment is at q*from()+(1-q)*to().
     *
     * @param pos the specified points position
     */
    default float project(Position pos) {
        return ((pos.getX() - from().getX()) * diffX() + (pos.getY() - from().getY()) * diffY()) / lengthSquared();
    }

    /**
     * Returns the interval of quotients between leftAngle and rightAngle
     * looking from the specified position. The starting point of the
     * segment must be left of its end point when looking from the
     * specified position.
     *
     * @param pos        the specified position to look from
     * @param leftAngle  the specified left angle
     * @param rightAngle the specified right angle
     */
    default Interval interval(Position pos, Angle leftAngle, Angle rightAngle) {
        final float nomLeft = nominator(pos, leftAngle.x, leftAngle.y);
        final float detLeft = determinant(leftAngle.x, leftAngle.y);
        final float nomRight = nominator(pos, rightAngle.x, rightAngle.y);
        final float detRight = determinant(rightAngle.x, rightAngle.y);
        if (abs(detLeft) <= FLT_EPSILON || abs(detRight) <= FLT_EPSILON)
            return new Interval(0f, 0f);
        final float q1 = nomLeft / detLeft;
        final float q2 = nomRight / detRight;
        if (q1 > q2)
            return new Interval(0f, 0f);
        final float lower = q1 < ZERO_TOLERANCE ? 0f : min(1f, q1);
        final float upper = q2 > 1f - ZERO_TOLERANCE ? 1f : max(0f, q2);
        return new Interval(lower, upper);
    }

    /**
     * Returns the x-coordinate of the vector from the start to the end point.
     */
    default float diffX() {
        return to().getX() - from().getX();
    }

    /**
     * Returns the y-coordinate of the vector from the start to the end point.
     */
    default float diffY() {
        return to().getY() - from().getY();
    }

    /**
     * Computes the determinant of the matrix whose first column vector is this segment and the
     * second column vector is the specified segment.
     *
     * @param other the specified segment
     */
    default float determinantWith(SegmentLike other) {
        return diffX() * other.diffY() - diffY() * other.diffX();
    }

    /**
     * Computes the square of the minimal distance between this and the specified segment.
     *
     * @param other other segment
     * @return squared distance
     */
    default float minDistanceSquared(SegmentLike other) {
        final float rx = other.from().getX() - from().getX();
        final float ry = other.from().getY() - from().getY();
        final float ux = diffX();
        final float uy = diffY();
        final float vx = other.diffX();
        final float vy = other.diffY();

        final float ru = rx * ux + ry * uy;
        final float rv = rx * vx + ry * vy;
        final float uu = ux * ux + uy * uy;
        final float uv = ux * vx + uy * vy;
        final float vv = vx * vx + vy * vy;

        if (uu < ZERO_TOLERANCE) // this segment is in fact a single point
            if (vv < ZERO_TOLERANCE) // other is a point, too
                return rx * rx + ry * ry;
            else
                return sqr(other.distanceTo(from()));
        if (vv < ZERO_TOLERANCE) // other is in fact a point
            return sqr(distanceTo(other.from()));

        final float det = uu * vv - uv * uv;
        final float s;
        final float t;

        if (det < ZERO_TOLERANCE * uu * vv) {
            s = min(max(ru / uu, 0f), 1f);
            t = 0f;
        }
        else {
            s = min(max((ru * vv - rv * uv) / det, 0f), 1f);
            t = min(max((ru * uv - rv * uu) / det, 0f), 1f);
        }

        final float mu1 = min(max((t * uv + ru) / uu, 0f), 1f);
        final float mu2 = min(max((s * uv - rv) / vv, 0f), 1f);

        return pointAt(mu1).distanceSquaredTo(other.pointAt(mu2));
    }
}
package pp.util;

public class FloatMath {
    private FloatMath() { /* don't instantiate */ }

    public static final double DBL_EPSILON = 2.220446049250313E-16d;
    /**
     * A "close to zero" float epsilon value for use
     */
    public static final float FLT_EPSILON = 1.1920928955078125E-7f;
    /**
     * A "close to zero" float epsilon value for use
     */
    public static final float ZERO_TOLERANCE = 0.0001f;
    /**
     * The value 1/3, as a float.
     */
    public static final float ONE_THIRD = 1f / 3f;
    /**
     * The value PI as a float. (180 degrees)
     */
    public static final float PI = (float) Math.PI;
    /**
     * The value 2PI as a float. (360 degrees)
     */
    public static final float TWO_PI = 2.0f * PI;
    /**
     * The value PI/2 as a float. (90 degrees)
     */
    public static final float HALF_PI = 0.5f * PI;
    /**
     * The value PI/4 as a float. (45 degrees)
     */
    public static final float QUARTER_PI = 0.25f * PI;
    /**
     * The value 1/PI as a float.
     */
    public static final float INV_PI = 1.0f / PI;
    /**
     * The value 1/(2PI) as a float.
     */
    public static final float INV_TWO_PI = 1.0f / TWO_PI;
    /**
     * A value to multiply a degree value by, to convert it to radians.
     */
    public static final float DEG_TO_RAD = PI / 180.0f;
    /**
     * A value to multiply a radian value by, to convert it to degrees.
     */
    public static final float RAD_TO_DEG = 180.0f / PI;

    /**
     * Linear interpolation from startValue to endValue by the given percent.
     * Basically: ((1 - percent) * startValue) + (percent * endValue)
     *
     * @param scale      scale value to use. if 1, use endValue, if 0, use startValue.
     * @param startValue Beginning value. 0% of f
     * @param endValue   ending value. 100% of f
     * @return The interpolated value between startValue and endValue.
     */
    public static float interpolateLinear(float scale, float startValue, float endValue) {
        if (startValue == endValue) {
            return startValue;
        }
        if (scale <= 0f) {
            return startValue;
        }
        if (scale >= 1f) {
            return endValue;
        }
        return ((1f - scale) * startValue) + (scale * endValue);
    }

    /**
     * Linear extrapolation from startValue to endValue by the given scale.
     * if scale is between 0 and 1 this method returns the same result as interpolateLinear
     * if the scale is over 1 the value is linearly extrapolated.
     * Note that the end value is the value for a scale of 1.
     *
     * @param scale      the scale for extrapolation
     * @param startValue the starting value (scale = 0)
     * @param endValue   the end value (scale = 1)
     * @return an extrapolation for the given parameters
     */
    public static float extrapolateLinear(float scale, float startValue, float endValue) {
        return ((1f - scale) * startValue) + (scale * endValue);
    }

    /**
     * Returns the arc cosine of a value.<br>
     * Special cases:
     * <ul><li>If fValue is smaller than -1, then the result is PI.
     * <li>If the argument is greater than 1, then the result is 0.</ul>
     *
     * @param fValue The value to arc cosine.
     * @return The angle, in radians.
     * @see Math#acos(double)
     */
    public static float acos(float fValue) {
        if (-1.0f < fValue) {
            if (fValue < 1.0f) {
                return (float) Math.acos(fValue);
            }

            return 0.0f;
        }

        return PI;
    }

    /**
     * Returns the arc sine of a value.<br>
     * Special cases:
     * <ul><li>If fValue is smaller than -1, then the result is -HALF_PI.
     * <li>If the argument is greater than 1, then the result is HALF_PI.</ul>
     *
     * @param fValue The value to arc sine.
     * @return the angle in radians.
     * @see Math#asin(double)
     */
    public static float asin(float fValue) {
        if (-1.0f < fValue) {
            if (fValue < 1.0f) {
                return (float) Math.asin(fValue);
            }

            return HALF_PI;
        }

        return -HALF_PI;
    }

    /**
     * Returns the arc tangent of an angle given in radians.<br>
     *
     * @param fValue The angle, in radians.
     * @return fValue's atan
     * @see Math#atan(double)
     */
    public static float atan(float fValue) {
        return (float) Math.atan(fValue);
    }

    /**
     * A direct call to Math.atan2.
     *
     * @param fY ordinate
     * @param fX abscissa
     * @return Math.atan2(fY, fX)
     * @see Math#atan2(double, double)
     */
    public static float atan2(float fY, float fX) {
        return (float) Math.atan2(fY, fX);
    }

    /**
     * Rounds a fValue up. A call to Math.ceil
     *
     * @param fValue The value.
     * @return The fValue rounded up
     * @see Math#ceil(double)
     */
    public static float ceil(float fValue) {
        return (float) Math.ceil(fValue);
    }

    /**
     * Returns cosine of an angle. Direct call to Math
     *
     * @param v The angle to cosine.
     * @return the cosine of the angle.
     * @see Math#cos(double)
     */
    public static float cos(float v) {
        return (float) Math.cos(v);
    }

    /**
     * Returns the sine of an angle. Direct call to Math
     *
     * @param v The angle to sine.
     * @return the sine of the angle.
     * @see Math#sin(double)
     */
    public static float sin(float v) {
        return (float) Math.sin(v);
    }

    /**
     * Returns E^fValue
     *
     * @param fValue Value to raise to a power.
     * @return The value E^fValue
     * @see Math#exp(double)
     */
    public static float exp(float fValue) {
        return (float) Math.exp(fValue);
    }

    /**
     * Returns Absolute value of a float.
     *
     * @param fValue The value to abs.
     * @return The abs of the value.
     * @see Math#abs(float)
     */
    public static float abs(float fValue) {
        if (fValue < 0) {
            return -fValue;
        }
        return fValue;
    }

    /**
     * Returns a number rounded down.
     *
     * @param fValue The value to round
     * @return The given number rounded down
     * @see Math#floor(double)
     */
    public static float floor(float fValue) {
        return (float) Math.floor(fValue);
    }

    /**
     * Returns 1/sqrt(fValue)
     *
     * @param fValue The value to process.
     * @return 1/sqrt(fValue)
     * @see Math#sqrt(double)
     */
    public static float invSqrt(float fValue) {
        return (float) (1.0f / Math.sqrt(fValue));
    }

    /**
     * Quickly estimate 1/sqrt(fValue).
     *
     * @param x the input value (&ge;0)
     * @return an approximate value for 1/sqrt(x)
     */
    public static float fastInvSqrt(float x) {
        float halfX = 0.5f * x;
        int i = Float.floatToIntBits(x); // get bits for floating value
        i = 0x5f375a86 - (i >> 1); // gives initial guess y0
        x = Float.intBitsToFloat(i); // convert bits back to float
        x = x * (1.5f - halfX * x * x); // Newton step, repeating increases accuracy
        return x;
    }

    /**
     * Returns the log base E of a value.
     *
     * @param fValue The value to log.
     * @return The log of fValue base E
     * @see Math#log(double)
     */
    public static float log(float fValue) {
        return (float) Math.log(fValue);
    }

    /**
     * Returns a number raised to an exponent power. fBase^fExponent
     *
     * @param fBase     The base value (IE 2)
     * @param fExponent The exponent value (IE 3)
     * @return base raised to exponent (IE 8)
     * @see Math#pow(double, double)
     */
    public static float pow(float fBase, float fExponent) {
        return (float) Math.pow(fBase, fExponent);
    }

    /**
     * Returns the value squared. fValue ^ 2
     *
     * @param fValue The value to square.
     * @return The square of the given value.
     */
    public static float sqr(float fValue) {
        return fValue * fValue;
    }

    /**
     * Returns the square root of a given value.
     *
     * @param fValue The value to sqrt.
     * @return The square root of the given value.
     * @see Math#sqrt(double)
     */
    public static float sqrt(float fValue) {
        return (float) Math.sqrt(fValue);
    }

    /**
     * Returns the tangent of the specified angle.
     *
     * @param fValue The value to tangent, in radians.
     * @return The tangent of fValue.
     * @see Math#tan(double)
     */
    public static float tan(float fValue) {
        return (float) Math.tan(fValue);
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0 otherwise
     *
     * @param iValue The integer to examine.
     * @return The integer's sign.
     */
    public static int sign(int iValue) {
        return Integer.compare(iValue, 0);
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0 otherwise
     *
     * @param fValue The float to examine.
     * @return The float's sign.
     */
    public static float sign(float fValue) {
        return Math.signum(fValue);
    }

    /**
     * Take a float input and clamp it between min and max.
     *
     * @param input the value to be clamped
     * @param min   the minimum output value
     * @param max   the maximum output value
     * @return clamped input
     */
    public static float clamp(float input, float min, float max) {
        return Math.max(min, Math.min(input, max));
    }

    /**
     * Clamps the given float to be between 0 and 1.
     *
     * @param input the value to be clamped
     * @return input clamped between 0 and 1.
     */
    public static float saturate(float input) {
        return clamp(input, 0f, 1f);
    }

    /**
     * Determine if two floats are approximately equal.
     * This takes into account the magnitude of the floats, since
     * large numbers will have larger differences be close to each other.
     * <p>
     * Should return true for a=100000, b=100001, but false for a=10000, b=10001.
     *
     * @param a The first float to compare
     * @param b The second float to compare
     * @return True if a and b are approximately equal, false otherwise.
     */
    public static boolean approximateEquals(float a, float b) {
        if (a == b) {
            return true;
        }
        else {
            return (abs(a - b) / Math.max(abs(a), abs(b))) <= 0.00001f;
        }
    }
}

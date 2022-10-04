package pp.droids.view;

import com.jme3.math.Vector3f;
import pp.util.FloatPoint;
import pp.util.Position;

/**
 * Class providing methods for transformation from model to view coordinates
 * and vice versa.
 */
public class CoordinateTransformation {
    private CoordinateTransformation() {
        // do not instantiate
    }

    public static float modelToViewX(Position pos) {
        return modelToViewX(pos.getX(), pos.getY());
    }

    public static float modelToViewX(float x, float y) {
        return y;
    }

    public static float modelToViewY(Position pos) {
        return 0f;
    }

    public static float modelToViewY(float x, float y) {
        return 0f;
    }

    public static float modelToViewZ(Position pos) {
        return modelToViewZ(pos.getX(), pos.getY());
    }

    public static float modelToViewZ(float x, float y) {
        return x;
    }

    public static Vector3f modelToView(Position pos) {
        return new Vector3f(modelToViewX(pos), modelToViewY(pos), modelToViewZ(pos));
    }

    public static float viewToModelX(Vector3f vec) {
        return vec.getZ();
    }

    public static float viewToModelY(Vector3f vec) {
        return vec.getX();
    }

    /**
     * Returns the model coordinates of the specified vector in view coordinates
     * if the vector belongs to the floor.
     *
     * @param vec vector in view coordinates
     */
    public static Position viewToModel(Vector3f vec) {
        return new FloatPoint(viewToModelX(vec), viewToModelY(vec));
    }

    /**
     * Returns the view coordinates of the specified vector in model coordinates
     *
     * @param x x-component of the model coordinates
     * @param y y-component of the model coordinates
     */
    public static Vector3f modelToView(float x, float y) {
        return new Vector3f(modelToViewX(x, y),
                            modelToViewY(x, y),
                            modelToViewZ(x, y));
    }
}

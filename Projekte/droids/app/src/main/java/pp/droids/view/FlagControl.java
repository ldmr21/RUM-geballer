package pp.droids.view;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.Flag;

import static com.jme3.math.FastMath.sin;
import static com.jme3.math.Vector3f.UNIT_Y;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;
import static pp.util.Angle.normalizeAngle;

/**
 * This class controls the robot.
 */
class FlagControl extends AbstractControl {
    private static final float DISP = 0.5f;
    private static final float OMEGA = 2f;
    private static final float AMPLITUDE = 0.6f;
    private final Flag flag;
    private float time;

    /**
     * Constructor to set up the flag.
     *
     * @param flag given flag
     */
    public FlagControl(Flag flag) {
        this.flag = flag;
    }

    /**
     * Updates the position of the robot.
     *
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        time += tpf;
        if (spatial != null) {
            final float angle = flag.getRotation() + AMPLITUDE * sin(normalizeAngle(OMEGA * time));
            spatial.getLocalRotation().fromAngleAxis(angle, UNIT_Y);
            final float dh = flag.getCaptor() == null ? 0f : DISP;
            spatial.setLocalTranslation(modelToViewX(flag),
                                        modelToViewY(flag) + dh,
                                        modelToViewZ(flag));
        }
    }

    /**
     * Does nothing.
     *
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing
    }
}

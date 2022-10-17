package pp.droids.view;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.DamageReceiver;

import static com.jme3.math.FastMath.PI;
import static com.jme3.math.Vector3f.UNIT_Y;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

/**
 * This class controls the robot.
 */
class RobotControl extends AbstractControl {
    private static final float FLASH_TIME = 1f;
    private static final float FLASH_INTERVAL = .1f;
    private final DamageReceiver robot;
    private CullHint hint;
    private boolean visible = true;

    /**
     * Constructor to set up the robot.
     * @param robot given robot
     */
    public RobotControl(DamageReceiver robot) {
        this.robot = robot;
    }

    /**
     * Updates the position of the robot.
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            final float angle = PI + robot.getRotation();
            spatial.getLocalRotation().fromAngleAxis(angle, UNIT_Y);
            spatial.setLocalTranslation(modelToViewX(robot),
                                        modelToViewY(robot),
                                        modelToViewZ(robot));
            if (hint == null)
                hint = spatial.getCullHint();
            else if (isRobotVisible() != visible) {
                if (visible)
                    spatial.setCullHint(CullHint.Always);
                else
                    spatial.setCullHint(hint);
                visible = isRobotVisible();
            }
        }
    }

    /**
     * Shows if the robot is visible
     * @return boolean if the robot is visible
     */
    private boolean isRobotVisible() {
        if (robot.getTimeSinceLastHit() < 0f || robot.getTimeSinceLastHit() > FLASH_TIME)
            return true;
        return Math.round(robot.getTimeSinceLastHit() / FLASH_INTERVAL) % 2f == 0;
    }

    /**
     * Does nothing.
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothingzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz
    }
}

package pp.droids.view.radar;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.Droid;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * This class controls mazes.
 */
class MazeControl extends AbstractControl {
    private final Droid droid;

    /**
     * Initializes the droid.
     * @param droid the particular droid.
     */
    public MazeControl(Droid droid) {
        this.droid = droid;
    }

    /**
     * Updates the position of a spatial.
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            final float sin = FastMath.sin(droid.getRotation());
            final float cos = FastMath.cos(droid.getRotation());
            final float dx = -droid.getX();
            final float dy = -droid.getY();
            Quaternion quat = new Quaternion();
            quat.fromAngleAxis(HALF_PI - droid.getRotation(), Vector3f.UNIT_Z);
            spatial.setLocalRotation(quat);
            spatial.setLocalTranslation(dx * sin - dy * cos,
                                        dx * cos + dy * sin,
                                        0);
        }
    }

    /**
     * Does nothing.
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing
    }
}

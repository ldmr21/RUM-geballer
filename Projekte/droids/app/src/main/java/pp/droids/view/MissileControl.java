package pp.droids.view;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.BoundedItem;

import static com.jme3.math.FastMath.PI;
import static com.jme3.math.Vector3f.UNIT_Y;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

/**
 * Class to control missiles
 */
class MissileControl extends AbstractControl {
    private final BoundedItem missile;
    private final float height;

    /**
     * Constructor to set the missile and its height.
     *
     * @param missile the given missile
     * @param height  height of the missile
     */
    public MissileControl(BoundedItem missile, float height) {
        this.missile = missile;
        this.height = height;
    }

    /**
     * Updates the missile position.
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            final float angle = PI + missile.getRotation();
            spatial.getLocalRotation().fromAngleAxis(angle, UNIT_Y);
            spatial.setLocalTranslation(modelToViewX(missile),
                                        modelToViewY(missile) + height,
                                        modelToViewZ(missile));
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

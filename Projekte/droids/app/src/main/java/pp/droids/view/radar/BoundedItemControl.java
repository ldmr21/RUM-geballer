package pp.droids.view.radar;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.BoundedItem;
import pp.droids.model.Droid;

/**
 * This class controls bounded item.
 */
class BoundedItemControl extends AbstractControl {
    private final BoundedItem item;
    private final Droid droid;

    /**
     * Initializes the item and the droid.
     * @param item the considered item
     * @param droid the droid
     */
    public BoundedItemControl(BoundedItem item, Droid droid) {
        this.item = item;
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
            final float dx = item.getX() - droid.getX();
            final float dy = item.getY() - droid.getY();
            spatial.setLocalTranslation(dx * sin - dy * cos - 0.5f,
                                        dx * cos + dy * sin - 0.5f,
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

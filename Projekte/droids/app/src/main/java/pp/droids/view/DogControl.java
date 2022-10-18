package pp.droids.view;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.Dog;
import pp.droids.model.Flag;

import static com.jme3.math.FastMath.PI;
import static com.jme3.math.FastMath.sin;
import static com.jme3.math.Vector3f.UNIT_X;
import static com.jme3.math.Vector3f.UNIT_Y;
import static com.jme3.math.Vector3f.UNIT_Z;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;
import static pp.util.Angle.normalizeAngle;

public class DogControl extends AbstractControl {

    private final Dog dog;

    /**
     * Constructor to set up the dog.
     *
     * @param dog given dog
     */
    public DogControl(Dog dog) {
        this.dog = dog;
    }

    /**
     * Updates the position of the robot.
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            final float angle = dog.getRotation();
            spatial.getLocalRotation().fromAngleAxis(angle, UNIT_Y);
            spatial.setLocalTranslation(modelToViewX(dog),
                                        modelToViewY(dog),
                                        modelToViewZ(dog));
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

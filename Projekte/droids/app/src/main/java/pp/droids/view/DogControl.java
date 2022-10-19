package pp.droids.view;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.Dog;

import static com.jme3.math.Vector3f.UNIT_Y;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

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

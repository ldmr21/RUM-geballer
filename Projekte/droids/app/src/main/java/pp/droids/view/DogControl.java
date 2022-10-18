package pp.droids.view;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.Dog;

import static com.jme3.math.FastMath.PI;
import static com.jme3.math.Vector3f.UNIT_Y;
import static com.jme3.math.Vector3f.UNIT_X;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

public class DogControl extends AbstractControl {
    private final Dog dog;

    public DogControl(Dog dog) {this.dog = dog;}

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

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}

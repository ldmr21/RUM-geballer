package pp.droids.view.debug;

import com.jme3.scene.Node;
import pp.util.Angle;
import pp.util.map.Triangle;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * Mode that shows just the current observation
 */
class ObservationMode extends Mode {
    private final Node segmentsNode = new Node("segments"); //NON-NLS

    ObservationMode(DebugView debugView) {
        super(debugView);
    }

    /**
     * Attaches a child, when observation mode is selected.
     */
    @Override
    void select() {
        debugView.centerNode.attachChild(segmentsNode);
    }

    /**
     * Detaches a child, when observation mode is deselected.
     */
    @Override
    void deselect() {
        debugView.centerNode.detachChild(segmentsNode);
    }

    /**
     * Switches the mode to the debug view.
     */
    @Override
    void nextMode() {
        debugView.setEnabled(false);
    }

    /**
     * Updates the observation mode.
     */
    @Override
    void update() {
        if (debugView.getDebugee().getLatestObservation() == null)
            return;
        segmentsNode.detachAllChildren();
        for (Triangle t : debugView.getDebugee().getLatestObservation().getTriangles()) {
            if (Float.isInfinite(t.leftDist()) || Float.isInfinite(t.rightDist()))
                continue;
            final Angle lookUpward = Angle.fromRadians(HALF_PI - debugView.getDebugee().getRotation());
            final Angle leftAngle = t.leftAngle().plus(lookUpward);
            final Angle rightAngle = t.rightAngle().plus(lookUpward);
            final float x1 = t.leftDist() * leftAngle.x;
            final float y1 = t.leftDist() * leftAngle.y;
            final float x2 = t.rightDist() * rightAngle.x;
            final float y2 = t.rightDist() * rightAngle.y;
            segmentsNode.attachChild(debugView.getDraw().makeLine(x1, y1, x2, y2, 1f, getColor(t.cat())));
        }
    }
}

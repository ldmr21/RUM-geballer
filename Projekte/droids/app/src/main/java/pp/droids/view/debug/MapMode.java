package pp.droids.view.debug;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pp.graphics.Draw;
import pp.util.CircularEntity;
import pp.util.Position;
import pp.util.TypedSegment;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * Mode that shows the observation map.
 */
class MapMode extends Mode {
    private static final ColorRGBA CROSS_COLOR1 = ColorRGBA.LightGray;
    private static final ColorRGBA CROSS_COLOR2 = ColorRGBA.Red;
    private static final float CROSS_WIDTH = 0.2f;
    private final Node turnNode = new Node("turn"); //NON-NLS
    private final Node observerNode = new Node("observer"); //NON-NLS

    MapMode(DebugView debugView) {
        super(debugView);
        turnNode.attachChild(observerNode);
    }

    /**
     * Resets the map mode.
     */
    void reset() {
        observerNode.detachAllChildren();
    }

    /**
     * Attaches a child, when map mde is selected.
     */
    @Override
    void select() {
        debugView.centerNode.attachChild(turnNode);
    }

    /**
     * Detaches a child, when map mode is deselected.
     */
    @Override
    void deselect() {
        debugView.centerNode.detachChild(turnNode);
    }

    /**
     * Switches to observation mode.
     */
    void nextMode() {
        debugView.selectMode(debugView.observationMode);
    }

    /**
     * Updates the mode.
     */
    @Override
    void update() {
        observerNode.detachAllChildren();
        adjustView();
        for (TypedSegment s : debugView.getDebugee().getMap().getSegments())
            observerNode.attachChild(debugView.getDraw().makeLine(s, 0f, getColor(s.cat())));
        for (CircularEntity c : debugView.getDebugee().getMap().getEntities())
            observerNode.attachChild(debugView.getDraw().makeEllipse(c.getX(), c.getY(), 0f,
                                                                     2f * c.getRadius(), 2f * c.getRadius(),
                                                                     getColor(c.cat())));
        Position prev = debugView.getDebugee();
        for (Position p : debugView.getDebugee().getPath()) {
            observerNode.attachChild(debugView.getDraw().makeLine(prev, p, 3f, ColorRGBA.Pink));
            prev = p;
        }
    }

    /**
     * Adjust the view to the debugee's position and orientation
     * by updating the observer node and the turn node.
     */
    private void adjustView() {
        observerNode.setLocalTranslation(-debugView.getDebugee().getX(), -debugView.getDebugee().getY(), 0f);
        final Quaternion rot = new Quaternion();
        rot.fromAngleAxis(HALF_PI - debugView.getDebugee().getRotation(), Vector3f.UNIT_Z);
        turnNode.setLocalRotation(rot);
    }

    /**
     * Checks whether the item, when moved to the specified position, would collide with anything else.
     *
     * @return true, if a collision would happen
     */
    private boolean collisionAt(Position p) {
        return debugView.getDebugee().getMap().getSegments().stream()
                        .anyMatch(s -> s.distanceTo(p) <= debugView.getDebugee().getRadius());
    }

    /**
     * Adds a cross
     *
     * @param p the position of the cross center.
     */
    private void addCross(Position p) {
        final ColorRGBA color = collisionAt(p) ? CROSS_COLOR2 : CROSS_COLOR1;
        final Draw draw = debugView.getDraw();
        observerNode.attachChild(draw.makeLine(p.getX() - CROSS_WIDTH, p.getY() - CROSS_WIDTH,
                                               p.getX() + CROSS_WIDTH, p.getY() + CROSS_WIDTH,
                                               2f,
                                               color));
        observerNode.attachChild(draw.makeLine(p.getX() - CROSS_WIDTH, p.getY() + CROSS_WIDTH,
                                               p.getX() + CROSS_WIDTH, p.getY() - CROSS_WIDTH,
                                               2f,
                                               color));
    }
}

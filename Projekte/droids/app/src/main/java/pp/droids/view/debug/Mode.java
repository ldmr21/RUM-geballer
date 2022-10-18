package pp.droids.view.debug;

import com.jme3.math.ColorRGBA;
import pp.droids.model.Category;

/**
 * Abstract mode class using the state pattern.
 */
abstract class Mode {
    static ColorRGBA getColor(String cat) {
        return switch (cat) {
            case Category.CHARACTER -> ColorRGBA.Red;
            case Category.OBSTACLE -> ColorRGBA.Magenta;
            case Category.WALL -> ColorRGBA.White;
            case Category.OUTER_WALL -> ColorRGBA.Cyan;
            case Category.EXIT -> ColorRGBA.Orange;
            case Category.DOG -> ColorRGBA.Green;
            default -> ColorRGBA.Yellow;
        };
    }

    final DebugView debugView;

    Mode(DebugView debugView) {
        this.debugView = debugView;
    }

    /**
     * Called for each frame
     */
    abstract void update();

    /**
     * Switch to next mode
     */
    abstract void nextMode();

    /**
     * Executed when this mode gets selected
     */
    abstract void select();

    /**
     * Executed when this mode gets deselected
     */
    abstract void deselect();
}

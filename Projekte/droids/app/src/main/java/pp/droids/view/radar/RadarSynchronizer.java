package pp.droids.view.radar;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;
import pp.droids.GameState;
import pp.droids.ModelViewSynchronizer;
import pp.droids.model.BoundedItem;
import pp.droids.model.Dog;
import pp.droids.model.Droid;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Item;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.Visitor;
import pp.util.TypedSegment;

import java.text.MessageFormat;

/**
 * This class synchronizes the radar.
 */
class RadarSynchronizer extends ModelViewSynchronizer implements Visitor<Spatial> {
    public static final String DROID = "Droid"; //NON-NLS
    public static final String ENEMY = "Enemy"; //NON-NLS
    public static final String PROJECTILE = "Projectile"; //NON-NLS
    public static final String OBSTACLE = "Obstacle"; //NON-NLS
    public static final String FLAG = "Flag"; //NON-NLS
    public static final String EXIT = "Exit"; //NON-NLS

    public static final String DOG = "Dog";

    public RadarSynchronizer(GameState gameState, Node root) {
        super(gameState, root);
    }

    /**
     * Calls the accept method.
     *
     * @param item the given item
     * @return translated spatial
     */
    @Override
    protected Spatial translate(Item item) {
        return item.accept(this);
    }

    /**
     * Generates a picture to the given item.
     *
     * @param item given item for the picture
     * @param name name for the picture
     * @return generated picture
     */
    private Picture getPicture(BoundedItem item, String name) {
        final Picture p = new Picture(name);
        p.setImage(gameState.getApp().getAssetManager(),
                   MessageFormat.format("Textures/Pictures/{0}.png", name), true); //NON-NLS
        p.addControl(new BoundedItemControl(item, gameState.getModel().getDroidsMap().getDroid()));
        p.setHeight(1f);
        p.setWidth(1f);
        return p;
    }

    /**
     * Visit method for droid. Calls the getPicture method.
     *
     * @param droid the given droid
     * @return generated picture
     */
    @Override
    public Spatial visit(Droid droid) {
        return getPicture(droid, DROID);
    }

    /**
     * Visit method for an enemy. Calls the getPicture method.
     *
     * @param enemy the given enemy
     * @return generated picture
     */
    @Override
    public Spatial visit(Enemy enemy) {
        return getPicture(enemy, ENEMY);
    }

    /**
     * Visit method for a projectile. Calls the getPicture method.
     *
     * @param projectile the given projectile
     * @return generated picture
     */
    @Override
    public Spatial visit(Projectile projectile) {
        return getPicture(projectile, PROJECTILE);
    }

    /**
     * Visit method for an obstacle. Calls the getPicture method.
     *
     * @param obstacle the given obstacle
     * @return generated picture
     */
    @Override
    public Spatial visit(Obstacle obstacle) {
        return getPicture(obstacle, OBSTACLE);
    }

    /**
     * Visit method for a rocket.
     *
     * @param rocket the given rocket
     * @return generated picture
     */
    @Override
    public Spatial visit(Rocket rocket) {
        return null;
    }

    /**
     * Visit method for a maze.
     *
     * @param maze the given maze
     * @return generated node
     */
    @Override
    public Spatial visit(Maze maze) {
        final Node parent = new Node("RadarMaze");
        for (TypedSegment seg : maze.getSegments())
            parent.attachChild(gameState.getApp().getDraw().makeLine(seg, 0f, ColorRGBA.Green));
        parent.addControl(new MazeControl(gameState.getModel().getDroidsMap().getDroid()));
        return parent;
    }

    @Override
    public Spatial visit(Flag flag) {
        return getPicture(flag, FLAG);
    }

    @Override
    public Spatial visit(Exit exit) {
        return getPicture(exit, EXIT);
    }

    @Override
    public Spatial visit(Dog dog){
        return getPicture(dog, DOG);
    }
}

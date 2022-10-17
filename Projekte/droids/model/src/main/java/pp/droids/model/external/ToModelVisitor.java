package pp.droids.model.external;

import pp.droids.model.BoundedItem;
import pp.droids.model.Dog;
import pp.droids.model.Droid;
import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.FlagCaptor;
import pp.droids.model.MapLevel;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Rocket;
import pp.util.FloatPoint;
import pp.util.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A visitor used for translating all external representations into model items.
 */
class ToModelVisitor implements Visitor {
    private final DroidsModel model;
    private final DroidsMap map;
    /**
     * A list of all levels.
     */
    private final List<MapLevel> levelList;
    /**
     * Maps flag captors to their captured flags, represented by id strings
     */
    private final Map<FlagCaptor, String> flagCaptor2flagId = new HashMap<>();

    /**
     * Maps id strings to flags with these ids.
     */
    private final Map<String, Flag> id2flag = new HashMap<>();
    private final StringBuilder errors = new StringBuilder();

    /**
     * Creates a visitor for the specified external representation af a game map and the specified game model.
     *
     * @param externalMap external representation af a game map
     * @param model       game model
     */
    ToModelVisitor(ExternalMap externalMap, DroidsModel model) {
        this.model = model;
        map = new DroidsMap(model, externalMap.width, externalMap.height);
        levelList = externalMap.items.stream()
                                     .map(it -> it.level)
                                     .collect(Collectors.toSet())
                                     .stream()
                                     .map(s -> new MapLevel(map, s))
                                     .toList();
    }

    /**
     * Returns the game map as a translation result.
     */
    DroidsMap getMap() {
        return map;
    }

    /**
     * Returns all error messages occurred so far or null if no error occurred.
     */
    String getErrors() {
        return errors.isEmpty() ? null : errors.toString();
    }

    /**
     * Adds cross-references between items
     */
    void connectItems() {
        flagCaptor2flagId.forEach((fc, id) -> fc.setCapturedFlag(getFlag(id)));
    }

    private void addError(String msg) {
        if (!errors.isEmpty())
            errors.append("\n");
        errors.append(msg);
    }

    private Flag getFlag(String id) {
        final Flag flag = id2flag.get(id);
        if (flag == null)
            addError("cannot find flag for " + id); //NON-NLS
        return flag;
    }

    /**
     * Returns the level with the same name as used in the specified external item.
     */
    private MapLevel findLevel(ExternalItem item) {
        return levelList.stream()
                        .filter(l -> l.getName().equals(item.level))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("cannot find level " + item.level));
    }

    @Override
    public void visit(ExternalDroid item) {
        final Droid droid = new Droid(model, item.radius, item.lives, item.reloadTime);
        droid.setPos(item.getPos());
        droid.setRotation(item.angle);
        if (item.destroyed)
            droid.destroy();
        map.setDroid(droid, findLevel(item));
        if (item.flag != null)
            flagCaptor2flagId.put(droid, item.flag);
    }

    @Override
    public void visit(ExternalEnemy item) {
        final Enemy enemy = new Enemy(model, item.radius, item.lives, item.reloadTime);
        addItem(enemy, item);
        if (item.flag != null)
            flagCaptor2flagId.put(enemy, item.flag);
    }

    @Override
    public void visit(ExternalObstacle item) {
        addItem(new Obstacle(model), item);
    }

    @Override
    public void visit(ExternalRocket item) {
        final Rocket rocket = new Rocket(model);
        rocket.setPos(item.getPos());
        rocket.setTarget(item.getTarget());
        if (item.destroyed)
            rocket.destroy();
        map.register(rocket, findLevel(item));
    }

    @Override
    public void visit(ExternalMaze item) {
        final List<Position> points = new ArrayList<>(item.coords.length / 2);
        for (int i = 0; i < item.coords.length; i += 2)
            points.add(new FloatPoint(item.coords[i], item.coords[i + 1]));
        final Maze maze = new Maze(model, points);
        if (item.destroyed)
            maze.destroy();
        map.register(maze, findLevel(item));
    }

    @Override
    public void visit(ExternalFlag item) {
        final Flag flag = new Flag(model);
        id2flag.put(item.id, flag);
        addItem(flag, item);
    }

    @Override
    public void visit(ExternalDog item) {
        final Dog dog = new Dog(model);
        addItem(dog, item);
    }

    @Override
    public void visit(ExternalExit item) {
        addItem(new Exit(model, item.radius), item);
    }

    @Override
    public void visit(ExternalDog item){
        final Dog dog = new Dog(model);
        addItem(dog, item);
    }

    /**
     * Adds the specified (model) item to the map based on the information stored in the
     * specified external item.
     *
     * @param modelItem    the model item
     * @param externalItem the external item
     */
    private void addItem(BoundedItem modelItem, ExternalBoundedItem externalItem) {
        modelItem.setPos(externalItem.getPos());
        modelItem.setRotation(externalItem.angle);
        if (externalItem.destroyed)
            modelItem.destroy();
        map.register(modelItem, findLevel(externalItem));
    }
}

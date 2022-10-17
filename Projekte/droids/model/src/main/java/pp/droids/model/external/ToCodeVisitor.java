package pp.droids.model.external;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * A visitor used for translating all external representations into Java code that
 * creates the corresponding model items.
 */
class ToCodeVisitor implements Visitor {
    private final StringBuilder sb = new StringBuilder();
    /**
     * Maps flag captors to their captured flags, represented by id strings
     */
    private final Map<String, String> flagCaptor2flagId = new HashMap<>();

    public ToCodeVisitor(ExternalMap externalMap) {
        final Set<String> levels = externalMap.items.stream()
                                                    .map(it -> it.level)
                                                    .collect(Collectors.toSet());
        sb.append(format("final DroidsMap map = new DroidsMap(model, %d, %d);%n", //NON-NLS
                         externalMap.width, externalMap.height));
        sb.append("final Map<String, MapLevel> id2level = new HashMap<>();\n"); //NON-NLS
        for (String level : levels)
            sb.append(format("id2level.put(\"%s\", new MapLevel(map, \"%s\")); //NON-NLS%n",  //NON-NLS
                             level, level));
    }

    /**
     * Returns the generated code.
     */
    public String getCode() {
        return sb.toString();
    }

    /**
     * Adds cross-references between items
     */
    void connectItems() {
        flagCaptor2flagId.forEach((captor, flag) -> sb.append(format(Locale.US,
                                                                     "%s.setCapturedFlag(%s);%n", //NON-NLS
                                                                     captor, flag)));
        sb.append("map.addRegisteredItems();\nmodel.setDroidsMap(map);\n"); //NON-NLS
    }

    @Override
    public void visit(ExternalDroid item) {
        sb.append(format(Locale.US, "final Droid %s = new Droid(model, %ff, %d, %ff);%n", //NON-NLS
                         item.id, item.radius, item.lives, item.reloadTime));
        setPos(item);
        setRotation(item);
        setDestroyed(item);
        sb.append(format(Locale.US, "map.setDroid(%s, id2level.get(\"%s\")); //NON-NLS%n", //NON-NLS
                         item.id, item.level));
        rememberCapturedFlag(item);
    }

    @Override
    public void visit(ExternalEnemy item) {
        sb.append(format(Locale.US, "final Enemy %s = new Enemy(model, %ff, %d, %ff);%n",  //NON-NLS
                         item.id, item.radius, item.lives, item.reloadTime));
        addItem(item);
        rememberCapturedFlag(item);
    }

    @Override
    public void visit(ExternalObstacle item) {
        sb.append(format(Locale.US, "final Obstacle %s = new Obstacle(model);%n", item.id));  //NON-NLS
        addItem(item);
    }

    @Override
    public void visit(ExternalRocket item) {
        sb.append(format(Locale.US, "final Rocket %s = new Rocket(model);%n", item.id));  //NON-NLS
        setPos(item);
        sb.append(format(Locale.US, "%s.setTarget(new FloatPoint(%ff, %ff));%n",  //NON-NLS
                         item.id, item.targetX, item.targetY));
        setDestroyed(item);
        register(item);
    }

    @Override
    public void visit(ExternalMaze item) {
        sb.append(format(Locale.US, "final Maze %s = new Maze(model, List.of(", item.id)); //NON-NLS
        for (int i = 0; i < item.coords.length; i += 2) {
            if (i > 0)
                sb.append(", ");
            sb.append(format(Locale.US, "new FloatPoint(%ff, %ff)", //NON-NLS
                             item.coords[i], item.coords[i + 1]));
        }
        sb.append("));\n");
        setDestroyed(item);
        register(item);
    }

    @Override
    public void visit(ExternalFlag item) {
        sb.append(format(Locale.US, "final Flag %s = new Flag(model);%n", item.id)); //NON-NLS
        addItem(item);
    }

    @Override
    public void visit(ExternalExit item) {
        sb.append(format(Locale.US, "final Exit %s = new Exit(model);%n", item.id)); //NON-NLS
        addItem(item);
    }

    @Override
    public void visit(ExternalDog item){
        sb.append(format(Locale.US,"final Flag %s = new Flag(model);%n", item.id));
        addItem(item);
    }

    /**
     * Adds the specified (model) item to the map based on the information stored in the
     * specified external item.
     *
     * @param item the  external item
     */
    private void addItem(ExternalBoundedItem item) {
        setPos(item);
        setRotation(item);
        setDestroyed(item);
        register(item);
    }

    private void setPos(ExternalBoundedItem item) {
        sb.append(format(Locale.US, "%s.setPos(%ff, %ff);%n", item.id, item.x, item.y));  //NON-NLS
    }

    private void setRotation(ExternalBoundedItem item) {
        sb.append(format(Locale.US, "%s.setRotation(%ff);%n", item.id, item.angle)); //NON-NLS
    }

    private void setDestroyed(ExternalItem item) {
        if (item.destroyed)
            sb.append(format(Locale.US, "%s.destroy();%n", item.id)); //NON-NLS
    }

    private void register(ExternalItem item) {
        sb.append(format(Locale.US, "map.register(%s, id2level.get(\"%s\")); //NON-NLS%n", //NON-NLS
                         item.id, item.level));
    }

    private void rememberCapturedFlag(ExternalShooter item) {
        if (item.flag != null)
            flagCaptor2flagId.put(item.id, item.flag);
    }
}

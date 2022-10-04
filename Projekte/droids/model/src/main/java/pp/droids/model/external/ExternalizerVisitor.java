package pp.droids.model.external;

import pp.droids.model.Droid;
import pp.droids.model.DroidsMap;
import pp.droids.model.Enemy;
import pp.droids.model.Exit;
import pp.droids.model.Flag;
import pp.droids.model.Item;
import pp.droids.model.Maze;
import pp.droids.model.Obstacle;
import pp.droids.model.Projectile;
import pp.droids.model.Rocket;
import pp.droids.model.VoidVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ExternalizerVisitor implements VoidVisitor {
    private final DroidsMap map;
    private final List<ExternalItem> items;
    private final Map<Item, String> idMap = new HashMap<>();

    public ExternalizerVisitor(DroidsMap map, List<ExternalItem> items) {
        this.map = map;
        this.items = items;
        int ctr = 0;
        idMap.put(map.getDroid(), "id" + ++ctr);
        for (Item it : map.getItems())
            if (it != map.getDroid())
                idMap.put(it, "id" + ++ctr);
    }

    @Override
    public void visit(Droid droid) {
        items.add(new ExternalDroid(map.getDroid(), idMap));
    }

    @Override
    public void visit(Obstacle obstacle) {
        items.add(new ExternalObstacle(obstacle, idMap));
    }

    @Override
    public void visit(Enemy enemy) {
        items.add(new ExternalEnemy(enemy, idMap));
    }

    @Override
    public void visit(Rocket rocket) {
        items.add(new ExternalRocket(rocket, idMap));
    }

    @Override
    public void visit(Projectile proj) {
        /* do nothing */
    }

    @Override
    public void visit(Maze maze) {
        items.add(new ExternalMaze(maze, idMap));
    }


    @Override
    public void visit(Flag flag) {
        items.add(new ExternalFlag(flag, idMap));
    }

    @Override
    public void visit(Exit exit) {
        items.add(new ExternalExit(exit, idMap));
    }
}
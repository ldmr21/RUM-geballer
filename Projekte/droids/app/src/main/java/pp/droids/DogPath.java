package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pp.droids.model.Category;
import pp.droids.model.Dog;
import pp.droids.model.Droid;
import pp.droids.model.DroidsModel;
import pp.droids.view.MainSynchronizer;
import pp.util.CircularEntity;
import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.Segment;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static pp.droids.view.CoordinateTransformation.modelToView;
import static pp.droids.view.CoordinateTransformation.viewToModel;
import static pp.util.FloatMath.PI;

public class DogPath extends AbstractAppState implements Future {

    private static final Logger LOGGER = System.getLogger(DogPath.class.getName());
    private DroidsApp app;
    private Future<List<Segment>> futurePath;
    private Dog dog;
    private Boolean doneBol = false;
    private Boolean cancBol = true;

    private void setDoneBol(boolean Bool){
        doneBol = Bool;
    }

    private void setCancBol(boolean Bool){
        cancBol = Bool;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (DroidsApp) app;
        this.dog = app.getStateManager().getState(GameState.class).getModel().getDroidsMap().getDog();
    }

    /**
     * Navigates the dog to the droid.
     */
    private void navigate() {
        for (CircularEntity c : dog.getMap().getEntities()){
            if(Objects.equals(c.cat(), Category.CHARACTER)){
                if(!isDone()) {
                    navigateTo(new FloatPoint(c.getX(), c.getY()));
                    setCancBol(false);
                }
                if(new FloatPoint(dog.getX(), dog.getY()).equals(new FloatPoint(c.getX(), c.getY()))){
                    setDoneBol(true);
                    setCancBol(true);
                    dog.clearObservationMap();


                }
            }
        }
        }
        /* final Vector3f origin = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0.0f);
        final Vector3f direction = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        final Ray ray = new Ray(origin, direction);
        final CollisionResults results = new CollisionResults();
        getGameState().getItemNode().collideWith(ray, results);
        for (int i = 0; i < results.size(); i++) {
            final CollisionResult collision = results.getCollision(i);
            // look for the first hit of the floor
            if (collision.getGeometry().getName().equals(GameState.FLOOR_NAME)) {
                navigateTo(viewToModel(collision.getContactPoint()));
                return;
            }
        }

         */

    private void search(){
        if(cancBol){
            dog.setRotation(dog.getRotation() + PI * 0.01f);
            setDoneBol(false);
        }
    }

    /**
     * Uses the DroidsNavigator for computing an optimal, collision-free path to the specified position in
     * a separate worker thread. The path is retrieved by the {@linkplain #update(float)} method as soon
     * as it will have been computed and then passed to the droid.
     *
     * @param target position where to go
     */
    private void navigateTo(Position target) {
        if (futurePath != null)
            LOGGER.log(Level.WARNING, "There is still a path search running."); //NON-NLS
        else {
            LOGGER.log(Level.INFO, "Navigating to ({0}|{1})", target.getX(), target.getY());  //NON-NLS
            final var navigator = dog.getNavigator();
            futurePath = dog.getModel().getExecutor().submit(() -> navigator.findPathTo(target));
        }
    }

    /**
     * Gets the game state from the app.
     *
     * @return game state
     */
    private GameState getGameState() {
        return app.getStateManager().getState(GameState.class);
    }

    /**
     * Gets the current Droid.
     *
     * @return droid
     */
    private Droid getDroid() {
        return getGameState().getModel().getDroidsMap().getDroid();
    }

    /**
     * Called to update the AppState. This method will be called every render pass if the AppState is both attached and enabled.
     *
     * @param tpf Time since the last call to update(), in seconds.
     */
    @Override
    public void update(float tpf) {
        super.update(tpf);
        navigate();
        search();
        // Check whether a path has been computed after navigateTo(Position) had been called
         if (futurePath != null && futurePath.isDone()) {
            try {
                final List<Segment> newPath = futurePath.get();
                LOGGER.log(Level.TRACE, "found path {0}", newPath); //NON-NLS
                if (newPath.isEmpty())
                    LOGGER.log(Level.DEBUG, "no path found"); //NON-NLS
                // pass the computed path to the droid
                dog.setPath(newPath);
            }
            catch (ExecutionException e) {
                LOGGER.log(Level.WARNING, "When calling futurePath.get()", e); //NON-NLS
            }
            catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted!", e); //NON-NLS
                Thread.currentThread().interrupt();
            }
            futurePath = null;
        }


    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return cancBol;
    }

    @Override
    public boolean isDone() {
        return doneBol;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}

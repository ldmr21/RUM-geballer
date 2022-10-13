package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultCheckboxModel;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.style.BaseStyles;
import pp.droids.model.DroidsModel;
import pp.droids.view.debug.DebugView;
import pp.droids.view.radar.RadarView;
import pp.util.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import static com.simsilica.lemur.component.BorderLayout.Position.East;
import static com.simsilica.lemur.component.BorderLayout.Position.West;
import static pp.droids.DroidsAppConfig.BUNDLE;

/**
 * Extends the class {@link com.jme3.app.state.AbstractAppState}.
 * <p>
 * This class handles the particular  menu state of the app.
 */
public class MenuState extends AbstractAppState {
    /**
     * Preferences node.
     */
    private static final Preferences PREFS = Prefs.getPreferences(MenuState.class);
    /**
     * Key for storing paths in preferences.
     */
    private static final String PATH_PREF = "path"; //NON-NLS

    private DroidsApp app;
    private Container mainDialogContainer;
    private Container loadDialogContainer;
    private Container saveDialogContainer;
    private Container currentDialog;
    private SoundModel soundModel;
    private RadarViewModel radarViewModel;
    private DebugViewModel debugViewModel;
    private final Node menuGuiNode = new Node("gui.menu");

    /**
     * Sets up the menu state.
     * <p>
     * It overrides {@link com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)}
     *
     * @param stateManager The state manager
     * @param application  The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        this.app = (DroidsApp) application;
        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass"); //NON-NLS
        soundModel = new SoundModel();
        radarViewModel = new RadarViewModel();
        debugViewModel = new DebugViewModel();

        if (isEnabled()) enableState();
    }

    /**
     * Enables the menu state by updating the sound model, the radar view model and the debug view model and shows the main dialog container.
     */
    private void enableState() {
        app.getDroidsGuiNode().attachChild(menuGuiNode);
        soundModel.update();
        radarViewModel.update();
        debugViewModel.update();
        showDialog(mainDialog());
    }

    /**
     * Disables the menu state.
     */
    private void disableState() {
        app.getDroidsGuiNode().detachChild(menuGuiNode);
        showDialog(null);
    }

    /**
     * Shows the given dialog box.
     *
     * @param newDialog the given dialog box
     */
    private void showDialog(Container newDialog) {
        if (currentDialog != newDialog) {
            if (currentDialog != null)
                menuGuiNode.detachChild(currentDialog);
            if (newDialog != null)
                menuGuiNode.attachChild(newDialog);
            currentDialog = newDialog;
        }
    }

    /**
     * Enables or disables the menu state.
     * <p>
     * It overrides {@link com.jme3.app.state.AbstractAppState#setEnabled(boolean)}
     *
     * @param enabled activate the menu state or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        if (app != null) {
            if (enabled)
                enableState();
            else
                disableState();
        }
    }

    /**
     * Returns the main dialog container. If the dialog container is null yet, it will be created with its labels and buttons.
     *
     * @return main dialog container
     */
    private Container mainDialog() {
        if (mainDialogContainer == null) {
            mainDialogContainer = new Container();

            final Label label = mainDialogContainer.addChild(new Label(BUNDLE.getString("game.name")));
            label.setColor(ColorRGBA.Red);
            label.setTextHAlignment(HAlignment.Center);
            final Button randomBtn = mainDialogContainer.addChild(new Button(BUNDLE.getString("menu.map.create-random")));
            final Button loadBtn = mainDialogContainer.addChild(new Button(BUNDLE.getString("menu.map.load")));
            final Button saveBtn = mainDialogContainer.addChild(new Button(BUNDLE.getString("menu.map.save")));
            final Button continueBtn = mainDialogContainer.addChild(new Button(BUNDLE.getString("menu.return-to-game")));
            mainDialogContainer.addChild(new Checkbox(BUNDLE.getString("menu.sound-enabled"), soundModel));
            mainDialogContainer.addChild(new Checkbox(BUNDLE.getString("menu.radar-view-enabled"), radarViewModel));
            mainDialogContainer.addChild(new Checkbox(BUNDLE.getString("menu.debug-view-enabled"), debugViewModel));
            final Button stopBtn = mainDialogContainer.addChild(new Button(BUNDLE.getString("menu.quit")));

            randomBtn.addClickCommands(source -> getModel().loadRandomMap());
            loadBtn.addClickCommands(source -> showDialog(loadDialog()));
            saveBtn.addClickCommands(source -> showDialog(saveDialog()));
            continueBtn.addClickCommands(source -> app.switchStates());
            stopBtn.addClickCommands(source -> app.stop());
            centering(mainDialogContainer, 0f);
        }
        return mainDialogContainer;
    }

    /**
     * Positions the specified container in the center of the screen. The z component
     * of the dialog is set as specified.
     *
     * @param dialog the GUI container of the dialog
     * @param z      the z component of the dialog
     */
    private void centering(Container dialog, float z) {
        final Vector3f preferredSize = dialog.getPreferredSize();
        final AppSettings settings = app.getContext().getSettings();
        dialog.setLocalTranslation(0.5f * (settings.getWidth() - preferredSize.getX()),
                                   0.5f * (settings.getHeight() + preferredSize.getY()),
                                   z);
    }

    /**
     * Returns a dialog container. If the dialog container is null yet, it will be created.
     *
     * @return load dialog container
     */
    private Container loadDialog() {
        if (loadDialogContainer == null)
            loadDialogContainer = fileDialog("menu.map.load", this::load);
        return loadDialogContainer;
    }

    /**
     * Returns a dialog container while saving the map. If the container is null, it will be created.
     *
     * @return save dialog container
     */
    private Container saveDialog() {
        if (saveDialogContainer == null)
            saveDialogContainer = fileDialog("menu.map.save", this::save);
        return saveDialogContainer;
    }

    /**
     * Creates a file dialog container with buttons and text fields, which is used for other containers.
     *
     * @param key        name of the container
     * @param fileAction action in which the container is used
     * @return file dialog container
     */
    private Container fileDialog(String key, Consumer<String> fileAction) {
        final Container container = new Container();
        container.addChild(new Label(BUNDLE.getString(key)));
        final TextField textField = container.addChild(new TextField(getPath()));
        textField.setSingleLine(true);
        textField.setPreferredWidth(300f);
        final Container buttons = container.addChild(new Container(new BorderLayout()));
        final Button ok = buttons.addChild(new Button(BUNDLE.getString("button.ok")), West);
        final Button cancel = buttons.addChild(new Button(BUNDLE.getString("button.cancel")), East);
        ok.addClickCommands(source -> fileAction.accept(textField.getText()));
        cancel.addClickCommands(source -> showDialog(mainDialog()));
        centering(container, 10f);
        return container;
    }

    /**
     * Creates an error dialog container with buttons and text fields.
     *
     * @param text     error message
     * @param previous the previous shown container
     * @return error dialog container
     */
    private Container errorDialog(String text, Container previous) {
        Container errorContainer = new Container();
        errorContainer.addChild(new Label(BUNDLE.getString("text.error")));
        errorContainer.addChild(new Label(text));
        final Container buttons = errorContainer.addChild(new Container(new BorderLayout()));
        final Button ok = buttons.addChild(new Button(BUNDLE.getString("button.ok")), West);
        ok.addClickCommands(source -> showDialog(previous));
        centering(errorContainer, 10f);
        return errorContainer;
    }

    /**
     * Trys to load a map from a path, which shows the main dialog container, and catches an IOException.
     * In this case an error dialog window is shown.
     *
     * @param path path of the file, which should be loaded
     */
    private void load(String path) {
        try {
            getModel().loadMap(new File(path));
            putPath(path);
            showDialog(mainDialog());
        }
        catch (IOException e) {
            showDialog(errorDialog(e.getLocalizedMessage(), loadDialogContainer));
        }
    }

    /**
     * Trys to save a map at a path, which shows the main dialog container, and catches an IOException.
     * In this case an error dialog window is shown.
     *
     * @param path path of the file, which should be saved
     */
    private void save(String path) {
        try {
            getModel().saveMap(new File(path));
            putPath(path);
            showDialog(mainDialog());
        }
        catch (IOException e) {
            showDialog(errorDialog(e.getLocalizedMessage(), saveDialogContainer));
        }
    }

    /**
     * Retrieves the path stored in the preferences, or the empty string
     * if no path is stored in the preferences.
     */
    private static String getPath() {
        return PREFS.get(PATH_PREF, "");
    }

    /**
     * Stores the specified path in the preferences.
     *
     * @param path the path string
     */
    private static void putPath(String path) {
        PREFS.put(PATH_PREF, path);
    }

    /**
     * Method to get the model.
     *
     * @return model
     */
    private DroidsModel getModel() {
        return app.getStateManager().getState(GameState.class).getModel();
    }

    /**
     * Method to get the sound class.
     *
     * @return sound class.
     */
    private GameSound getSound() {
        return app.getStateManager().getState(GameSound.class);
    }

    /**
     * Method to get the radar view class.
     *
     * @return radar view class
     */
    private RadarView getRadarView() {
        return app.getStateManager().getState(RadarView.class);
    }

    /**
     * Method to get the debug view class.
     *
     * @return debug view class
     */
    private DebugView getDebugView() {
        return app.getStateManager().getState(DebugView.class);
    }

    /**
     * The class simplifies saving and changing the sound.
     * It extends {@link com.simsilica.lemur.DefaultCheckboxModel}.
     */
    private class SoundModel extends DefaultCheckboxModel {
        public SoundModel() {
            super(getSound().isEnabled());
        }

        public void update() {
            setChecked(getSound().isEnabled());
        }

        @Override
        public void setChecked(boolean state) {
            super.setChecked(state);
            getSound().setEnabled(state);
        }
    }

    /**
     * The class simplifies saving and changing the radar view.
     * It extends {@link com.simsilica.lemur.DefaultCheckboxModel}.
     */
    private class RadarViewModel extends DefaultCheckboxModel {
        public RadarViewModel() {
            super(getRadarView().isEnabled());
        }

        public void update() {
            setChecked(getRadarView().isEnabled());
        }

        @Override
        public void setChecked(boolean state) {
            super.setChecked(state);
            getRadarView().setEnabled(state);
        }
    }

    /**
     * The class simplifies saving and changing the debug view.
     * It extends {@link com.simsilica.lemur.DefaultCheckboxModel}.
     */
    private class DebugViewModel extends DefaultCheckboxModel {
        public DebugViewModel() {
            super(getDebugView().isEnabled());
        }

        public void update() {
            setChecked(getDebugView().isEnabled());
        }

        @Override
        public void setChecked(boolean state) {
            super.setChecked(state);
            getDebugView().setEnabled(state);
        }
    }
}

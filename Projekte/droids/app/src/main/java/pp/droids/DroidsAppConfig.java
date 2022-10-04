package pp.droids;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;
import pp.droids.model.DroidsConfig;

import java.util.ResourceBundle;

/**
 * Represents game configurations. Configurations can be loaded from
 * a json file.
 */
@SuppressWarnings("CanBeFinal")
public class DroidsAppConfig extends DroidsConfig {
    /**
     * The resource bundle of the Droids game.
     */
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("droids"); //NON-NLS

    /**
     * The title of the window showing the game
     */
    @JsonProperty("window title") //NON-NLS
    private String windowTitle = "Droids"; //NON-NLS

    /**
     * Indicates whether the settings window shall be shown
     * for configuring the game.
     */
    @JsonProperty("show settings") //NON-NLS
    private boolean showSettings = false;

    /**
     * The width of the game view resolution.
     */
    @JsonProperty("resolution width") //NON-NLS
    private int resolutionWidth = 1024;

    /**
     * The height of the game view resolution.
     */
    @JsonProperty("resolution height") //NON-NLS
    private int resolutionHeight = 768;

    /**
     * Indicates whether the game shall start in full screen mode.
     */
    @JsonProperty("full screen") //NON-NLS
    private boolean fullScreen = false;

    /**
     * Indicates whether gamma correction shall be enabled. If enabled, the main framebuffer will
     * be configured for sRGB colors, and sRGB images will be linearized.
     * <p>
     * Gamma correction requires a GPU that supports GL_ARB_framebuffer_sRGB;
     * otherwise this setting will be ignored.
     */
    @JsonProperty("use gamma correction") //NON-NLS
    private boolean useGammaCorrection = true;

    /**
     * Indicates whether to use full resolution framebuffers on Retina displays.
     * This is ignored on other platforms.
     */
    @JsonProperty("use retina framebuffer") //NON-NLS
    private boolean useRetinaFrameBuffer = false;

    /**
     * Indicates whether the JME statistics window shall be shown in the lower left corner.
     */
    @JsonProperty("show statistics") //NON-NLS
    private boolean showStatistics = false;

    /**
     * The time a hint is shown.
     */
    @JsonProperty("show.hint.time") //NON-NLS
    private int hintTime = 4;

    /**
     * The color of the center text during game play.
     */
    @JsonProperty("overlay center color") //NON-NLS
    private ColorResource centerColor = new ColorResource(ColorRGBA.Red);

    /**
     * The color of the top text during game play.
     */
    @JsonProperty("overlay top color") //NON-NLS
    private ColorResource topColor = new ColorResource(ColorRGBA.White);

    /**
     * The color of the bottom text during game play.
     */
    @JsonProperty("overlay bottom color") //NON-NLS
    private ColorResource bottomColor = new ColorResource(ColorRGBA.Yellow);

    /**
     * Returns whether the settings window shall be shown
     * for configuring the game.
     */
    @JsonIgnore
    public boolean getShowSettings() {
        return showSettings;
    }

    /**
     * Returns an {@linkplain com.jme3.system.AppSettings} object configured
     * by this configuration.
     */
    public AppSettings getSettings() {
        final AppSettings settings = new AppSettings(true);
        settings.setTitle(windowTitle);
        settings.setResolution(resolutionWidth, resolutionHeight);
        settings.setFullscreen(fullScreen);
        settings.setUseRetinaFrameBuffer(useRetinaFrameBuffer);
        settings.setGammaCorrection(useGammaCorrection);
        return settings;
    }

    /**
     * Returns whether the JME statistics window shall be shown in the lower left corner.
     */
    @JsonIgnore
    public boolean getShowStatistics() {
        return showStatistics;
    }

    /**
     * Returns the duration how long a hint shall show.
     */
    @JsonIgnore
    public int getHintTime() {
        return hintTime;
    }

    /**
     * Returns the color of the center text during game play.
     */
    @JsonIgnore
    public ColorRGBA getCenterColor() {
        return centerColor.getColor();
    }

    /**
     * Returns the color of the top text during game play.
     */
    @JsonIgnore
    public ColorRGBA getTopColor() {
        return topColor.getColor();
    }

    /**
     * Returns the color of the bottom text during game play.
     */
    @JsonIgnore
    public ColorRGBA getBottomColor() {
        return bottomColor.getColor();
    }
}

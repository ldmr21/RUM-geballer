package pp.droids;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jme3.math.ColorRGBA;

/**
 * Resource for specifying colors in JSON files.
 */
class ColorResource {
    @JsonProperty
    private float red;
    @JsonProperty
    private float green;
    @JsonProperty
    private float blue;
    @JsonProperty
    private float alpha;

    @JsonIgnore
    private ColorRGBA color;

    /**
     * Creates empty color object.
     */
    private ColorResource() {
        // Jackson needs a default constructor
    }

    /**
     * Creates a color object with the given values.
     *
     * @param red   determines red value
     * @param green determines green value
     * @param blue  determines blue value
     * @param alpha determines the opacity
     */
    public ColorResource(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Creates a color object using an object of {@link com.jme3.math.ColorRGBA} class.
     *
     * @param color an existing color object
     */
    public ColorResource(ColorRGBA color) {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Returns the color stored.
     */
    @JsonIgnore
    public ColorRGBA getColor() {
        if (color == null)
            color = new ColorRGBA(red, green, blue, alpha);
        return color;
    }
}

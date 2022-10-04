package pp.graphics;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import pp.util.Position;
import pp.util.SegmentLike;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;

import static com.jme3.math.FastMath.TWO_PI;
import static com.jme3.math.FastMath.cos;
import static com.jme3.math.FastMath.sin;
import static com.jme3.math.FastMath.sqr;
import static com.jme3.math.FastMath.sqrt;

/**
 * Class for creating graphical primitives.
 */
public class Draw {
    private static final Logger LOGGER = System.getLogger(Draw.class.getName());
    private static final int NUM = 10;
    private static final String UNSHADED = "Common/MatDefs/Misc/Unshaded.j3md"; //NON-NLS
    private static final String COLOR = "Color"; //NON-NLS
    private final AssetManager am;
    private final Map<ColorRGBA, Geometry> lineMap = new HashMap<>();
    private final Map<ColorRGBA, Geometry> rectangleMap = new HashMap<>();
    private final Map<ColorRGBA, Geometry> circleMap = new HashMap<>();
    private Mesh lineMesh;
    private Mesh circleMesh;

    /**
     * Creates an in stance of the Draw class with the specified
     * asset manager.
     *
     * @param assetManager the specified asset manager
     */
    public Draw(AssetManager assetManager) {
        am = assetManager;
    }

    private Geometry makeLine(ColorRGBA color) {
        LOGGER.log(Level.DEBUG, "create line with color {0}", color); //NON-NLS
        if (lineMesh == null) {
            lineMesh = new Mesh();
            lineMesh.setMode(Mesh.Mode.Lines);
            lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{0, 0, 0, 0, 1, 0});
            lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{0, 1});
        }
        final Geometry lineGeom = new Geometry("lineMesh", lineMesh.clone());
        Material matWireframe = new Material(am, UNSHADED); //NON-NLS
        matWireframe.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        matWireframe.setColor(COLOR, color); //NON-NLS
        lineGeom.setMaterial(matWireframe);

        return lineGeom;
    }

    /**
     * Creates a line for the specified segment.
     *
     * @param segment the segment with its start and end point
     * @param z       depth information
     * @param color   line color
     */
    public Geometry makeLine(SegmentLike segment, float z, ColorRGBA color) {
        return makeLine(segment.from(), segment.to(), z, color);
    }

    /**
     * Creates a straight line between the specified points with the specified color.
     *
     * @param p1    start point
     * @param p2    end point
     * @param z     depth information
     * @param color line color
     */
    public Geometry makeLine(Position p1, Position p2, float z, ColorRGBA color) {
        return makeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), z, color);
    }

    /**
     * Creates a straight line between the specified points with the specified color.
     *
     * @param x1    x-coordinate of the start point
     * @param y1    y-coordinate of the start point
     * @param x2    x-coordinate of the end point
     * @param y2    y-coordinate of the end point
     * @param z     depth information
     * @param color line color
     */
    public Geometry makeLine(float x1, float y1, float x2, float y2, float z, ColorRGBA color) {
        final Geometry line = lineMap.computeIfAbsent(color, this::makeLine).clone();
        line.lookAt(Vector3f.UNIT_Z, new Vector3f(x2 - x1, y2 - y1, 0));
        line.setLocalScale(sqrt(sqr(x2 - x1) + sqr(y2 - y1)));
        line.setLocalTranslation(x1, y1, z);
        return line;
    }

    private Geometry makeRectangle(ColorRGBA color) {
        final Mesh quad = new Quad(1f, 1f);
        final Geometry rectangle = new Geometry("quad", quad); //NON-NLS
        Material mat = new Material(am, UNSHADED); //NON-NLS
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.setColor(COLOR, color); //NON-NLS
        rectangle.setMaterial(mat);
        return rectangle;
    }

    /**
     * Creates an axis-parallel rectangle with the specified color.
     *
     * @param x     x-coordinate of the bottom-left corner
     * @param y     y-coordinate of the bottom-left corner
     * @param w     width of the rectangle
     * @param h     height of the rectangle
     * @param z     depth information
     * @param color line color
     */
    public Geometry makeRectangle(float x, float y, float z, float w, float h, ColorRGBA color) {
        final Geometry rectangle = rectangleMap.computeIfAbsent(color, this::makeRectangle).clone();
        rectangle.setLocalScale(w, h, 1f);
        rectangle.setLocalTranslation(x, y, z);
        return rectangle;
    }

    private Geometry makeCircle(ColorRGBA color) {
        if (circleMesh == null) {
            circleMesh = new Mesh();
            circleMesh.setMode(Mesh.Mode.LineLoop);
            final float[] pointBuffer = new float[3 * NUM];
            final short[] indexBuffer = new short[NUM];
            int j = 0;
            for (short i = 0; i < NUM; i++) {
                final float a = TWO_PI / NUM * i;
                pointBuffer[j++] = 0.5f * cos(a);
                pointBuffer[j++] = 0.5f * sin(a);
                pointBuffer[j++] = 0f;
                indexBuffer[i] = i;
            }
            circleMesh.setBuffer(VertexBuffer.Type.Position, 3, pointBuffer);
            circleMesh.setBuffer(VertexBuffer.Type.Index, 2, indexBuffer);
        }

        final Geometry circle = new Geometry("circleMesh", circleMesh.clone());
        Material matWireframe = new Material(am, UNSHADED); //NON-NLS
        matWireframe.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        matWireframe.setColor(COLOR, color); //NON-NLS
        circle.setMaterial(matWireframe);

        return circle;
    }

    /**
     * Creates an ellipse with the specified color..
     *
     * @param x     x-coordinate of the center point
     * @param y     y-coordinate of the center point
     * @param w     width of the ellipse
     * @param h     height of the ellipse
     * @param z     depth information
     * @param color line color
     */
    public Geometry makeEllipse(float x, float y, float z, float w, float h, ColorRGBA color) {
        final Geometry ellipse = circleMap.computeIfAbsent(color, this::makeCircle).clone();
        ellipse.setLocalScale(w, h, 1f);
        ellipse.setLocalTranslation(x, y, z);
        return ellipse;
    }
}

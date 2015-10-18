package gasengine.renderer;
import gasengine.scene.Component;

/**
 * Created by Matthew on 10/16/2015.
 */

public class Renderable extends Component {

    public class RenderableData{
        public int mVBo;
        public int vertexCount;
        public int mShaderProgram;
        public int mVPosAttribute;
        public int mVTransformUniform;
    }

    private float[] vertices = {};
    private PreRender renderer;
    private RenderableData data;
    private boolean cached = false;

    public Renderable()
    {
        renderer = new PreRender();
        data = new RenderableData();
    }
    public Renderable(int mVBo)
    {
        renderer = new PreRender();
        data = new RenderableData();
        cached = true;
        data.mVBo = mVBo;
    }

    @MessageHandler(value="OnComponentAdded")
    public void OnComponentAdded()
    {
        renderer.init_pipeline(this);
    }

    public void setVertices(float[] verts)
    {
        vertices = verts;
    }
    public float[] getVertices()
    {
        return vertices;
    }

    public boolean isCached()
    {
        return cached;
    }
    public RenderableData getData()
    {
        return data;
    }
}

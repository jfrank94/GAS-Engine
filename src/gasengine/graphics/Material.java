package gasengine.graphics;


public class Material
{
    Shader mShader;

    public Material(Shader shader) // FIXME temporary
    {
        mShader = shader;
    }

    public void bind()
    {
        mShader.bind();
    }
}

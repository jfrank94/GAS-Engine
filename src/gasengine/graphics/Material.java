package gasengine.graphics;


public class Material
{
    private Shader mShader;

    public Material(Shader shader) // FIXME temporary
    {
        mShader = shader;
    }

    public void bind()
    {
        mShader.bind();
    }

    public Shader getShader()
    {
        return mShader;
    }
}

package gasengine.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Created by Matthew on 10/16/2015.
 */
public class PreRender {

    private int mVBo = 0;
    private int mVertexCount = 0;

    private int mShaderProgram;
    private int mVPosAttribute;
    private int mVTransformUniform;

    private static final String VERTEX_SHADER_SOURCE =
            "#version 120\n" + // shading language version

                    "attribute vec3 vertexPos;\n" + // vertex position attribute (handled per-vertex by GL after we bind vertex buffer and set up the attribute)
                    "uniform mat4 vertexTransform;\n" + // vertex transform uniform (applies to entire object, so we only set it once per frame)

                    "void main() {\n" +
                    "gl_Position = vertexTransform * vec4(vertexPos, 1.0);\n" + // vertex position output in clip-space is the vertex transform multiplied by position
                    "}";

    private static final String FRAGMENT_SHADER_SOURCE =
            "#version 120\n" + // version

                    "void main() {\n" +
                    "gl_FragColor = vec4(0, 0, 1, 1);\n" + // set the output color for each pixel/fragment to blue
                    "}";
    private static final String FRAGMENT_SHADER_SOURCE2 =
            "#version 120\n" + // version

                    "void main() {\n" +
                    "gl_FragColor = vec4(0, 1, 0, 1);\n" + // set the output color for each pixel/fragment to blue
                    "}";

    public final void init_pipeline(Renderable renderable)
    {
        loadGeometry(renderable);
        compileShaders(renderable);
    }

    private void loadGeometry(Renderable renderable)
    {
        float[] vertices = renderable.getVertices();
        mVertexCount = vertices.length / 3; // number of vertices to draw

        if(!renderable.isCached()) {
            // stuff contents of that vertex array into a float buffer so we can give it to GL
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
            vertexBuffer.put(vertices);
            vertexBuffer.flip();

            mVBo = GL15.glGenBuffers(); // create the vertex buffer

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVBo); // set it as the current buffer
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW); // upload vertex data to GL driver for our bound buffer
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind the buffer

            renderable.getEntity().getScene().getRenderer().add_mVBo(mVBo);
            renderable.getData().mVBo = mVBo;
        }
        renderable.getData().vertexCount = mVertexCount;
    }

    private void compileShaders(Renderable renderable)
    {
        int vsId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER); // create a vertex shader

        GL20.glShaderSource(vsId, VERTEX_SHADER_SOURCE); // hand GL our vertex shader code
        GL20.glCompileShader(vsId); // compile that code

        System.out.println("Vertex shader status: " + GL20.glGetShaderInfoLog(vsId)); // spit out any errors if compilation failed


        int fsId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER); // create a fragment shader

        //Just to test out using different shaders, need an actual way to implement this.
        Random rand = new Random();
        int n = rand.nextInt(2) + 1;
        if(n == 1)
            GL20.glShaderSource(fsId, FRAGMENT_SHADER_SOURCE); // hand GL our fragment shader code
        else
            GL20.glShaderSource(fsId, FRAGMENT_SHADER_SOURCE2); // hand GL our fragment shader code
        GL20.glCompileShader(fsId); // compile it

        System.out.println("Fragment shader status: " + GL20.glGetShaderInfoLog(fsId)); // spit out any errors if compilation failed

        mShaderProgram = GL20.glCreateProgram(); // create a shader program which we will attach our individual shaders to
        renderable.getData().mShaderProgram = mShaderProgram;

        GL20.glAttachShader(mShaderProgram, vsId); // attach vertex shader
        GL20.glAttachShader(mShaderProgram, fsId); // attach fragment shader

        GL20.glLinkProgram(mShaderProgram); // link it
        GL20.glValidateProgram(mShaderProgram); // validate it


        mVPosAttribute = GL20.glGetAttribLocation(mShaderProgram, "vertexPos"); // get the location of the vertex position attribute
        mVTransformUniform = GL20.glGetUniformLocation(mShaderProgram, "vertexTransform"); // get the location of the vertex transform uniform
        renderable.getData().mVPosAttribute = mVPosAttribute;
        renderable.getData().mVTransformUniform = mVTransformUniform;


        // cleanup (program is already made, individual shaders are no longer needed)
        GL20.glDeleteShader(vsId);
        GL20.glDeleteShader(fsId);
    }
}

import gasengine.Engine;
import gasengine.graphics.*;
import gasengine.messages.MessageHandler;
import gasengine.scene.Component;
import gasengine.scene.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;


public class TriangleRenderer extends Component
{
    private static final String VERTEX_SHADER_SOURCE =
            "#version 330\n" + // shading language version

                    "layout (location=0) in vec3 _vertexPos;\n" + // vertex position attribute (handled per-vertex by GL after we bind vertex buffer and set up the attribute)
                    "layout (location=1) in vec3 _vertexNorm;\n" +

                    "uniform mat4 _MVP;\n" + // vertex transform uniform (applies to entire object, so we only set it once per frame)
                    "uniform mat4 _World;\n" +

                    "out vec3 Normal0;\n" +

                    "void main() {\n" +
                    "gl_Position = _MVP * vec4(_vertexPos, 1.0);\n" + // vertex position output in clip-space is the vertex transform multiplied by position
                    "Normal0 = (_World * vec4(_vertexNorm, 0.0)).xyz;\n" +
                    "}";

    private static final String FRAGMENT_SHADER_SOURCE =
            "#version 330\n" + // version

                    "in vec3 Normal0;\n" +

                    "out vec4 FragColor;\n" +

                    "void main() {\n" +
                    "vec3 color = vec3(1, 1, 1);\n" +
                    "vec3 lightDir = vec3(1, -1, 0);\n" +
                    "float mul = dot(normalize(Normal0), -normalize(lightDir));\n" +

                    "if (mul > 0) {\n" +
                    "FragColor = vec4(color * mul, 1);\n" +
                    "} else {\n" +
                    "FragColor = vec4(0, 0, 0, 0);\n" +
                    "}\n" +
                    "}";

    private int mVertexCount;
    private int mVbo;
    private int mNormalBuffer;

    private float mScale = 1;
    private float mRotSpeed = 1;

    Shader mShader = new Shader(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE);
    Material mMat = new Material(mShader);

    public Vector3f computeNormal(Vector3f v1, Vector3f v2, Vector3f v3)
    {
        Vector3f d1 = new Vector3f(v3);
        d1.sub(v2);

        Vector3f d2 = new Vector3f(v1);
        d2.sub(v2);

        Vector3f norm = new Vector3f(d1);
        norm.cross(d2);
        norm.normalize();

        return norm;
    }

    public TriangleRenderer(String model, float targetSize, float rotSpeed)
    {
        mRotSpeed = rotSpeed;

        Mesh mesh = new Mesh(model);
        List<MeshData.Verts> vertices = mesh.getVertices();
        List<MeshData.Face> faces = mesh.getFaces();
        int dim = mesh.getFacesDim();

        List<Vector3f> vertList = new ArrayList<>();
        List<Vector3f> normList = new ArrayList<>();

        Vector3f normal;

        for (MeshData.Face face : faces)
        {
            List<Integer> t = face.indices;
            switch (face.indices.size())
            {
                case 3:
                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(1) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(2) - 1).vert);

                    normal = computeNormal(
                            vertices.get(face.indices.get(0) - 1).vert,
                            vertices.get(face.indices.get(1) - 1).vert,
                            vertices.get(face.indices.get(2) - 1).vert
                    );

                    normList.add(normal); normList.add(normal); normList.add(normal); // this is all terrible, inefficient and hacky

                    break;

                case 4:
                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(1) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(2) - 1).vert);

                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(2) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(3) - 1).vert);

                    normal = computeNormal(
                            vertices.get(face.indices.get(0) - 1).vert,
                            vertices.get(face.indices.get(1) - 1).vert,
                            vertices.get(face.indices.get(2) - 1).vert
                    );

                    normList.add(normal); normList.add(normal); normList.add(normal); // this is all terrible, inefficient and hacky
                    normList.add(normal); normList.add(normal); normList.add(normal);

                    break;
                case 6:
                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim) - 1).vert);
                    if(dim < 3)
                        vertList.add(vertices.get(face.indices.get(dim*2) - 1).vert);

                    normal = computeNormal(
                            vertices.get(face.indices.get(0) - 1).vert,
                            vertices.get(face.indices.get(dim) - 1).vert,
                            vertices.get(face.indices.get(dim*2) - 1).vert
                    );

                    normList.add(normal); normList.add(normal); normList.add(normal); // this is all terrible, inefficient and hacky

                    break;
                case 8:
                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim*2) - 1).vert);

                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim*2) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim*3) - 1).vert);

                    normal = computeNormal(
                            vertices.get(face.indices.get(0) - 1).vert,
                            vertices.get(face.indices.get(dim) - 1).vert,
                            vertices.get(face.indices.get(dim*2) - 1).vert
                    );

                    normList.add(normal); normList.add(normal); normList.add(normal); // this is all terrible, inefficient and hacky
                    normList.add(normal); normList.add(normal); normList.add(normal);

                    break;
                case 9:
                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim*2) - 1).vert);

                    normal = computeNormal(
                            vertices.get(face.indices.get(0) - 1).vert,
                            vertices.get(face.indices.get(dim) - 1).vert,
                            vertices.get(face.indices.get(dim*2) - 1).vert
                    );

                    normList.add(normal); normList.add(normal); normList.add(normal); // this is all terrible, inefficient and hacky

                    break;
                case 12:
                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim*2) - 1).vert);

                    vertList.add(vertices.get(face.indices.get(0) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim*2) - 1).vert);
                    vertList.add(vertices.get(face.indices.get(dim*3) - 1).vert);

                    normal = computeNormal(
                            vertices.get(face.indices.get(0) - 1).vert,
                            vertices.get(face.indices.get(dim) - 1).vert,
                            vertices.get(face.indices.get(dim*2) - 1).vert
                    );

                    normList.add(normal); normList.add(normal); normList.add(normal); // this is all terrible, inefficient and hacky
                    normList.add(normal); normList.add(normal); normList.add(normal);

                    break;
                default:
                    throw new RuntimeException("Tried to load invalid obj file");
            }
        }

        float maxLen = 0;

        mVertexCount = vertList.size();

        for (int i = 0; i < mVertexCount; i++)
        {
            float len = vertList.get(i).length();

            if (len > maxLen)
                maxLen = len;
        }

        mScale = targetSize / maxLen;

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(mVertexCount * 3);
            vertList.forEach(vert -> {
                vertexBuffer.put(vert.x);
                vertexBuffer.put(vert.y);
                vertexBuffer.put(vert.z);
            });
        vertexBuffer.flip();

        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normList.size() * 3);
        normList.forEach(norm -> {
            normalBuffer.put(norm.x);
            normalBuffer.put(norm.y);
            normalBuffer.put(norm.z);
        });
        normalBuffer.flip();

        mVbo = GL15.glGenBuffers(); // create the vertex buffer

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVbo); // set it as the current buffer
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW); // upload vertex data to GL driver for our bound buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind the buffer



        mNormalBuffer = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mNormalBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    @MessageHandler.MessageHook("Render")
    public void render()
    {
        Entity ent = getEntity();

        Quaternionf q = new Quaternionf();

        q.rotateAxis(
                (float) Math.toRadians(
                        ((((double) System.currentTimeMillis() * mRotSpeed) % 1000) / 1000) * -360 // once very second
                ),
                0, 1, 0
        );

        Engine.getRenderSystem()
            .buildModelMatrix(ent.getPosition(new Vector3f()), q, mScale);

        glEnable(GL_DEPTH_TEST); // FIXME this should probably go in the shader
        glDepthFunc(GL_LESS);

        mMat.bind();

        for (Shader.AttributeInfo attr : mShader.getAttributes())
        {
            switch (attr.getName())
            {
                case "_vertexPos":
                    GL20.glEnableVertexAttribArray(attr.getIndex());
                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVbo); // bind the vertex buffer for the following calls
                    GL20.glVertexAttribPointer( // tell it how that data is packed into the vertex buffer and which input it goes to in the shader
                        attr.getIndex(), // location of the vertex shader's vertex position attribute
                        3,              // size per vertex (3, since it's just the position (x, y, z))
                        GL_FLOAT,       // format
                        false,          // normalize
                        0,              // stride (this and the next one are useful if the buffer has stuff packed in other than just positions)
                        0               // offset
                    );

                    break;

                case "_vertexNorm":
                    GL20.glEnableVertexAttribArray(attr.getIndex());
                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mNormalBuffer); // bind the vertex buffer for the following calls
                    GL20.glVertexAttribPointer( // tell it how that data is packed into the vertex buffer and which input it goes to in the shader
                        attr.getIndex(), // location of the vertex shader's vertex position attribute
                        3,              // size per vertex (3, since it's just the position (x, y, z))
                        GL_FLOAT,       // format
                        false,          // normalize
                        0,              // stride (this and the next one are useful if the buffer has stuff packed in other than just positions)
                        0               // offset
                    );

                    break;
            }
        }

        glDrawArrays(GL_TRIANGLES, 0, mVertexCount);

        // TODO cleanup and binding will be easier and faster with VAOs

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        for (Shader.AttributeInfo attr : mShader.getAttributes())
        {
            switch (attr.getName())
            {
                case "_vertexPos":
                case "_vertexNorm":
                    GL20.glDisableVertexAttribArray(attr.getIndex());

                    break;
            }
        }
    }
}
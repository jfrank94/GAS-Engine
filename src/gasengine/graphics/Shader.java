package gasengine.graphics;

import gasengine.Engine;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;


public class Shader
{
    int mShaderProgram;

    List<AttributeInfo> mShaderAttributes;
    List<UniformInfo> mShaderUniforms;

    // TODO shader caching (same for materials, textures, etc.)
    public Shader(String vertSrc, String fragSrc)
    {
        int vsId = compileShader(GL20.GL_VERTEX_SHADER, vertSrc);
        int fsId = compileShader(GL20.GL_FRAGMENT_SHADER, fragSrc);

        mShaderProgram = GL20.glCreateProgram();

        GL20.glAttachShader(mShaderProgram, vsId);
        GL20.glAttachShader(mShaderProgram, fsId);

        GL20.glLinkProgram(mShaderProgram);
        GL20.glValidateProgram(mShaderProgram);

        // cleanup (program is already made, individual shaders are no longer needed)
        GL20.glDeleteShader(vsId);
        GL20.glDeleteShader(fsId);
    }

    public Shader(String name)
    {
        // searches in shaders/ directory for name.vert and name.frag
    }

    @Override
    public void finalize() throws Throwable
    {
        GL20.glDeleteProgram(mShaderProgram);

        super.finalize();
    }

    private int compileShader(int type, String src)
    {
        int id = GL20.glCreateShader(type);

        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);

        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader compilation failed: " + GL20.glGetShaderInfoLog(id));

        return id;
    }

    public List<AttributeInfo> getAttributes()
    {
        if (mShaderAttributes == null)
        {
            mShaderAttributes = new ArrayList<>();

            int numAttribs = GL20.glGetProgrami(mShaderProgram, GL20.GL_ACTIVE_ATTRIBUTES);
            int bufSize = GL20.glGetProgrami(mShaderProgram, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);

            for (int i = 0; i < numAttribs; i++)
            {
                IntBuffer lengthBuf = BufferUtils.createIntBuffer(1); // we can discard this one
                IntBuffer sizeBuf = BufferUtils.createIntBuffer(1);
                IntBuffer typeBuf = BufferUtils.createIntBuffer(1);
                ByteBuffer nameBuf = BufferUtils.createByteBuffer(bufSize);

                GL20.glGetActiveAttrib(mShaderProgram, i, lengthBuf, sizeBuf, typeBuf, nameBuf);

                byte[] nameBytes = new byte[nameBuf.remaining() - 1]; // we don't need the null character
                nameBuf.get(nameBytes);
                String name = new String(nameBytes).trim(); // puts whitespace at the end for some stupid reason (even though I got rid of the null character...)

                int index = GL20.glGetAttribLocation(mShaderProgram, name);

                mShaderAttributes.add(new AttributeInfo(
                    sizeBuf.get(),
                    typeBuf.get(),
                    name,
                    index
                ));
            }

            mShaderAttributes = Collections.unmodifiableList(mShaderAttributes); // so the immutable conversion only needs to be done once
        }

        return mShaderAttributes;
    }

    public List<UniformInfo> getUniforms()
    {
        if (mShaderUniforms == null)
        {
            mShaderUniforms = new ArrayList<>();

            int numUniforms = GL20.glGetProgrami(mShaderProgram, GL20.GL_ACTIVE_UNIFORMS);
            int bufSize = GL20.glGetProgrami(mShaderProgram, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);

            for (int i = 0; i < numUniforms; i++)
            {
                IntBuffer sizeBuf = BufferUtils.createIntBuffer(1);
                IntBuffer typeBuf = BufferUtils.createIntBuffer(1);
                ByteBuffer nameBuf = BufferUtils.createByteBuffer(bufSize);

                GL20.glGetActiveUniform(mShaderProgram, i, null, sizeBuf, typeBuf, nameBuf);

                byte[] nameBytes = new byte[nameBuf.remaining() - 1]; // we don't need the null character
                nameBuf.get(nameBytes);
                String name = new String(nameBytes).trim(); // trim here for good measure...

                int location = GL20.glGetUniformLocation(mShaderProgram, name);

                mShaderUniforms.add(new UniformInfo(
                    sizeBuf.get(),
                    typeBuf.get(),
                    name,
                    location
                ));
            }

            mShaderUniforms = Collections.unmodifiableList(mShaderUniforms); // so the immutable conversion only needs to be done once
        }

        return mShaderUniforms;
    }

    public void bind()
    {
        RenderSystem renderSys = Engine.getRenderSystem();

        GL20.glUseProgram(mShaderProgram);

        for (UniformInfo uniform : getUniforms())
        {
            switch (uniform.getName())
            {
                case "_MVP": // model view projection matrix transform
                    FloatBuffer mvpBuf = BufferUtils.createFloatBuffer(16); // TODO fix allocations here for efficiency
                    renderSys.getMVP().get(mvpBuf);

                    GL20.glUniformMatrix4fv(uniform.getLocation(), false, mvpBuf);

                    break;

                case "_World":
                    FloatBuffer wmBuf = BufferUtils.createFloatBuffer(16); // TODO fix allocations here for efficiency
                    Matrix4f wm = new Matrix4f();
                    renderSys.getModelMatrix(wm);
                    wm.get(wmBuf);

                    GL20.glUniformMatrix4fv(uniform.getLocation(), false, wmBuf);

                    break;
            }
        }
    }


    public static class AttributeInfo
    {
        private int mSize;
        private int mType;
        private String mName;
        private int mIndex;

        public AttributeInfo(int size, int type, String name, int index)
        {
            mSize = size;
            mType = type;
            mName = name;
            mIndex = index;
        }

        public int getSize()
        {
            return mSize;
        }

        public int getType()
        {
            return mType;
        }

        public String getName()
        {
            return mName;
        }

        public int getIndex()
        {
            return mIndex;
        }
    }

    public static class UniformInfo
    {
        private int mSize;
        private int mType;
        private String mName;
        private int mLocation;

        public UniformInfo(int size, int type, String name, int location)
        {
            mSize = size;
            mType = type;
            mName = name;
            mLocation = location;
        }

        public int getSize()
        {
            return mSize;
        }

        public int getType()
        {
            return mType;
        }

        public String getName()
        {
            return mName;
        }

        public int getLocation()
        {
            return mLocation;
        }
    }
}

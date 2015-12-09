package gasengine.graphics;

import gasengine.Engine;
import gasengine.collections.SimpleHashMap;
import gasengine.graphics.components.Camera;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
//import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;


public class RenderSystem
{
    private long mWindow = NULL;

    private int mWidth = 1024;
    private int mHeight = 1024;

    private float mFov = 45f;
    private float mZNear = 0.01f;
    private float mZFar = 10000f;

    private Matrix4f mProjectionMatrix = new Matrix4f();
    private Matrix4f mViewMatrix = new Matrix4f();
    private Matrix4f mModelMatrix = new Matrix4f();

    private Matrix4f mMVP = new Matrix4f();

    GLFWFramebufferSizeCallback mFramebufferSizeCallback;
    GLFWKeyCallback mKeyCallback;

    public RenderSystem(SimpleHashMap params)
    {
        mWidth = params.getInteger("win_width", mWidth);
        mHeight = params.getInteger("win_height", mHeight);

        mFov = params.getFloat("fov", mFov);
        mZNear = params.getFloat("znear", mZNear);
        mZFar = params.getFloat("zfar", mZFar);


        if (glfwInit() != GL11.GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // set some window/GL parameters
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        // create window
        mWindow = glfwCreateWindow(mWidth, mHeight, "GAS-Engine", NULL, NULL);
        if (mWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // get resolution of primary monitor so we can center the window
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(
                mWindow,
                (GLFWvidmode.width(vidmode) - mWidth) / 2,
                (GLFWvidmode.height(vidmode) - mHeight) / 2
        );

        glfwMakeContextCurrent(mWindow); // make the GL context current
        glfwSwapInterval(1); // enable v-sync

        glfwShowWindow(mWindow);

        GLContext.createFromCurrent(); // create the GL context

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set the background color

        // set the window resize callback
        glfwSetFramebufferSizeCallback(mWindow, mFramebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int w, int h)
            {
                mWidth = w;
                mHeight = h;

                GL11.glViewport(0, 0, mWidth, mHeight); // update the viewport dimensions

                rebuildProjectionMatrix();
            }
        });

        glfwSetKeyCallback(mWindow, mKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, GL_TRUE); // mark the window to be closed
            }
        });

        rebuildProjectionMatrix();
    }

    public boolean isRunning()
    {
        return glfwWindowShouldClose(mWindow) == GL_FALSE;
    }

    public long getWindowHandle()
    {
        return mWindow;
    }

    public void renderScene()
    {
        if (!isRunning())
            return;

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // assume only one camera for now (order is not considered)
        Engine.getScene()
            .getEntitiesWithComponent(Camera.class)
            .forEach(cam -> cam.getComponent(Camera.class).renderView());

        int err = glGetError();

        if (err != GL_NO_ERROR)
        {
            String error = "?";

            switch (err)
            {
                case GL_INVALID_OPERATION:      error="INVALID_OPERATION";      break;
                case GL_INVALID_ENUM:           error="INVALID_ENUM";           break;
                case GL_INVALID_VALUE:          error="INVALID_VALUE";          break;
                case GL_OUT_OF_MEMORY:          error="OUT_OF_MEMORY";          break;
            }

            throw new RuntimeException("There was a GL error: " + error + "[" + err + "]");
        }

        glfwSwapBuffers(mWindow);
        glfwPollEvents();
    }

    public void shutDown()
    {
        glfwDestroyWindow(mWindow);
        glfwTerminate();
    }


    private void updateMVP()
    {
        mMVP.set(mProjectionMatrix)
            .mul(mViewMatrix)
            .mul(mModelMatrix);
    }

    private void rebuildProjectionMatrix()
    {
        mProjectionMatrix.setPerspective(
            (float) Math.toRadians(mFov),
            (float) mWidth / mHeight,
            mZNear, mZFar
        );

        updateMVP();
    }

    public void buildViewMatrix(Vector3f camPos, Vector3f camTarget, Vector3f camUp)
    {
        mViewMatrix.setLookAt(camPos, camTarget, camUp);

        updateMVP();
    }

    public void buildModelMatrix(Vector3f pos, Quaternionf rot, float scale)
    {
        mModelMatrix
            .identity()
            .translate(pos)
            .rotate(rot)
            .scale(scale);

        updateMVP();
    }

    public void buildModelMatrix(Vector3f pos, Quaternionf rot)
    {
        buildModelMatrix(pos, rot, 1);
    }

    public void getModelMatrix(Matrix4f mat)
    {
        mat.set(mModelMatrix);
    }

    public Matrix4f getMVP() // FIXME should not allocate a new matrix each time
    {
        return new Matrix4f().set(mMVP);
    }
}

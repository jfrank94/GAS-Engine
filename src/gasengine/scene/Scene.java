package gasengine.scene;

import gasengine.renderer.Renderer;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Scene
{
    private long mNextEntId = 0;
    private List<Entity> mEntities = new ArrayList<>();

    private static long mWindow = NULL;
    private static int mWidth = 1024;
    private static int mHeight = 1024;
    private static Vector3f mCamPos = new Vector3f(0, 100, 100);
    private static Vector3f mCamDir = new Vector3f(0, -1, -1).normalize();

    private Renderer renderer;

    public Scene()
    {
        setupContext();
        renderer = new Renderer();
    }

    public Entity addEntity()
    {
        Entity ent = new Entity(this, mNextEntId);

        mEntities.add(ent);
        mNextEntId++;

        return ent;
    }

    void removeEntity(Entity ent) // package visible only (users call ent.destroy() instead)
    {
        mEntities.remove(ent);
    }

    public List<Entity> getEntities()
    {
        return Collections.unmodifiableList(mEntities);
    }

    public Entity getEntityById(long id)
    {
        return mEntities
            .stream()
            .filter(ent -> ent.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<Entity> findEntitiesByTag(String tag)
    {
        return mEntities
            .stream()
            .filter(ent -> ent.hasTag(tag))
            .collect(Collectors.toList());
    }

    public void updateScene()
    {
        /*List<Entity> camera = findEntitiesByTag("camera");
        if(camera.size() == 0)
            throw new RuntimeException("No Camera Entity initialized");
        camera.forEach(cam -> {
                    cam.update();
                });*/
        updateCameraDir();
        renderer.render(mEntities);
    }

    public long getmWindow()
    {
        return mWindow;
    }

    //temp hacks
    public Vector3f getmCamPos()
    {
        return mCamPos;
    }
    public Vector3f getmCamDir()
    {
        return mCamDir;
    }

    public Renderer getRenderer()
    {
        return renderer;
    }

    private static GLFWKeyCallback winKeyCallBack; //Need to store callback instance so it isn't GC.
    public static void setupContext()
    {
        if (glfwInit() != GL11.GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // set some window/GL parameters
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        // create window
        mWindow = glfwCreateWindow(mWidth, mHeight, "Hello World!", NULL, NULL);
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

        //GLContext.createFromCurrent(); // create the GL context
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set the background color

        // set the key callback
        glfwSetKeyCallback(mWindow, winKeyCallBack = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, GL_TRUE); // mark the window to be closed

                Vector3f dir = new Vector3f(0, 0, 0);

                if (key == GLFW_KEY_A)
                {
                    dir.set(mCamDir);
                    dir.cross(new Vector3f(0, -1, 0));
                    dir.normalize();
                }
                else if (key == GLFW_KEY_D)
                {
                    dir.set(mCamDir);
                    dir.cross(new Vector3f(0, 1, 0));
                    dir.normalize();
                }
                else if (key == GLFW_KEY_W)
                {
                    dir.set(mCamDir);
                }
                else if (key == GLFW_KEY_S)
                {
                    dir.set(mCamDir);
                    dir.mul(-1);
                }

                mCamPos.add(dir);
            }
        });

        // set the window resize callback
        glfwSetFramebufferSizeCallback(mWindow, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int w, int h)
            {
                mWidth = w;
                mHeight = h;

                GL11.glViewport(0, 0, mWidth, mHeight); // update the viewport dimensions
            }
        });
    }

    private double mLastX = 0;
    private double mLastY = 0;

    private long mLastTime = 0;

    private boolean mMouseDown = false;

    private void updateCameraDir()
    {
        DoubleBuffer dbx = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer dby = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(mWindow, dbx, dby);

        double x = dbx.get(0);
        double y = dby.get(0);

        if (glfwGetMouseButton(mWindow, GLFW_MOUSE_BUTTON_LEFT) != 1)
        {
            mLastTime = System.currentTimeMillis();
            mMouseDown = false;

            return;
        }

        if (!mMouseDown)
        {
            mLastTime = System.currentTimeMillis();

            mLastX = x;
            mLastY = y;

            mMouseDown = true;

            return;
        }

        long dt = System.currentTimeMillis() - mLastTime;

        double dx = x - mLastX;
        double dy = y - mLastY;

        Matrix3f mat = new Matrix3f();

        mat.rotation((float)-dx * 0.0001f * dt, new Vector3f(0, 1, 0));

        mCamDir.mul(mat);
        mCamDir.normalize();

        Vector3f right = new Vector3f(mCamDir);
        right.cross(new Vector3f(0, 1, 0));

        mat.rotation((float) -dy * 0.0001f * dt, right);

        mCamDir.mul(mat);
        mCamDir.normalize();

        mLastX = x;
        mLastY = y;

        mLastTime = System.currentTimeMillis();
    }
}

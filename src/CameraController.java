import gasengine.Engine;
import gasengine.scene.Component;
import gasengine.scene.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;


public class CameraController extends Component
{
    private double mLastX = 0;
    private double mLastY = 0;

    private long mLastTime = 0;

    private boolean mMouseDown = false;

    private void updateAngles()
    {
        Entity cam = getEntity();
        long window = Engine.getRenderSystem().getWindowHandle();

        DoubleBuffer dbx = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer dby = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(window, dbx, dby);

        double x = dbx.get(0);
        double y = dby.get(0);

        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) != 1)
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

        Quaternionf rot = cam.getRotation(new Quaternionf());
        //rot.rotateAxis((float)-dx * 0.0003f * dt, new Vector3f(0, 1, 0));

            /*Vector3f dir = new Vector3f(0, 0, -1);
            rot.transform(dir);
            dir.normalize();

            Vector3f up = new Vector3f(0, 1, 0);
            rot.transform(up);
            rot.normalize();

            Vector3f right = new Vector3f(dir);
            right.cross(up);
            right.normalize();*/



        //rot.rotateAxis((float)-dy * 0.0003f * dt, new Vector3f(1, 0, 0));
        //rot.rotateAxis((float)-dy * 0.0003f * dt, right);

        // FIXME this part is not perfect, but it kinda works; I need to read up on using quaternions properly

        cam.getRotation(rot);

        Quaternionf r1 = new Quaternionf();
        r1.rotateAxis((float)-dx * 0.0003f * dt, new Vector3f(0, 1, 0));

        Quaternionf r2 = new Quaternionf();
        r2.rotateAxis((float)-dy * 0.0003f * dt, new Vector3f(1, 0, 0));

        r1.mul(r2);
        rot.mul(r1);


        cam.setRotation(rot);

        mLastX = x;
        mLastY = y;

        mLastTime = System.currentTimeMillis();
    }

    private void updatePosition()
    {
        Entity cam = getEntity();
        long window = Engine.getRenderSystem().getWindowHandle();

        Vector3f mCamDir = new Vector3f(0, 0, -1);
        cam.getRotation(new Quaternionf()).transform(mCamDir);

        Vector3f dir = new Vector3f(0, 0, 0);

        if (glfwGetKey(window, GLFW_KEY_A) == GL_TRUE)
        {
            dir.set(mCamDir);
            dir.cross(new Vector3f(0, -1, 0));
            dir.normalize();
        }
        else if (glfwGetKey(window, GLFW_KEY_D) == GL_TRUE)
        {
            dir.set(mCamDir);
            dir.cross(new Vector3f(0, 1, 0));
            dir.normalize();
        }
        else if (glfwGetKey(window, GLFW_KEY_W) == GL_TRUE)
        {
            dir.set(mCamDir);
        }
        else if (glfwGetKey(window, GLFW_KEY_S) == GL_TRUE)
        {
            dir.set(mCamDir);
            dir.mul(-1);
        }

        cam.setPosition(cam.getPosition(new Vector3f()).add(dir));
    }

    @MessageHook("Update")
    public void update()
    {
        updateAngles();
        updatePosition();
    }
}
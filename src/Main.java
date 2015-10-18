import gasengine.Engine;
import gasengine.renderer.Renderable;
import gasengine.scene.Entity;
import gasengine.scene.Scene;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;


public class Main
{
    public static void main(String[] args)
    {
        Engine.initialize();

        Scene scene = Engine.getScene();

        scene.setupContext();
        scene = demo(scene);

        Engine.run();

        while (glfwWindowShouldClose(scene.getmWindow()) == GL_FALSE)
        {
            scene.updateScene();
        }

        Engine.shutDown();
    }

    public static Scene demo(Scene scene)
    {
        // Square
        float[] verts = {
                -1.0f,-1.0f,-1.0f,
                -1.0f,-1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f,-1.0f,
                -1.0f,-1.0f,-1.0f,
                -1.0f, 1.0f,-1.0f,
                1.0f,-1.0f, 1.0f,
                -1.0f,-1.0f,-1.0f,
                1.0f,-1.0f,-1.0f,
                1.0f, 1.0f,-1.0f,
                1.0f,-1.0f,-1.0f,
                -1.0f,-1.0f,-1.0f,
                -1.0f,-1.0f,-1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f,-1.0f,
                1.0f,-1.0f, 1.0f,
                -1.0f,-1.0f, 1.0f,
                -1.0f,-1.0f,-1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f,-1.0f, 1.0f,
                1.0f,-1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f,-1.0f,-1.0f,
                1.0f, 1.0f,-1.0f,
                1.0f,-1.0f,-1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f,-1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f,-1.0f,
                -1.0f, 1.0f,-1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f,-1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f,-1.0f, 1.0f
        };

        Entity entity = scene.addEntity();
        entity.setPosition(0, 0, 0);
        Renderable cmp = new Renderable();
        cmp.setVertices(verts);
        entity.addComponent(cmp);

        for(int x = -5; x < 5; ++x) {
            for(int z = -5; z < 5; ++z) {
                Entity entity2 = scene.addEntity();
                entity2.setPosition(x * 5, 0, z * 5);
                Renderable cmp2 = new Renderable(cmp.getData().mVBo); //Just copying the vertex buffer id
                cmp2.setVertices(verts);
                entity2.addComponent(cmp2);
            }
        }
        return scene;
    }
}

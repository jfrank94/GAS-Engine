import gasengine.Engine;
import gasengine.collections.SimpleHashMap;
import gasengine.graphics.components.Camera;
import gasengine.scene.Entity;
import gasengine.scene.Scene;

import java.awt.*;


public class Main
{
    public static void main(String[] args)
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        Engine.initialize(
            new SimpleHashMap()
                .set("win_width", (int)(width * (2f/3)))
                .set("win_height", (int)(height * (2f/3)))
        );

        Scene scene = Engine.getScene();

        Entity cam = scene.addEntity();
            cam.addComponent(new Camera());
            cam.addComponent(new CameraController());
            cam.setPosition(0, 0, 10);

        Entity arc = scene.addEntity();
            arc.setPosition(-5, 0, 0);
            arc.addComponent(new TriangleRenderer("arc.obj", 5, -0.05f));

        Entity teapot = scene.addEntity();
            teapot.setPosition(5, 0, 0);
            teapot.addComponent(new TriangleRenderer("teapot.obj", 2, 0.5f));

        Entity island = scene.addEntity();
            island.setPosition(0, -50, 0);
            island.addComponent(new TriangleRenderer("island.obj", 50, 0f));

        Engine.run();

        Engine.shutDown();
    }
}

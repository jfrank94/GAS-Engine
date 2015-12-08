import gasengine.Engine;
import gasengine.collections.SimpleHashMap;
import gasengine.graphics.components.Camera;
import gasengine.scene.Entity;
import gasengine.scene.Scene;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Main
{
    public static void main(String[] args)
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        Engine.initialize(
                new SimpleHashMap()
                        .set("win_width", (int) (width * (2f / 3)))
                        .set("win_height", (int) (height * (2f / 3)))
        );

        Scene scene = Engine.getScene();

        Entity cam = scene.addEntity();
            cam.addComponent(new Camera());
            cam.addComponent(new CameraController());
            cam.setPosition(0, 0, 10);


        Entity arc = scene.addEntity();
            arc.setPosition(-10, 10, -20);
            arc.addComponent(new TexturedTriangleRenderer("arc.obj", "arc.png", 5, -0.05f));

        /*Entity teapot = scene.addEntity();
            teapot.setPosition(5, 0, -20);
            teapot.addComponent(new TriangleRenderer("teapot.obj", 2, 0.5f));*/

        Entity island = scene.addEntity();
            island.setPosition(-20, -27, -10);
            island.addComponent(new TexturedTriangleRenderer("island.obj", "grass.png", 10, 0f));

        /*Entity yoda = scene.addEntity();
            yoda.setPosition(15, -36, 0);
            yoda.addComponent(new TriangleRenderer("yoda.obj", 5, 0.0f));*/

        Entity moon = scene.addEntity();
            moon.setPosition(-100, 250, -800);
            moon.addComponent(new TriangleRenderer("moon.obj", 200, 0.005f));

        Entity city = scene.addEntity();
        city.setPosition(100, -40, -20);
        city.addComponent(new TexturedTriangleRenderer("sirus.obj", "cga1.png", 400, 0.0f));

        Engine.run();

        Engine.shutDown();
    }
}

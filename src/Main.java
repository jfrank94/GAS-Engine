import gasengine.Engine;
import gasengine.audio.Sound;
import gasengine.audio.SoundComponent;
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
        System.setProperty("org.lwjgl.util.Debug", "true");

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
            arc.setPosition(-10, 10, -35);
            arc.addComponent(new TexturedTriangleRenderer("arc.obj", "arc.png", 5, 0.0f));
            SoundComponent snd = arc.addComponent(new SoundComponent("air_raid.wav"));
                snd.setLoop(true);
                snd.play();

        Entity teapot = scene.addEntity();
            teapot.setPosition(5, 0, -20);
            teapot.addComponent(new TriangleRenderer("teapot.obj", 2, 0.5f));
            //teapot.addComponent(new Sound());

        /*Entity island = scene.addEntity();
            island.setPosition(-200, -50, 100);
            island.addComponent(new TexturedTriangleRenderer("island.obj", "grass.png", 100, 0f));*/

        /*Entity yoda = scene.addEntity();
            yoda.setPosition(15, -36, 0);
            yoda.addComponent(new TriangleRenderer("yoda.obj", 5, 0.0f));*/

        Entity moon = scene.addEntity();
            moon.setPosition(-625, 325, -650);
            moon.addComponent(new TexturedTriangleRenderer("moon.obj", "moon-4k.png", 50, 0.02f));

        Entity planet = scene.addEntity();
            planet.setPosition(-100, 250, -800);
            planet.addComponent(new TexturedTriangleRenderer("Earth.obj", "Earth_D.png", 175, 0.002f));

        Entity city = scene.addEntity();
            city.setPosition(-628, 373, -645);
            //city.addComponent(new TriangleRenderer("colony.obj", 600, 0.0f));
            city.addComponent(new TexturedTriangleRenderer("atlantis.obj", "cga1.png", 10, 0.025f));

        Entity city2 = scene.addEntity();
            city2.setPosition(-300, -200, -350);
            city2.addComponent(new TexturedTriangleRenderer("colony.obj", "fac_231.png", 400, 0.01f));

        Engine.run();

        Engine.shutDown();
    }
}

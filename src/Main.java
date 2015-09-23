import gasengine.Engine;
import gasengine.messages.MessageReceiver;
import gasengine.scene.Component;
import gasengine.scene.Entity;
import gasengine.scene.Scene;


public class Main
{
    public static void main(String[] args)
    {
        Engine.initialize();

        Scene scene = Engine.getScene();

        Entity entity = scene.addEntity();
            entity.setPosition(0, 0, 0);

        Engine.run();

        Engine.shutDown();
    }
}

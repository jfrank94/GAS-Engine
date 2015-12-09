package gasengine.audio;

import gasengine.Engine;
import gasengine.graphics.components.Camera;
import gasengine.scene.Component;
import gasengine.scene.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class SoundComponent extends Component
{
    private Sound mSound;

    public SoundComponent(String name)
    {
        mSound = new Sound(name);
    }


    public void play()
    {
        mSound.play();
    }

    public void pause()
    {
        mSound.pause();
    }

    public void stop()
    {
        mSound.stop();
    }


    public void setLoop(boolean loop)
    {
        mSound.setLoop(loop);
    }


    @MessageHook("Update")
    public void update()
    {
        Entity cam = Engine.getScene().getEntitiesWithComponent(Camera.class).stream().findAny().orElse(null);
        if (cam == null)
            return;

        Vector3f rpos = getEntity().getPosition(new Vector3f())
            .sub(cam.getPosition(new Vector3f()));

        cam.getRotation(new Quaternionf()).conjugate().transform(rpos);

        mSound.setPosition(rpos);
    }
}

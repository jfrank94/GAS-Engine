package gasengine.audio;

import gasengine.Engine;
import org.lwjgl.openal.*;

import java.nio.ByteBuffer;

/**
 * Created by Matthew on 12/8/2015.
 */
public class SoundSystem {
    private static long device;
    private static long context;

    public SoundSystem(){
        device = ALC10.alcOpenDevice((ByteBuffer)null);
        context = ALC10.alcCreateContext(device,(ByteBuffer)null);
        ALC10.alcMakeContextCurrent(context);
    }

    public void updateSounds() {
        Engine.getScene()
                .getEntitiesWithComponent(Sound.class)
                .forEach(sound -> sound.getComponent(Sound.class).update());
    }
}

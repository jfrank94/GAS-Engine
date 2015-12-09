package gasengine.audio;

import org.lwjgl.openal.ALContext;


public class SoundSystem
{
    public SoundSystem()
    {
        ALContext.create();
    }
}

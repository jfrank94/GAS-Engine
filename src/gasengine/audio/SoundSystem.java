package gasengine.audio;

import gasengine.collections.SimpleHashMap;
import org.lwjgl.openal.ALContext;


public class SoundSystem
{
    public SoundSystem(SimpleHashMap params)
    {
        ALContext.create();
    }
}

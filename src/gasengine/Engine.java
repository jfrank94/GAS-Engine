package gasengine;

import gasengine.audio.SoundSystem;
import gasengine.collections.SimpleHashMap;
import gasengine.graphics.RenderSystem;
import gasengine.scene.Scene;


public final class Engine
{
    private static RenderSystem sRenderSystem;
    private static SoundSystem sSoundSystem;

    private static Scene sScene;

    private static float sTimeScale;
    private static double sTime;
    private static double sDeltaTime;

    private static long sLastUpdate; // real time (ns)

    private static boolean sRun = false;

    private static boolean sInit = false;


    private Engine() {}

    private static void assertInitialized()
    {
        if (!sInit)
            throw new IllegalStateException("Engine not initialized");
    }


    public static void initialize(SimpleHashMap params)
    {
        if (sInit)
            throw new RuntimeException("Tried to re-initialize Engine");

        if (params == null)
            params = new SimpleHashMap();

        sRenderSystem = new RenderSystem(params);
        sSoundSystem = new SoundSystem(params);

        sScene = new Scene();

        sTimeScale = params.getFloat("timescale", 1f);
        sTime = 0;
        sDeltaTime = 0;

        sLastUpdate = -1;

        sRun = false;

        sInit = true;
    }

    public static void initialize()
    {
        initialize(null);
    }

    public static void shutDown()
    {
        assertInitialized();

        sRenderSystem.shutDown();

        sRenderSystem = null;
        sScene = null;

        sInit = false;
    }


    public static void step(double dt)
    {
        assertInitialized();

        sDeltaTime = dt * sTimeScale;
        sTime += sDeltaTime;

        sScene.broadcastMessage("Update", sDeltaTime);

        // NOTE physics simulation may want to use fixed time slices in its update routine (http://fabiensanglard.net/timer_and_framerate/)

        sRenderSystem.renderScene();
    }


    public static void run()
    {
        assertInitialized();

        sRun = true;

        sLastUpdate = -1;

        while (sRun && sRenderSystem.isRunning())
        {
            long curTime = System.nanoTime();

            if (sLastUpdate < 0)
                sLastUpdate = curTime;

            step(((double)(curTime - sLastUpdate)) / 1e9);

            sLastUpdate = curTime;
        }
    }

    public static void stop()
    {
        sRun = false;
    }


    public static void setTimeScale(float scale)
    {
        sTimeScale = scale;
    }

    public static float getTimeScale()
    {
        return sTimeScale;
    }

    public static double getDeltaTime()
    {
        return sDeltaTime;
    }

    public static double getTime()
    {
        return sTime;
    }


    public static Scene getScene()
    {
        assertInitialized();

        return sScene;
    }

    public static RenderSystem getRenderSystem()
    {
        assertInitialized();

        return sRenderSystem;
    }
}

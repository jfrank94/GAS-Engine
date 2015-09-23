package gasengine;

import gasengine.scene.Scene;


public final class Engine
{
    private static Scene sScene;

    private static boolean sInit = false;


    private Engine() {}

    private static void assertInitialized()
    {
        if (!sInit)
            throw new RuntimeException("Engine not initialized");
    }


    public static void initialize()
    {
        if (sInit)
            throw new RuntimeException("Tried to re-initialize Engine");

        sScene = new Scene();

        sInit = true;
    }

    public static void shutDown()
    {
        assertInitialized();

        // TODO shutdown

        sInit = false;
    }

    public static void run()
    {
        assertInitialized();
    }


    public static Scene getScene()
    {
        assertInitialized();

        return sScene;
    }
}

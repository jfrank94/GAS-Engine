package gasengine.graphics.components;

import gasengine.Engine;
import gasengine.graphics.RenderSystem;
import gasengine.scene.Component;
import gasengine.scene.Entity;
import gasengine.scene.Scene;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class Camera extends Component
{
    @MessageHook
    public void Initialize()
    {
        int count = Engine.getScene().getEntitiesWithComponent(Camera.class).size();

        if (count > 1)
        {
            destroy();

            throw new IllegalStateException("Only one camera is allowed in the scene at a time.");
        }
    }


    public void renderView()
    {
        RenderSystem renderSys = Engine.getRenderSystem();
        Entity ent = getEntity();

        Vector3f pos = ent.getPosition(new Vector3f());
        Quaternionf rot = ent.getRotation(new Quaternionf());

        Vector3f dir = new Vector3f(0, 0, -1);
            rot.transform(dir);

        dir.normalize();

        Vector3f targ = new Vector3f(pos).add(dir); // FIXME all of this crap

        renderSys.buildViewMatrix(pos, targ, new Vector3f(0, 1, 0));

        Scene scene = Engine.getScene();

        scene.broadcastMessage("Render");
    }
}

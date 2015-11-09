package gasengine.scene;

import gasengine.messages.MessageHandler;

import java.lang.annotation.*;


public abstract class Component extends MessageHandler
{
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresComponent
    {
        Class<? extends Component> value();
    }


    private boolean mInit;

    private Entity mEntity;
    private boolean mValid;


    final void initialize(Entity ent) // package visible only (called by the entity when added)
    {
        if (mInit)
            throw new RuntimeException("Tried to re-initialize component");

        mEntity = ent;
        mValid = true;

        ensureHasRequiredComponents();
        processMessageAnnotations();

        mInit = true;

        sendMessage("Initialize");
    }

    public final void destroy()
    {
        if (!mValid)
            return;

        sendMessage("OnDestroyed");

        mEntity.removeComponent(this);

        mValid = false;
    }

    @MessageHook("OnComponentRemoved")
    private void ensureHasRequiredComponents()
    {
        for (RequiresComponent req : this.getClass().getAnnotationsByType(RequiresComponent.class))
        {
            if (!mEntity.hasComponent(req.value()))
            {
                destroy();

                throw new RuntimeException("Entity is missing required component");
            }
        }
    }


    public final Entity getEntity()
    {
        return mEntity;
    }

    public final boolean isValid() // whether this component is still attached to an entity
    {
        return mValid;
    }
}

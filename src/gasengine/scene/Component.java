package gasengine.scene;

import gasengine.messages.MessageHandler;


public abstract class Component extends MessageHandler
{
    public static class Message
    {
        public static final String INITIALIZE = "Initialize";
        public static final String DESTROYED = "OnDestroyed";
    }


    private boolean mInit;

    private Entity mEntity;
    private boolean mValid;

    final void initialize(Entity ent) // package visible only (called by the entity when added)
    {
        if (mInit)
            throw new RuntimeException("Tried to re-initialize component");

        processMessageAnnotations();

        mInit = true;

        mEntity = ent;
        mValid = true;

        sendMessage(Message.INITIALIZE);
    }

    public final void destroy()
    {
        if (!mValid)
            return;

        sendMessage(Message.DESTROYED);

        mEntity.removeComponent(this);

        mValid = false;
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

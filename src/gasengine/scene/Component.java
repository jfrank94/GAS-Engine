package gasengine.scene;

import gasengine.messages.MessageReceiver;


public abstract class Component extends MessageReceiver
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

        handleMessage(Message.INITIALIZE);
    }

    final void init_copy(Entity ent)
    {
        mEntity = ent;
    }

    public final void destroy()
    {
        if (!mValid)
            return;

        handleMessage(Message.DESTROYED);

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

    public void sendMessage(String name, Object data)
    {
        mEntity.sendMessage(name, data);
    }

    public void sendMessage(String name)
    {
        sendMessage(name, null);
    }
}

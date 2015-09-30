package gasengine.messages;


public interface MessageReceiver
{
    void sendMessage(String name, Object data);

    default void sendMessage(String name)
    {
        sendMessage(name, null);
    }
}

package gasengine.messages;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class MessageHandler implements MessageReceiver
{
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface HandlesMessage
    {
        String value() default "";
    }


    private boolean mProcessed = false;

    private HashMap<String, List<Method>> mHandlers = new HashMap<>();

    public MessageHandler()
    {
        processMessageAnnotations();
    }

    protected void processMessageAnnotations()
    {
        if (mProcessed)
            return;

        Method[] methods = this.getClass().getMethods();
        for (Method method : methods)
        {
            HandlesMessage annotation = method.getAnnotation(HandlesMessage.class);
            if (annotation == null)
                continue;

            if (method.getParameterCount() > 1)
                throw new RuntimeException("Message handlers can only have up to one parameter");

            String message = annotation.value();

            if (message.isEmpty())
                message = method.getName();

            if (!mHandlers.containsKey(message))
                mHandlers.put(message, new ArrayList<>());

            mHandlers.get(message).add(method);
        }

        mProcessed = true;
    }

    @Override
    public void sendMessage(String name, Object data)
    {
        if (!mHandlers.containsKey(name))
            return;

        for (Method method : mHandlers.get(name))
        {
            try
            {
                if (method.getParameterCount() == 0)
                    method.invoke(this);
                else
                    method.invoke(this, data);
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Failed to invoke message handler: " + ex);
            }
        }
    }
}

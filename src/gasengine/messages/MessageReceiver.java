package gasengine.messages;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class MessageReceiver
{
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MessageHandler
    {
        String value() default "";
    }


    private boolean mProcessed = false;

    HashMap<String, List<Method>> mHandlers = new HashMap<>();

    public MessageReceiver()
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
            MessageHandler annotation = method.getAnnotation(MessageHandler.class);
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

    public void handleMessage(String name, Object data)
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

    public void handleMessage(String name)
    {
        handleMessage(name, null);
    }
}

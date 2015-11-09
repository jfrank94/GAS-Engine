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
    public @interface MessageHook
    {
        String value() default "";
    }

    public interface Hook
    {
        void invoke(Object data);
    }


    private boolean mProcessed = false;

    private HashMap<String, List<Hook>> mHooks = new HashMap<>();

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
            MessageHook annotation = method.getAnnotation(MessageHook.class);
            if (annotation == null)
                continue;

            if (method.getParameterCount() > 1)
                throw new RuntimeException("Message handlers can only have up to one parameter");

            String message = annotation.value();

            if (message.isEmpty())
                message = method.getName();

            addHook(message, data -> {
                try
                {
                    boolean access = method.isAccessible();

                    method.setAccessible(true); // override normal class/method visibility

                    if (method.getParameterCount() == 0)
                        method.invoke(MessageHandler.this);
                    else
                        method.invoke(MessageHandler.this, data);

                    method.setAccessible(access); // restore old state
                }
                catch (Exception ex)
                {
                    throw new RuntimeException("Failed to invoke message hook: " + ex);
                }
            });
        }

        mProcessed = true;
    }

    public void addHook(String name, Hook callback)
    {
        if (!mHooks.containsKey(name))
            mHooks.put(name, new ArrayList<>());

        mHooks.get(name).add(callback);
    }


    @Override
    public void sendMessage(String name, Object data)
    {
        if (!mHooks.containsKey(name))
            return;

        mHooks
            .get(name)
            .forEach(hook -> hook.invoke(data));
    }
}

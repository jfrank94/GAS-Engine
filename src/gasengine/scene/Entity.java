package gasengine.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public final class Entity
{
    public static class Message
    {
        public static final String COMPONENT_ADDED = "OnComponentAdded";
        public static final String COMPONENT_REMOVED = "OnComponentRemoved";
    }


    private String mName;
    private List<String> mTags = new ArrayList<>();

    private Scene mScene;
    private long mId;
    private boolean mValid;

    private List<Component> mComponents = new ArrayList<>();

    private Vector3f mPosition = new Vector3f();
    private Quaternionf mRotation = new Quaternionf();
    private Matrix4f mTransform = new Matrix4f();

    public Entity(Scene scene, long id)
    {
        mScene = scene;
        mId = id;
        mValid = true;
    }

    public void destroy()
    {
        mComponents.forEach(cmp -> {
            if (cmp.isValid())
                cmp.destroy();
        });
        mComponents.clear();

        mScene.removeEntity(this);

        mValid = false;
    }


    private void rebuildTransform()
    {
        mTransform
            .identity()
            .translate(mPosition)
            .rotate(mRotation);
    }

    public void setPosition(Vector3f pos)
    {
        mPosition.set(pos);

        rebuildTransform();
    }

    public void setPosition(float x, float y, float z)
    {
        mPosition.set(x, y, z);

        rebuildTransform();
    }

    public void getPosition(Vector3f pos)
    {
        pos.set(mPosition);
    }


    public void setRotation(Quaternionf rot)
    {
        mRotation.set(rot);

        rebuildTransform();
    }

    public void getRotation(Quaternionf rot)
    {
        rot.set(mRotation);
    }


    public void getWorldPosition(Vector3f localPos)
    {
        mTransform.transform(localPos);
    }

    public void getLocalPosition(Vector3f worldPos)
    {
        Matrix4f inv = new Matrix4f();

        mTransform.invert(inv);

        inv.transform(worldPos);
    }


    public void setName(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public void addTag(String tag)
    {
        if (!mTags.contains(tag))
            mTags.add(tag);
    }

    public void removeTag(String tag)
    {
        mTags.remove(tag);
    }

    public List<String> getTags()
    {
        return Collections.unmodifiableList(mTags);
    }

    public boolean hasTag(String tag)
    {
        return mTags.contains(tag);
    }

    public long getId()
    {
        return mId;
    }

    public Scene getScene()
    {
        return mScene;
    }

    public boolean isValid() // whether the entity is currently in the scene
    {
        return mValid;
    }


    public <T extends Component> T addComponent(T cmp)
    {
        mComponents.add(cmp);

        cmp.initialize(this);

        sendMessage(Message.COMPONENT_ADDED, cmp);

        return cmp;
    }

    public boolean hasComponent(Class<? extends Component> clazz)
    {
        return mComponents
            .stream()
            .anyMatch(clazz::isInstance);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> clazz)
    {
        return mComponents
            .stream()
            .filter(clazz::isInstance)
            .findFirst()
            .map(cmp -> (T) cmp)
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> getComponents(Class<T> clazz)
    {
        return mComponents
            .stream()
            .filter(clazz::isInstance)
            .map(cmp -> (T) cmp)
            .collect(Collectors.toList());
    }

    void removeComponent(Component cmp) // package visible only (users call cmp.destroy() instead)
    {
        sendMessage(Message.COMPONENT_REMOVED, cmp);

        mComponents.remove(cmp);
    }


    public void sendMessage(String name, Object data)
    {
        mComponents.forEach(cmp -> cmp.handleMessage(name, data));
    }

    public void sendMessage(String name)
    {
        sendMessage(name, null);
    }
}

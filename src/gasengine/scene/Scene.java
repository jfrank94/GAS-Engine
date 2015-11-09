package gasengine.scene;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Scene
{
    private long mNextEntId = 0;
    private HashMap<Long, Entity> mEntities = new HashMap<>();

    public Entity addEntity()
    {
        Entity ent = new Entity(mNextEntId);

        mEntities.put(mNextEntId, ent);
        mNextEntId++;

        return ent;
    }

    void removeEntity(Entity ent) // package visible only (users call ent.destroy() instead)
    {
        mEntities.remove(ent.getId());
    }

    public Collection<Entity> getEntities()
    {
        return Collections.unmodifiableCollection(mEntities.values());
    }

    public List<Entity> getEntities(Predicate<Entity> predicate)
    {
        return mEntities.values()
            .stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }

    public List<Entity> getEntitiesWithComponent(Class<? extends Component> clazz)
    {
        return getEntities(ent -> ent.hasComponent(clazz));
    }

    public List<Entity> findEntitiesByTag(String tag)
    {
        return mEntities.values()
            .stream()
            .filter(ent -> ent.hasTag(tag))
            .collect(Collectors.toList());
    }

    public Entity getEntityById(long id)
    {
        return mEntities.get(id);
    }


    public void broadcastMessage(String name, Object data)
    {
        mEntities.values().forEach(ent -> ent.sendMessage(name, data));
    }

    public void broadcastMessage(String name)
    {
        broadcastMessage(name, null);
    }

    public void broadcastMessageTo(String name, Object data, Predicate<Entity> predicate)
    {
        mEntities.values()
            .stream()
            .filter(predicate)
            .forEach(ent -> ent.sendMessage(name, data));
    }

    public void broadcastMessageTo(String name, Predicate<Entity> predicate)
    {
        broadcastMessageTo(name, null, predicate);
    }
}

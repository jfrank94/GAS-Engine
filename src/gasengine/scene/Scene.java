package gasengine.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Scene
{
    private long mNextEntId = 0;
    private List<Entity> mEntities = new ArrayList<>();

    public Entity addEntity()
    {
        Entity ent = new Entity(this, mNextEntId);

        mEntities.add(ent);
        mNextEntId++;

        return ent;
    }

    void removeEntity(Entity ent) // package visible only (users call ent.destroy() instead)
    {
        mEntities.remove(ent);
    }

    public List<Entity> getEntities()
    {
        return Collections.unmodifiableList(mEntities);
    }

    public Entity getEntityById(long id)
    {
        return mEntities
            .stream()
            .filter(ent -> ent.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<Entity> findEntitiesByTag(String tag)
    {
        return mEntities
            .stream()
            .filter(ent -> ent.hasTag(tag))
            .collect(Collectors.toList());
    }
}

package gasengine.collections;

import java.util.HashMap;


public class SimpleHashMap extends HashMap<String, Object>
{
    public String getString(String key, String def)
    {
        Object val = get(key);

        if (val instanceof String)
            return (String)val;

        return def;
    }

    public String getString(String key)
    {
        return getString(key, null);
    }


    public Integer getInteger(String key, Integer def)
    {
        Object val = get(key);

        if (val instanceof Integer)
            return (Integer)val;

        return def;
    }

    public Integer getInteger(String key)
    {
        return getInteger(key, null);
    }


    public Float getFloat(String key, Float def)
    {
        Object val = get(key);

        if (val instanceof Float)
            return (Float)val;

        return def;
    }

    public Float getFloat(String key)
    {
        return getFloat(key, null);
    }


    public Double getDouble(String key, Double def)
    {
        Object val = get(key);

        if (val instanceof Double)
            return (Double)val;

        return def;
    }

    public Double getDouble(String key)
    {
        return getDouble(key, null);
    }


    public SimpleHashMap getSimpleMap(String key, SimpleHashMap def)
    {
        Object val = get(key);

        if (val instanceof SimpleHashMap)
            return (SimpleHashMap)val;

        return def;
    }

    public SimpleHashMap getSimpleMap(String key)
    {
        return getSimpleMap(key, null);
    }


    public SimpleHashMap set(String key, Object val)
    {
        put(key, val);

        return this;
    }
}

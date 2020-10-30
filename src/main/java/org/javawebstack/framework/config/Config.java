package org.javawebstack.framework.config;

import com.google.gson.JsonElement;
import org.javawebstack.framework.util.JsonHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Config {

    private final Map<String, String> config = new HashMap<>();

    public void add(String path, Map<String, String> data){
        String prefix = path != null && path.length() > 0 ? path + "." : "";
        data.forEach((key, value) -> set(prefix+key.toLowerCase(Locale.ROOT), value));
    }

    public void add(Map<String, String> data){
        add(null, data);
    }

    public void add(String path, JsonElement json){
        add(path, JsonHelper.toKeyValueMap(json));
    }

    public void add(JsonElement json){
        add(null, json);
    }

    public void set(String key, String value){
        config.put(key, value);
    }

    public String get(String key, String defaultValue){
        if(!has(key))
            return defaultValue;
        return config.get(key);
    }

    public String get(String key){
        return get(key, null);
    }

    public int getInt(String key, int defaultValue){
        String value = get(key);
        if(value == null)
            return defaultValue;
        return Integer.parseInt(value);
    }

    public boolean isEnabled(String key, boolean defaultValue){
        String value = get(key);
        if(value == null)
            return defaultValue;
        if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("yes"))
            return true;
        return false;
    }

    public boolean isEnabled(String key){
        return isEnabled(key, false);
    }

    public boolean has(String key){
        return config.containsKey(key);
    }

}

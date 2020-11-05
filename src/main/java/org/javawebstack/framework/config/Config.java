package org.javawebstack.framework.config;

import com.google.gson.JsonElement;
import org.javawebstack.framework.util.IO;
import org.javawebstack.framework.util.Json;
import org.javawebstack.orm.wrapper.MySQL;
import org.javawebstack.orm.wrapper.SQLite;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {

    private final Map<String, String> config = new HashMap<>();
    private final List<Map<String, String>> envKeyMappings = new ArrayList<>();

    public Config(){
        addEnvKeyMapping(new HashMap<String, String>(){{
            put("DATABASE_DRIVER", "database.driver");
            put("DATABASE_FILE", "database.file");
            put("DATABASE_HOST", "database.host");
            put("DATABASE_PORT", "database.port");
            put("DATABASE_USER", "database.user");
            put("DATABASE_PASSWORD", "database.password");
            put("HTTP_SERVER_PORT", "http.server.port");
            put("HTTP_SERVER_CORS", "http.server.cors");
            put("HTTP_SERVER_JSON", "http.server.json");
        }});
    }

    public void addEnvKeyMapping(Map<String, String> mapping){
        envKeyMappings.add(mapping);
    }

    public void set(String path, String key, String value){
        String prefix = path != null && path.length() > 0 ? path + "." : "";
        set(prefix+key.toLowerCase(Locale.ROOT), value);
    }

    public void add(String path, Map<String, String> data){
        data.forEach((key, value) -> set(path, key, value));
    }

    public void add(Map<String, String> data){
        add(null, data);
    }

    public void add(String path, Properties data){
        data.forEach((key, value) -> set(path, key.toString(), value.toString()));
    }

    public void add(Properties data){
        add(null, data);
    }

    public void add(String path, JsonElement json){
        add(path, Json.toKeyValueMap(json));
    }

    public void add(JsonElement json){
        add(null, json);
    }

    public void addJsonFile(String path, File file){
        try {
            add(path, IO.readJsonFile(file));
        } catch (IOException ignored) {}
    }

    public void addJsonFile(String path, String fileName){
        addJsonFile(path, new File(fileName));
    }

    public void addJsonFile(File file){
        try {
            add(IO.readJsonFile(file));
        } catch (IOException ignored) {}
    }

    public void addJsonFile(String fileName){
        addJsonFile(new File(fileName));
    }

    public void addJsonResource(String path, ClassLoader classLoader, String resourcePath){
        try {
            add(path, IO.readJsonResource(classLoader, resourcePath));
        } catch (IOException ignored) {}
    }

    public void addJsonResource(String path, String resourcePath){
        try {
            add(path, IO.readJsonResource(resourcePath));
        } catch (IOException ignored) {}
    }

    public void addJsonResource(ClassLoader classLoader, String resourcePath){
        addJsonResource(null, classLoader, resourcePath);
    }

    public void addJsonResource(String resourcePath){
        addJsonResource(ClassLoader.getSystemClassLoader(), resourcePath);
    }

    public void addPropertyFile(String path, File file){
        try {
            add(path, IO.readPropertyFile(file));
        } catch (IOException ignored) {}
    }

    public void addPropertyFile(String path, String fileName){
        addPropertyFile(path, new File(fileName));
    }

    public void addPropertyFile(File file){
        try {
            add(IO.readPropertyFile(file));
        } catch (IOException ignored) {}
    }

    public void addPropertyFile(String fileName){
        addPropertyFile(new File(fileName));
    }

    public void addPropertyResource(String path, ClassLoader classLoader, String resourcePath){
        try {
            add(path, IO.readPropertyResource(classLoader, resourcePath));
        } catch (IOException ignored) {}
    }

    public void addPropertyResource(String path, String resourcePath){
        try {
            add(path, IO.readPropertyResource(resourcePath));
        } catch (IOException ignored) {}
    }

    public void addPropertyResource(ClassLoader classLoader, String resourcePath){
        addPropertyResource(null, classLoader, resourcePath);
    }

    public void addPropertyResource(String resourcePath){
        addPropertyResource(ClassLoader.getSystemClassLoader(), resourcePath);
    }

    public void addEnvFile(String path, File file){
        try {
            IO.readPropertyFile(file).forEach((key, value) -> set(path, transformEnvKey(key.toString()), value.toString()));
        } catch (IOException ignored) {}
        System.getenv().entrySet().forEach(e -> set(path, transformEnvKey(e.getKey()), e.getValue()));
    }

    public void addEnvFile(File file){
        addEnvFile(null, file);
    }

    public void addEnvFile(String path, String fileName){
        addEnvFile(path, new File(fileName));
    }

    public void addEnvFile(String fileName){
        addEnvFile(new File(fileName));
    }

    private String transformEnvKey(String key){
        for(Map<String, String> map : envKeyMappings){
            if(map.containsKey(key))
                return map.get(key);
        }
        return "env." + key.toLowerCase(Locale.ROOT);
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

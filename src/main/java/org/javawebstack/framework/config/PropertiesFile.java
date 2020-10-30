package org.javawebstack.framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class PropertiesFile extends HashMap<String, String> {

    private final File file;

    public PropertiesFile(File file){
        this.file = file;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
            properties.forEach((key, value) -> put(key.toString(), value.toString()));
        } catch (IOException ignored) {}
    }

    public PropertiesFile(ClassLoader classLoader, String resourcePath){
        this.file = null;
        Properties properties = new Properties();
        try {
            properties.load(classLoader.getResourceAsStream(resourcePath));
            properties.forEach((key, value) -> put(key.toString(), value.toString()));
        } catch (IOException ignored) {}
    }

    public PropertiesFile(String resourcePath){
        this(ClassLoader.getSystemClassLoader(), resourcePath);
    }

    public void save(){
        if(file != null)
            save(file);
    }

    public void save(File file){
        try {
            Properties properties = new Properties();
            properties.putAll(this);
            properties.store(new FileOutputStream(file), null);
        } catch (IOException ignored) {}
    }

}

package org.javawebstack.framework.config;

import java.io.File;

public class EnvFile extends PropertiesFile {

    public EnvFile(File file){
        super(file);
        System.getenv().entrySet().forEach(e -> {
            if(!containsKey(e.getKey())){
                put(e.getKey(), e.getValue());
            }
        });
    }

}

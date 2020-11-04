package org.javawebstack.framework.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.javawebstack.framework.util.JsonHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class JsonConfigFile extends HashMap<String, String> {

    public JsonConfigFile(File file) {
        try {
            JsonHelper.toKeyValueMap(new Gson().fromJson(new FileReader(file), JsonElement.class));
        } catch (FileNotFoundException ignored) { }
    }

    public JsonConfigFile(String file){
        this(new File(file));
    }
}

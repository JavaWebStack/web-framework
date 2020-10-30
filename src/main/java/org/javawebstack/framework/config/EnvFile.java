package org.javawebstack.framework.config;

import org.javawebstack.framework.util.IOHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class EnvFile extends HashMap<String, String> {

    private final List<String> order = new ArrayList<>();
    private final File file;

    public EnvFile(File file){
        this.file = file;
        if(file != null && file.exists()) {
            try {
                String[] lines = new String(IOHelper.readFile(file), StandardCharsets.UTF_8).replace("\r", "").split("\n");
                for (String line : lines) {
                    if (line.length() == 0 || line.startsWith("#") || line.startsWith(" ") || line.startsWith("\t") || !line.contains("="))
                        continue;
                    String[] spl = line.split("=", 2);
                    put(spl[0], spl[1]);
                }
            } catch (IOException ignored) {}
        }
        System.getenv().entrySet().forEach(e -> {
            if(!containsKey(e.getKey())){
                put(e.getKey(), e.getValue());
            }
        });
    }

    public String put(String key, String value) {
        if(!order.contains(key))
            order.add(key);
        return super.put(key, value);
    }

    public String remove(Object key){
        order.remove(key);
        return super.remove(key);
    }

    public void save(){
        if(file != null)
            save(file);
    }

    public void save(File file){
        StringBuilder sb = new StringBuilder();
        for(String k : order){
            sb.append(k);
            sb.append('=');
            sb.append(get(k));
            sb.append('\n');
        }
        try {
            IOHelper.writeFile(file, sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {}
    }

}

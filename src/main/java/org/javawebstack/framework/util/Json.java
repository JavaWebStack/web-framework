package org.javawebstack.framework.util;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class Json {

    public static Map<String, String> toKeyValueMap(JsonElement element){
        if(element == null || element.isJsonNull())
            return new HashMap<>();
        Map<String, String> map = new HashMap<>();
        if(element.isJsonPrimitive()){
            map.put("", element.getAsString());
        }
        if(element.isJsonObject()){
            for(String key : element.getAsJsonObject().keySet()){
                Map<String, String> valueMap = toKeyValueMap(element.getAsJsonObject().get(key));
                for(String innerKey : valueMap.keySet())
                    map.put(key + (innerKey.length() > 0 ? "." + innerKey : ""), valueMap.get(innerKey));
            }
        }
        if(element.isJsonArray()){
            for(int i=0; i < element.getAsJsonArray().size(); i++){
                Map<String, String> valueMap = toKeyValueMap(element.getAsJsonArray().get(i));
                for(String innerKey : valueMap.keySet())
                    map.put(i + (innerKey.length() > 0 ? "." + innerKey : ""), valueMap.get(innerKey));
            }
        }
        return map;
    }

}

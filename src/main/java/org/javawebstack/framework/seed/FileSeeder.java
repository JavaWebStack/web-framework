package org.javawebstack.framework.seed;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.util.IO;
import org.javawebstack.graph.*;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileSeeder implements Seeder {

    private static final GraphMapper mapper = new GraphMapper().setNamingPolicy(NamingPolicy.SNAKE_CASE);

    private final WebApplication application;
    private final GraphArray array;

    public FileSeeder(WebApplication application, GraphArray array){
        this.application = application;
        this.array = array;
    }

    public FileSeeder(WebApplication application, File file){
        this(application, read(file));
    }

    public FileSeeder(WebApplication application, String resource){
        this(application, ClassLoader.getSystemClassLoader(), resource);
    }

    public FileSeeder(WebApplication application, ClassLoader loader, String resource){
        this(application, read(loader, resource));
    }

    public void seed(){
        Map<String, Class<? extends Model>> modelMap = new HashMap<>();
        ORM.getModels().forEach(m -> modelMap.put(m.getSimpleName(), m));

        for(GraphElement element : array){
            if(element.isObject()){
                GraphObject model = element.object();
                if(!model.has("type") || !model.get("type").isString() || !model.has("data"))
                    continue;
                Class<? extends Model> modelType = modelMap.get(model.get("type").string());
                if(modelType == null)
                    continue;
                if(model.get("data").isObject()){
                    seed(modelType, model.get("data").object());
                } else if(model.get("data").isArray()){
                    for(GraphElement e : model.get("data").array()){
                        if(e.isObject())
                            seed(modelType, e.object());
                    }
                }
            }
            if(element.isString()){
                Seeder seeder = application.getSeeder(element.string());
                if(seeder != null)
                    seeder.seed();
            }
        }
    }

    private <T extends Model> void seed(Class<T> type, GraphObject object){
        T t = mapper.fromGraph(object, type);
        if(t != null)
            t.save();
    }

    private static GraphArray read(File file){
        try {
            String[] spl = file.getName().split("\\.");
            switch (spl[spl.length-1]){
                case "yaml":
                case "yml":
                    return GraphElement.fromYaml(IO.readTextFile(file)).array();
                default:
                    return GraphArray.fromJson(IO.readJsonFile(file).getAsJsonArray());
            }
        } catch (IOException e) {
            return new GraphArray();
        }
    }

    private static GraphArray read(ClassLoader loader, String fileName){
        try {
            String[] spl = fileName.split("\\.");
            switch (spl[spl.length-1]){
                case "yaml":
                case "yml":
                    return GraphElement.fromYaml(IO.readTextResource(loader, fileName)).array();
                default:
                    return GraphArray.fromJson(IO.readJsonResource(loader, fileName).getAsJsonArray());
            }
        } catch (IOException e) {
            return new GraphArray();
        }
    }

    public static void create(File file, String[] merge, Class<? extends Model>... models){
        GraphArray root = new GraphArray();
        for(String m : merge)
            root.add(m);
        for(Class<? extends Model> model : models){
            GraphArray data = new GraphArray();
            Repo.get(model).all().forEach(e -> data.add(mapper.toGraph(e)));
            root.add(new GraphObject()
                    .set("type", model.getSimpleName())
                    .set("data", data)
            );
        }

        try {
            String[] spl = file.getName().split("\\.");
            switch (spl[spl.length-1]){
                case "yaml":
                case "yml":
                    IO.writeFile(file, root.toYaml(true));
                    break;
                default:
                    IO.writeFile(file, root.toJsonString(true));
                    break;
            }
        }catch (IOException ignored){}
    }

}

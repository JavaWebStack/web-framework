package org.javawebstack.framework.command;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.seed.FileSeeder;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateSeedCommand implements Command {

    public CommandResult execute(CommandSystem commandSystem, List<String> args, Map<String, List<String>> params) {
        if(args.size() < 1)
            return CommandResult.syntax("generate seed <name> [...models]");
        List<Class<? extends Model>> models = new ArrayList<>();
        Map<String, Class<? extends Model>> modelMap = new HashMap<>();
        ORM.getModels().forEach(m -> modelMap.put(m.getSimpleName(), m));
        for(int i=1; i<args.size(); i++){
            if(modelMap.containsKey(args.get(i)))
                models.add(modelMap.get(args.get(i)));
        }
        File seedsFolder = new File("src/main/resources/seeds");
        if(params.containsKey("module"))
            seedsFolder = new File(params.get("module").get(0)+"/src/main/resources/seeds");
        if(!seedsFolder.exists() && !seedsFolder.mkdirs())
            return CommandResult.error("Could not create seeds folder!");
        String name = args.get(0);
        String[] merge = params.containsKey("merge") ? params.get("merge").toArray(new String[0]) : new String[0];
        File seedFile = new File(seedsFolder, name + ".json");
        FileSeeder.create(seedFile, merge, models.toArray(new Class[0]));
        System.out.println("Seed generated!");
        return CommandResult.success();
    }

}

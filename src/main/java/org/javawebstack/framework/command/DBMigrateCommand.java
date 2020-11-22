package org.javawebstack.framework.command;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.orm.ORM;

import java.util.List;
import java.util.Map;

public class DBMigrateCommand implements Command {

    public CommandResult execute(CommandSystem commandSystem, List<String> args, Map<String, List<String>> params) {
        if(params.containsKey("a")){
            if(params.containsKey("d")){
                ORM.autoDrop();
                System.out.println("Dropped all tables!");
                return CommandResult.success();
            }
            ORM.autoMigrate(params.containsKey("f"));
            System.out.println("Auto-Migration successful!");
            return CommandResult.success();
        }
        return CommandResult.error("Manual migration is not yet fully implemented");
    }

}

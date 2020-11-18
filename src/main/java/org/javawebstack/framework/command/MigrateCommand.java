package org.javawebstack.framework.command;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.orm.ORM;

import java.util.List;
import java.util.Map;

public class MigrateCommand implements Command {

    public CommandResult execute(CommandSystem commandSystem, List<String> args, Map<String, List<String>> params) {
        if(params.containsKey("f")){
            // DROP TABLES
        }
        if(params.containsKey("a")){
            ORM.autoMigrate();
            System.out.println("Auto-Migration successful!");
            return CommandResult.success();
        }
        return CommandResult.error("Manual migration is not yet fully implemented");
    }

}

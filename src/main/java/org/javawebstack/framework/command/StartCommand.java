package org.javawebstack.framework.command;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.WebApplication;

import java.util.List;
import java.util.Map;

public class StartCommand implements Command {
    private final WebApplication application;
    public StartCommand(WebApplication application){
        this.application = application;
    }
    public CommandResult execute(CommandSystem commandSystem, List<String> list, Map<String, List<String>> map) {
        try {
            application.start();
        }catch (Throwable throwable){
            return CommandResult.error(throwable);
        }
        return CommandResult.success();
    }
}

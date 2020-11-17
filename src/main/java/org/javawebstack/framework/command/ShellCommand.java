package org.javawebstack.framework.command;

import bsh.EvalError;
import bsh.Interpreter;
import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.WebApplication;
import org.javawebstack.orm.ORM;

import java.util.List;
import java.util.Map;

public class ShellCommand implements Command {
    private final WebApplication application;
    public ShellCommand(WebApplication application){
        this.application = application;
    }
    public CommandResult execute(CommandSystem system, List<String> list, Map<String, List<String>> map) {
        Interpreter interpreter = new Interpreter();
        try {
            interpreter.set("app", application);
            interpreter.getNameSpace().importClass("org.javawebstack.orm.Repo");
            ORM.getModels().forEach(m -> interpreter.getNameSpace().importClass(m.getName()));
            interpreter.run();
        } catch (EvalError error) {
            return CommandResult.error(error);
        }
        return CommandResult.success();
    }
}

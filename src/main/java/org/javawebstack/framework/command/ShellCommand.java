package org.javawebstack.framework.command;

import bsh.EvalError;
import bsh.Interpreter;
import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.WebApplication;
import org.javawebstack.injector.Inject;
import org.javawebstack.orm.ORM;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class ShellCommand implements Command {
    @Inject
    private WebApplication application;

    public CommandResult execute(CommandSystem system, List<String> list, Map<String, List<String>> map) {
        InputStreamReader reader = new InputStreamReader(System.in);
        Interpreter interpreter = new Interpreter();
        try {
            interpreter.set("app", application);
            interpreter.getNameSpace().importClass("org.javawebstack.orm.Repo");
            ORM.getModels().forEach(m -> interpreter.getNameSpace().importClass(m.getName()));
            System.out.println("App Shell");
            while (true) {
                try {
                    interpreter.eval(reader);
                } catch (EvalError error) {
                    System.err.println(error.getMessage());
                }
            }
        } catch (EvalError error) {
            return CommandResult.error(error);
        }
    }
}

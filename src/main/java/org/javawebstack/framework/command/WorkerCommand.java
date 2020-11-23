package org.javawebstack.framework.command;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.WebApplication;
import org.javawebstack.injector.Inject;

import java.util.List;
import java.util.Map;

public class WorkerCommand implements Command {
    @Inject
    private WebApplication application;
    public CommandResult execute(CommandSystem commandSystem, List<String> args, Map<String, List<String>> params) {
        if(args.size() == 0)
            return CommandResult.syntax("worker <...queues>");
        application.startWorker(args.toArray(new String[0]));
        return CommandResult.success();
    }
}

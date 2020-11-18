package org.javawebstack.framework.command;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.util.Crypt;
import org.javawebstack.injector.Inject;

import java.util.List;
import java.util.Map;

public class CryptHashCommand implements Command {

    @Inject
    Crypt crypt;

    public CommandResult execute(CommandSystem commandSystem, List<String> args, Map<String, List<String>> params) {
        System.out.println(crypt.hash(args.size() > 0 ? args.get(0) : ""));
        return CommandResult.success();
    }

}

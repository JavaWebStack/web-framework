package org.javawebstack.framework.commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Collections;
import java.util.concurrent.Callable;

@Command(mixinStandardHelpOptions = true)
public class HelpCommand implements Callable<Integer> {

    private CommandLine commandLine;

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public HelpCommand(){
    }

    private void printCommands(CommandLine.Help help, int indent){
        System.out.println(String.join("",Collections.nCopies(indent, " ")) +help.commandList());
        help.subcommands().forEach((n, h) -> printCommands(h, indent+4));
    }

    public Integer call() {
        printCommands(commandLine.getHelp(), 0);
        return null;
    }
}

package org.javawebstack.framework.commands;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.orm.ORM;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "migrate:auto", description = "Migrate your Models automatically")
public class AutoMigrateCommand implements Callable<Integer> {
    private WebApplication webApplication;

    @CommandLine.Option(names = {"--fresh", "-f"}, description = "Auto Migrate fresh")
    public boolean fresh = false;

    public AutoMigrateCommand(WebApplication webApplication) {
        this.webApplication = webApplication;
    }

    public Integer call() {
        ORM.autoMigrate(fresh);
        return 0;
    }
}

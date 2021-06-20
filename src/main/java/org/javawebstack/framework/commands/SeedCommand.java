package org.javawebstack.framework.commands;

import org.javawebstack.framework.WebApplication;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "seed", description = "Seeding command")
public class SeedCommand implements Callable<Integer> {
    private WebApplication webApplication;

    @CommandLine.Parameters(description = "The seeders which are going to be seeded separated by a comma or * for all")
    private String seeders;

    public SeedCommand(WebApplication webApplication) {
        this.webApplication = webApplication;
    }

    public Integer call() {
        for (String s : seeders.split(",")) {
            if (s.equals("*")) {
                webApplication.getSeeders().forEach((s1, seeder) -> {
                    seeder.seed();
                });
            } else {
                webApplication.getSeeder(s).seed();
            }
        }
        return 0;
    }
}

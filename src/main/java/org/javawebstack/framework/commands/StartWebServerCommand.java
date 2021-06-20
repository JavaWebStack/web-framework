package org.javawebstack.framework.commands;

import org.javawebstack.framework.WebApplication;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "start", description = "Starts the WebServer")
public class StartWebServerCommand implements Callable<Integer> {
    private WebApplication webApplication;


    public StartWebServerCommand(WebApplication webApplication) {
        this.webApplication = webApplication;
    }

    public Integer call() {
        webApplication.start();
        return 0;
    }
}

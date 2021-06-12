package org.javawebstack.framework.commands;

import org.javawebstack.framework.WebApplication;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "start", description = "Starts the WebServer")
public class StartWebServerCommand implements Callable<Integer> {
    private WebApplication webApplication;

    /*@Option(names = {"--port", "-p"}, description = "Port")
    private int port;*/

    public StartWebServerCommand(WebApplication webApplication){
        this.webApplication = webApplication;
    }

    @Command(name = "test")
    public void asd(){

    }

    public Integer call() {
        webApplication.start();
        return 0;
    }
}

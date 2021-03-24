package org.javawebstack.framework.testing;

import org.javawebstack.command.CommandResult;
import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.config.Config;
import org.javawebstack.httpserver.test.HTTPTest;
import org.javawebstack.injector.Injector;

public abstract class WebFrameworkTest extends HTTPTest {

    private WebApplication webApplication;

    public WebFrameworkTest(WebApplication webApplication){
        super(webApplication.getServer());
        webApplication.getInjector().inject(this);

        this.webApplication = webApplication;
    }

    public WebApplication getApplication() {
        return webApplication;
    }

    public Injector getInjector(){
        return webApplication.getInjector();
    }

    public void seed(String name){
        webApplication.getSeeder(name).seed();
    }

    public CommandResult runCommand(String... input){
        return webApplication.getCommandSystem().eval(input);
    }

    public Config getConfig(){
        return webApplication.getConfig();
    }
}

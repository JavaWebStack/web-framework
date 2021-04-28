package org.javawebstack.framework.testing;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.config.Config;
import org.javawebstack.httpserver.test.HTTPTest;

public abstract class WebFrameworkTest extends HTTPTest {

    private WebApplication webApplication;

    public WebFrameworkTest(WebApplication webApplication){
        super(webApplication.getServer());

        this.webApplication = webApplication;
    }

    public WebApplication getApplication() {
        return webApplication;
    }

    public void seed(String name){
        webApplication.getSeeder(name).seed();
    }

    public Config getConfig(){
        return webApplication.getConfig();
    }
}

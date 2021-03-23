package org.javawebstack.framework.testing;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.httpserver.test.HTTPTest;
import org.javawebstack.injector.Injector;

public abstract class WebFrameworkTest extends HTTPTest {

    private WebApplication webApplication;

    public WebFrameworkTest(WebApplication webApplication){
        super(webApplication.getServer());
        this.webApplication = webApplication;
    }

    public WebApplication getWebApplication() {
        return webApplication;
    }

    public Injector getInjector(){
        return webApplication.getInjector();
    }

    public <T> T inject(Class<T> clazz){
        return webApplication.getInjector().getInstance(clazz);
    }

    public <T> T inject(Class<T> clazz, String name){
        return webApplication.getInjector().getInstance(clazz, name);
    }
}

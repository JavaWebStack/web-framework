package org.javawebstack.framework.testing;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.httpserver.test.HTTPTest;

public abstract class WebFrameworkTest extends HTTPTest {

    private WebApplication webApplication;

    public WebFrameworkTest(WebApplication webApplication){
        super(webApplication.getServer());
        this.webApplication = webApplication;
    }

    public WebApplication getWebApplication() {
        return webApplication;
    }
}

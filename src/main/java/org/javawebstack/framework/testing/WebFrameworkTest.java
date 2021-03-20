package org.javawebstack.framework.testing;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.httpserver.test.HTTPTest;

public abstract class WebFrameworkTest {

    private HTTPTest httpTest;
    private WebApplication webApplication;

    public WebFrameworkTest(WebApplication webApplication){
        this.webApplication = webApplication;
        httpTest = new HTTPTest(webApplication.getServer()) {};
    }

    public HTTPTest getHttpTest() {
        return httpTest;
    }

    public WebApplication getWebApplication() {
        return webApplication;
    }
}

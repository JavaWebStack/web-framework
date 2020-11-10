package org.javawebstack.framework.module;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.config.Config;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.injector.SimpleInjector;
import org.javawebstack.orm.wrapper.SQL;

public interface Module {

    default void beforeSetupConfig(WebApplication application, Config config){}
    default void beforeSetupInjection(WebApplication application, SimpleInjector injector){}
    default void beforeSetupModels(WebApplication application, SQL sql){}
    default void beforeSetupServer(WebApplication application, HTTPServer server){}
    default void setupConfig(WebApplication application, Config config){}
    default void setupInjection(WebApplication application, SimpleInjector injector){}
    default void setupModels(WebApplication application, SQL sql){}
    default void setupServer(WebApplication application, HTTPServer server){}

}

package org.javawebstack.framework.module;

import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.WebApplication;
import org.javawebstack.framework.config.Config;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.injector.Injector;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.orm.wrapper.SQLDriverFactory;

public interface Module {

    default void beforeSetupConfig(WebApplication application, Config config) {
    }

    default void beforeSetupInjection(WebApplication application, Injector injector) {
    }

    default void beforeSetupModels(WebApplication application, SQL sql) throws ORMConfigurationException {
    }

    default void beforeSetupServer(WebApplication application, HTTPServer server) {
    }

    default void beforeSetupSeeding(WebApplication application) {
    }

    default void setupConfig(WebApplication application, Config config) {
    }

    default void setupInjection(WebApplication application, Injector injector) {
    }

    default void setupDriverFactory(WebApplication application, SQLDriverFactory driverFactory) {
    }

    default void setupModels(WebApplication application, SQL sql) throws ORMConfigurationException {
    }

    default void setupServer(WebApplication application, HTTPServer server) {
    }

    default void setupCommands(WebApplication application, CommandSystem system) {
    }

    default void setupSeeding(WebApplication application) {
    }

}

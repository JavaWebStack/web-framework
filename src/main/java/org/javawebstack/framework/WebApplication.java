package org.javawebstack.framework;

import com.github.javafaker.Faker;
import org.javawebstack.framework.bind.ModelBindParamTransformer;
import org.javawebstack.framework.config.Config;
import org.javawebstack.framework.util.CORSPolicy;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.httpserver.inject.Injector;
import org.javawebstack.httpserver.inject.SimpleInjector;
import org.javawebstack.httpserver.transformer.response.JsonResponseTransformer;
import org.javawebstack.orm.wrapper.MySQL;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.orm.wrapper.SQLite;

public abstract class WebApplication {

    private final HTTPServer server;
    private final SimpleInjector injector;
    private final Faker faker = new Faker();
    private final Config config = new Config();

    public WebApplication(){
        injector = new SimpleInjector();
        injector.setInstance(Injector.class, injector);
        injector.setInstance(SimpleInjector.class, injector);
        injector.setInstance(Faker.class, faker);
        injector.setInstance(Config.class, config);
        injector.setInstanceUnsafe(getClass(), this);
        injector.setInstance(WebApplication.class, this);
        setupConfig(config);
        SQL sql = null;
        if(config.get("database.driver", "none").equalsIgnoreCase("sqlite")){
            sql = new SQLite(config.get("database.file", "db.sqlite"));
            setupModels(sql);
        }
        if(config.get("database.driver", "none").equalsIgnoreCase("mysql")){
            sql = new MySQL(
                    config.get("database.host", "localhost"),
                    config.getInt("database.port", 3306),
                    config.get("database.name", "app"),
                    config.get("database.user", "root"),
                    config.get("database.password", "")
            );
            setupModels(sql);
        }
        setupInjection(injector);
        server = new HTTPServer()
                .port(config.getInt("http.server.port", 80));
        injector.setInstance(HTTPServer.class, server);
        server.injector(injector);
        injector.inject(this);
        server.beforeInterceptor(new CORSPolicy(config.get("http.server.cors", "*")));
        if(config.isEnabled("http.server.json", true))
            server.responseTransformer(new JsonResponseTransformer().ignoreStrings());
        if(sql != null)
            server.routeParamTransformer(new ModelBindParamTransformer());
        setupServer(server);
    }

    public HTTPServer getServer() {
        return server;
    }

    public SimpleInjector getInjector() {
        return injector;
    }

    public Faker getFaker() {
        return faker;
    }

    public Config getConfig() {
        return config;
    }

    public abstract void setupConfig(Config config);
    public abstract void setupInjection(SimpleInjector injector);
    public abstract void setupModels(SQL sql);
    public abstract void setupServer(HTTPServer server);

    public void run(){
        server.start();
        server.join();
    }

}

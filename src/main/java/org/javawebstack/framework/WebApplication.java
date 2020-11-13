package org.javawebstack.framework;

import com.github.javafaker.Faker;
import org.javawebstack.framework.bind.ModelBindParamTransformer;
import org.javawebstack.framework.bind.ModelBindTransformer;
import org.javawebstack.framework.config.Config;
import org.javawebstack.framework.module.Module;
import org.javawebstack.framework.util.CORSPolicy;
import org.javawebstack.framework.util.Crypt;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.httpserver.transformer.response.JsonResponseTransformer;
import org.javawebstack.injector.Injector;
import org.javawebstack.injector.SimpleInjector;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.MySQL;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.orm.wrapper.SQLite;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class WebApplication {

    private Logger logger = Logger.getLogger("WebApp");
    private final SQL sql;
    private final HTTPServer server;
    private final SimpleInjector injector;
    private final Faker faker = new Faker();
    private final Config config = new Config();
    private final Crypt crypt;
    private final List<Module> modules = new ArrayList<>();
    private final ModelBindParamTransformer modelBindParamTransformer = new ModelBindParamTransformer();

    public WebApplication(){
        injector = new SimpleInjector();
        injector.setInstance(Injector.class, injector);
        injector.setInstance(SimpleInjector.class, injector);
        injector.setInstance(Faker.class, faker);
        injector.setInstance(Config.class, config);
        injector.setInstanceUnsafe(getClass(), this);
        injector.setInstance(WebApplication.class, this);
        setupModules();
        modules.forEach(m -> m.beforeSetupConfig(this, config));
        setupConfig(config);

        crypt = new Crypt(config.has("crypt.key") ? config.get("crypt.key") : Crypt.generateKey());
        injector.setInstance(Crypt.class, crypt);

        modules.forEach(m -> m.setupConfig(this, config));
        if(config.get("database.driver", "none").equalsIgnoreCase("sqlite")){
            sql = new SQLite(config.get("database.file", "db.sqlite"));
        }else if(config.get("database.driver", "none").equalsIgnoreCase("mysql")){
            sql = new MySQL(
                    config.get("database.host", "localhost"),
                    config.getInt("database.port", 3306),
                    config.get("database.name", "app"),
                    config.get("database.user", "root"),
                    config.get("database.password", "")
            );
        }else{
            sql = null;
        }
        if(sql != null){
            try {
                for(Module m : modules)
                    m.beforeSetupModels(this, sql);
                setupModels(sql);
                for(Module m : modules)
                    m.setupModels(this, sql);
            }catch (ORMConfigurationException ex){
                ex.printStackTrace();
            }
        }
        modules.forEach(m -> m.beforeSetupInjection(this, injector));
        setupInjection(injector);
        modules.forEach(m -> m.setupInjection(this, injector));
        server = new HTTPServer()
                .port(config.getInt("http.server.port", 80));
        injector.setInstance(HTTPServer.class, server);
        server.injector(injector);
        injector.inject(this);
        server.beforeInterceptor(new CORSPolicy(config.get("http.server.cors", "*")));
        if(config.isEnabled("http.server.json", true))
            server.responseTransformer(new JsonResponseTransformer().ignoreStrings());
        if(sql != null)
            server.routeParamTransformer(modelBindParamTransformer);
        modules.forEach(m -> m.beforeSetupServer(this, server));
        setupServer(server);
        modules.forEach(m -> m.setupServer(this, server));
    }

    public WebApplication addModule(Module module){
        modules.add(module);
        return this;
    }

    public WebApplication setModelBindTransformer(ModelBindTransformer transformer){
        modelBindParamTransformer.setTransformer(transformer);
        return this;
    }

    public Logger getLogger(){
        return logger;
    }

    public void setLogger(Logger logger){
        this.logger = logger;
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

    public Crypt getCrypt(){
        return crypt;
    }

    public abstract void setupModules();
    public abstract void setupConfig(Config config);
    public abstract void setupInjection(SimpleInjector injector);
    public abstract void setupModels(SQL sql) throws ORMConfigurationException;
    public abstract void setupServer(HTTPServer server);

    public void run(){
        server.start();
        server.join();
    }

}

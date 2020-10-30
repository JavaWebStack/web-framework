package org.javawebstack.framework;

import com.github.javafaker.Faker;
import eu.bebendorf.ajorm.wrapper.MySQL;
import eu.bebendorf.ajorm.wrapper.SQL;
import eu.bebendorf.ajorm.wrapper.SQLite;
import eu.bebendorf.ajwf.WebService;
import eu.bebendorf.ajwf.inject.Injector;
import eu.bebendorf.ajwf.inject.SimpleInjector;
import org.javawebstack.framework.config.Config;

public abstract class WebApplication {

    private final SQL sql;
    private final WebService service;
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
        if(config.get("database.driver", "mysql").equalsIgnoreCase("sqlite")){
            sql = new SQLite(config.get("database.file", "db.sqlite"));
        }else{
            sql = new MySQL(
                    config.get("database.host", "localhost"),
                    config.getInt("database.port", 3306),
                    config.get("database.name", "app"),
                    config.get("database.user", "root"),
                    config.get("database.password", "")
            );
        }
        setupModels(sql);
        setupInjection(injector);
        service = new WebService();
        injector.setInstance(WebService.class, service);
        service
                .injector(injector)
                .routeParamTransformer(new ModelBindingParamTransformer());
        setupService(service);
    }

    public WebService getService() {
        return service;
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
    public abstract void setupService(WebService service);

}

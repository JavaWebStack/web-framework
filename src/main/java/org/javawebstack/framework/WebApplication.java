package org.javawebstack.framework;

import com.github.javafaker.Faker;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.framework.bind.ModelBindParamTransformer;
import org.javawebstack.framework.bind.ModelBindTransformer;
import org.javawebstack.framework.config.Config;
import org.javawebstack.framework.module.Module;
import org.javawebstack.framework.seed.AllSeeder;
import org.javawebstack.framework.seed.MergedSeeder;
import org.javawebstack.framework.seed.Seeder;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.orm.wrapper.SQLDriverFactory;
import org.javawebstack.orm.wrapper.SQLDriverNotFoundException;
import org.javawebstack.webutils.*;
import org.javawebstack.webutils.crypt.Crypt;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public abstract class WebApplication {

    private Logger logger = Logger.getLogger("WebApp");
    private SQL sql;
    private final HTTPServer server;
    private final Faker faker = new Faker();
    private final Config config = new Config();
    private final Crypt crypt;
    private final List<Module> modules = new ArrayList<>();
    private final ModelBindParamTransformer modelBindParamTransformer;
    private final Map<String, Seeder> seeders = new HashMap<>();
    private final I18N translation = new I18N();
    private SQLDriverFactory sqlDriverFactory;

    public WebApplication() {

        setupModules();
        modules.forEach(m -> m.beforeSetupConfig(this, config));
        setupConfig(config);

        crypt = new Crypt(config.has("crypt.key") ? config.get("crypt.key") : Crypt.generateKey());

        modules.forEach(m -> m.setupConfig(this, config));
        sqlDriverFactory = new SQLDriverFactory(new HashMap<String, String>() {{
            put("file", config.get("database.file", "sb.sqlite"));
            put("host", config.get("database.host", "localhost"));
            put("port", config.get("database.port", "3306"));
            put("name", config.get("database.name", "app"));
            put("user", config.get("database.user", "root"));
            put("password", config.get("database.password", ""));
        }});
        modules.forEach(m -> m.setupDriverFactory(this, sqlDriverFactory));
        String driverName = config.get("database.driver", "none");
        try {
            sql = sqlDriverFactory.getDriver(driverName);

            for (Module m : modules)
                m.beforeSetupModels(this, sql);
            setupModels(sql);
            for (Module m : modules)
                m.setupModels(this, sql);
        } catch (ORMConfigurationException ex) {
            ex.printStackTrace();
        } catch (SQLDriverNotFoundException e) {
            logger.warning("[SQL] Driver " + driverName + " not found!");
        }
        modelBindParamTransformer = new ModelBindParamTransformer();

        server = new HTTPServer()
                .port(config.getInt("http.server.port", 80));
        server.beforeInterceptor(new CORSPolicy(config.get("http.server.cors", "*")));
        server.beforeInterceptor(new MultipartPolicy(config.get("http.server.tmp", null)));
        if (config.isEnabled("http.server.autoserialization", true))
            server.responseTransformer(new SerializedResponseTransformer().ignoreStrings());
        if (sql != null)
            server.routeParamTransformer(modelBindParamTransformer);
        modules.forEach(m -> m.beforeSetupServer(this, server));
        setupServer(server);
        modules.forEach(m -> m.setupServer(this, server));

        modules.forEach(m -> m.beforeSetupSeeding(this));
        setupSeeding();
        modules.forEach(m -> m.setupSeeding(this));
        addSeeder("all", new AllSeeder());
    }

    public Map<String, Seeder> getSeeders() {
        return seeders;
    }

    public void addTranslation(Locale locale, ClassLoader classLoader, String resource) {
        if (!resource.endsWith(".json"))
            resource += ".json";
        try {
            AbstractElement element = AbstractElement.fromJson(IO.readTextResource(classLoader, resource));
            if (element.isObject())
                translation.set(locale, new Translation(element.object()));
            if (element.isArray())
                translation.set(locale, new Translation(element.object()));
        } catch (IOException ignored) {
        }
    }

    public void addTranslation(Locale locale, String resource) {
        addTranslation(locale, ClassLoader.getSystemClassLoader(), resource);
    }

    public WebApplication addModule(Module module) {
        modules.add(module);
        return this;
    }

    public WebApplication setModelBindTransformer(ModelBindTransformer transformer) {
        modelBindParamTransformer.setTransformer(transformer);
        return this;
    }

    public WebApplication setAccessorAttribName(String name) {
        modelBindParamTransformer.setAccessorAttribName(name);
        return this;
    }

    public void addSeeder(String name, Seeder... seeder) {
        if (seeder.length == 0)
            return;
        if (seeder.length > 1) {
            addSeeder(name, new MergedSeeder(seeder));
            return;
        }
        seeders.put(name, seeder[0]);
    }

    public Seeder getSeeder(String name) {
        return seeders.get(name);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public HTTPServer getServer() {
        return server;
    }

    public Faker getFaker() {
        return faker;
    }

    public Config getConfig() {
        return config;
    }

    public Crypt getCrypt() {
        return crypt;
    }

    public I18N getTranslation() {
        return translation;
    }

    protected void setupModules() {
    }

    protected abstract void setupConfig(Config config);

    protected void setupSeeding() {
    }

    protected abstract void setupModels(SQL sql) throws ORMConfigurationException;

    protected abstract void setupServer(HTTPServer server);

    public void start() {
        server.start();
        server.join();
    }

}

package org.javawebstack.framework;

import com.github.javafaker.Faker;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.command.MultiCommand;
import org.javawebstack.framework.bind.ModelBindParamTransformer;
import org.javawebstack.framework.bind.ModelBindTransformer;
import org.javawebstack.framework.command.*;
import org.javawebstack.framework.config.Config;
import org.javawebstack.framework.module.Module;
import org.javawebstack.framework.seed.AllSeeder;
import org.javawebstack.framework.seed.MergedSeeder;
import org.javawebstack.framework.seed.Seeder;
import org.javawebstack.framework.util.*;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.httpserver.transformer.response.JsonResponseTransformer;
import org.javawebstack.injector.Injector;
import org.javawebstack.injector.SimpleInjector;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.MySQL;
import org.javawebstack.orm.wrapper.SQL;
import org.javawebstack.orm.wrapper.SQLite;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public abstract class WebApplication {

    private Logger logger = Logger.getLogger("WebApp");
    private final SQL sql;
    private final HTTPServer server;
    private final Injector injector;
    private final Faker faker = new Faker();
    private final Config config = new Config();
    private final Crypt crypt;
    private final List<Module> modules = new ArrayList<>();
    private final ModelBindParamTransformer modelBindParamTransformer;
    private final CommandSystem commandSystem = new CommandSystem();
    private final Map<String, Seeder> seeders = new HashMap<>();
    private final I18N translation = new I18N();

    public WebApplication() {
        injector = new SimpleInjector();
        injector.setInstance(Injector.class, injector);
        injector.setInstance(Faker.class, faker);
        injector.setInstance(Config.class, config);
        injector.setInstance((Class<WebApplication>) getClass(), this);
        injector.setInstance(WebApplication.class, this);
        injector.setInstance(CommandSystem.class, commandSystem);
        injector.setInstance(I18N.class, translation);
        commandSystem.setInjector(injector);

        setupModules();
        modules.forEach(m -> m.beforeSetupConfig(this, config));
        setupConfig(config);

        crypt = new Crypt(config.has("crypt.key") ? config.get("crypt.key") : Crypt.generateKey());
        injector.setInstance(Crypt.class, crypt);

        modules.forEach(m -> m.setupConfig(this, config));
        if (config.get("database.driver", "none").equalsIgnoreCase("sqlite")) {
            sql = new SQLite(config.get("database.file", "db.sqlite"));
        } else if (config.get("database.driver", "none").equalsIgnoreCase("mysql")) {
            sql = new MySQL(
                    config.get("database.host", "localhost"),
                    config.getInt("database.port", 3306),
                    config.get("database.name", "app"),
                    config.get("database.user", "root"),
                    config.get("database.password", "")
            );
        } else {
            sql = null;
        }
        if (sql != null) {
            try {
                for (Module m : modules)
                    m.beforeSetupModels(this, sql);
                setupModels(sql);
                for (Module m : modules)
                    m.setupModels(this, sql);
            } catch (ORMConfigurationException ex) {
                ex.printStackTrace();
            }
        }
        modelBindParamTransformer = new ModelBindParamTransformer();

        modules.forEach(m -> m.beforeSetupInjection(this, injector));
        setupInjection(injector);
        modules.forEach(m -> m.setupInjection(this, injector));

        server = new HTTPServer()
                .port(config.getInt("http.server.port", 80));
        injector.setInstance(HTTPServer.class, server);
        server.injector(injector);
        injector.inject(this);
        server.beforeInterceptor(new CORSPolicy(config.get("http.server.cors", "*")));
        server.beforeInterceptor(new MultipartPolicy(config.get("http.server.tmp", null)));
        if (config.isEnabled("http.server.json", true))
            server.responseTransformer(new JsonResponseTransformer().ignoreStrings());
        if (sql != null)
            server.routeParamTransformer(modelBindParamTransformer);
        modules.forEach(m -> m.beforeSetupServer(this, server));
        setupServer(server);
        modules.forEach(m -> m.setupServer(this, server));

        modules.forEach(m -> m.beforeSetupSeeding(this));
        setupSeeding();
        modules.forEach(m -> m.setupSeeding(this));
        addSeeder("all", new AllSeeder());

        setupCommands(commandSystem);
        modules.forEach(m -> m.setupCommands(this, commandSystem));
        commandSystem.addCommand("start", new StartCommand());
        commandSystem.addCommand("sh", new ShellCommand());
        commandSystem.addCommand("db", new MultiCommand()
                .add("migrate", new DBMigrateCommand())
                .add("seed", new DBSeedCommand())
        );
        commandSystem.addCommand("crypt", new MultiCommand()
                .add("encrypt", new CryptEncryptCommand())
                .add("decrypt", new CryptDecryptCommand())
                .add("hash", new CryptHashCommand())
        );
        commandSystem.addCommand("generate", new MultiCommand()
                .add("key", new GenerateKeyCommand())
        );
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
                translation.add(locale, element.object());
            if (element.isArray())
                translation.add(locale, element.array());
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
        for (Seeder seed : seeder)
            injector.inject(seed);
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

    public Injector getInjector() {
        return injector;
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
    protected void setupInjection(Injector injector){}
    protected void setupSeeding(){}
    protected abstract void setupModels(SQL sql) throws ORMConfigurationException;

    protected abstract void setupServer(HTTPServer server);

    protected abstract void setupCommands(CommandSystem system);

    public void run(String[] args) {
        if (args == null)
            args = new String[]{"start"};
        try {
            commandSystem.run(args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void start() {
        server.start();
        server.join();
    }

}

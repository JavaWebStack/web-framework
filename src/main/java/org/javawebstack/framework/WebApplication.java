package org.javawebstack.framework;

import com.github.javafaker.Faker;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.command.MultiCommand;
import org.javawebstack.framework.bind.ModelBindParamTransformer;
import org.javawebstack.framework.bind.ModelBindTransformer;
import org.javawebstack.framework.command.*;
import org.javawebstack.framework.config.Config;
import org.javawebstack.framework.job.*;
import org.javawebstack.framework.module.Module;
import org.javawebstack.framework.seed.AllSeeder;
import org.javawebstack.framework.seed.FileSeeder;
import org.javawebstack.framework.seed.MergedSeeder;
import org.javawebstack.framework.seed.Seeder;
import org.javawebstack.framework.util.*;
import org.javawebstack.graph.GraphElement;
import org.javawebstack.httpserver.HTTPServer;
import org.javawebstack.httpserver.transformer.response.JsonResponseTransformer;
import org.javawebstack.injector.Injector;
import org.javawebstack.injector.SimpleInjector;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
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
    private final SimpleInjector injector;
    private final Faker faker = new Faker();
    private final Config config = new Config();
    private final Crypt crypt;
    private final List<Module> modules = new ArrayList<>();
    private final ModelBindParamTransformer modelBindParamTransformer;
    private final CommandSystem commandSystem = new CommandSystem();
    private final Map<String, Seeder> seeders = new HashMap<>();
    private final I18N translation = new I18N();

    public WebApplication(){
        injector = new SimpleInjector();
        injector.setInstance(Injector.class, injector);
        injector.setInstance(SimpleInjector.class, injector);
        injector.setInstance(Faker.class, faker);
        injector.setInstance(Config.class, config);
        injector.setInstanceUnsafe(getClass(), this);
        injector.setInstance(WebApplication.class, this);
        injector.setInstance(CommandSystem.class, commandSystem);
        injector.setInstance(I18N.class, translation);
        commandSystem.setInjector(injector);

        setupModules();
        modules.forEach(m -> m.beforeSetupConfig(this, config));
        setupConfig(config);

        crypt = new Crypt(config.has("crypt.key") ? config.get("crypt.key") : Crypt.generateKey());
        injector.setInstance(Crypt.class, crypt);

        addSeeder("all", new AllSeeder());

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
        if(config.isEnabled("http.server.json", true))
            server.responseTransformer(new JsonResponseTransformer().ignoreStrings());
        if(sql != null)
            server.routeParamTransformer(modelBindParamTransformer);
        modules.forEach(m -> m.beforeSetupServer(this, server));
        setupServer(server);
        modules.forEach(m -> m.setupServer(this, server));

        setupSeeding();

        setupCommands(commandSystem);
        modules.forEach(m -> m.setupCommands(this, commandSystem));
        commandSystem.addCommand("start", new StartCommand());
        commandSystem.addCommand("worker", new WorkerCommand());
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
                .add("seed", new GenerateSeedCommand())
        );
    }

    public Map<String, Seeder> getSeeders(){
        return seeders;
    }

    public void addTranslation(Locale locale, ClassLoader classLoader, String resource){
        if(!resource.endsWith(".json"))
            resource += ".json";
        try {
            GraphElement element = GraphElement.fromJson(IO.readTextResource(classLoader, resource));
            if(element.isObject())
                translation.add(locale, element.object());
            if(element.isArray())
                translation.add(locale, element.array());
        } catch (IOException ignored) {}
    }

    public void addTranslation(Locale locale, String resource){
        addTranslation(locale, ClassLoader.getSystemClassLoader(), resource);
    }

    public void addDatabaseJobQueue(String name, boolean defaultQueue){
        addQueue(name, new DatabaseJobQueue(name), defaultQueue);
    }

    public void enableDatabaseJobs(ORMConfig config) throws ORMConfigurationException {
        ORM.register(DatabaseQueuedJob.class, sql, config);
    }

    public void addSyncJobQueue(String name, int capacity, boolean defaultQueue){
        addQueue(name, new SyncThreadedJobQueue(capacity).start(), defaultQueue);
    }

    public void addImmediateJobQueue(String name, boolean defaultQueue){
        addQueue(name, new ImmidiateJobQueue(), defaultQueue);
    }

    public void addQueue(String name, JobQueue queue, boolean defaultQueue){
        injector.setInstance(JobQueue.class, name, queue);
        if(defaultQueue)
            injector.setInstance(JobQueue.class, "", queue);
    }

    public WebApplication addModule(Module module){
        modules.add(module);
        return this;
    }

    public WebApplication setModelBindTransformer(ModelBindTransformer transformer){
        modelBindParamTransformer.setTransformer(transformer);
        return this;
    }

    public WebApplication setAccessorAttribName(String name){
        modelBindParamTransformer.setAccessorAttribName(name);
        return this;
    }

    public void addSeeder(String name, Seeder... seeder){
        if(seeder.length == 0)
            return;
        for(Seeder seed : seeder)
            injector.inject(seed);
        if(seeder.length > 1){
            addSeeder(name, new MergedSeeder(seeder));
            return;
        }
        seeders.put(name, seeder[0]);
    }

    public Seeder getSeeder(String name){
        if(!seeders.containsKey(name))
            seeders.put(name, new FileSeeder(this, getClass().getClassLoader(), "seeds/"+name+".json"));
        return seeders.get(name);
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

    public I18N getTranslation(){
        return translation;
    }

    protected void setupModules(){}
    protected abstract void setupConfig(Config config);
    protected void setupInjection(SimpleInjector injector){}
    protected void setupSeeding(){}
    protected abstract void setupModels(SQL sql) throws ORMConfigurationException;
    protected abstract void setupServer(HTTPServer server);
    protected abstract void setupCommands(CommandSystem system);

    public void run(String[] args){
        if(args == null)
            args = new String[]{"start"};
        commandSystem.run(args);
    }

    public void start(){
        server.start();
        server.join();
    }

    public void startWorker(String... queues){
        List<WorkerJobQueue> workerQueues = new ArrayList<>();
        for(String name : queues){
            JobQueue queue = injector.getInstance(JobQueue.class, name);
            if(queue == null)
                continue;
            if(queue instanceof WorkerJobQueue)
                workerQueues.add((WorkerJobQueue) queue);
        }
        if(workerQueues.size() == 0){
            logger.severe("No queue to process!");
            return;
        }
        UUID processUUID = UUID.randomUUID();
        logger.info("Running worker ("+processUUID.toString()+")");
        while (true){
            for(WorkerJobQueue queue : workerQueues)
                queue.process(processUUID);
        }
    }

}

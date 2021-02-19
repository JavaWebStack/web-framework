<p align="center"><img src="https://raw.githubusercontent.com/JavaWebStack/docs/master/docs/assets/img/icon.svg" width="100">

# Web-Framework
The Web Framework combines multiple JavaWebStack libraries with glue code and helpers to allow fast web development



# Getting started
## Repository
### Maven (pom.xml)
```xml
<repositories>
    <repository>
        <id>javawebstack</id>
        <url>https://repo.javawebstack.org</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.javawebstack</groupId>
        <artifactId>Web-Framework</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.22</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```
`Build` [Buildscript for compilation with Maven](https://gist.github.com/JulianFun123/14b412245ecf0257c7819eb8dadc1438)

## Usage
```java

public class ExampleApplication extends WebApplication {

    protected void setupConfig(Config config) {
        Map<String, String> map = new HashMap<>();
        // Maps a config entry with an key for .get("key")
        map.put("EXAMPLE_KEY", "example.key");
        config.addEnvKeyMapping(map);
        
        // Registers .env as a configuration and adds System envs
        config.addEnvFile(".env");
        
    }

    protected void setupModels(SQL sql) throws ORMConfigurationException {
        // The connection is going to be built by the credentials given in the config
        
        ORMConfig config = new ORMConfig().setTablePrefix("example_"); // .setDefaultSize(255)
        // Registeres every Model in the Users package with the sql connection and ORMConfig
        ORM.register(User.class.getPackage(), sql, config);
        // Adds or updates the database structure 
        ORM.autoMigrate();
    }

    protected void setupServer(HTTPServer server) {
        server.beforeInterceptor(exchange -> {
           // Just an attrib example (Passes it through following routes & middlewares)
           exchange.attrib("user", Repo.get(User.class).get(exchange.header("example-user-id")));
           // Do not intercept
           return false;
        });

        // Adds every Controller in the package of ExampleController and extends HttpController (recursive)
        server.controller(HttpController.class, ExampleController.class.getPackage());
    }


}

```

```java
@PathPrefix("/user")
public class ExampleController extends HttpController {
    @Get("/{id}")
    public String getUser(Exchange exchange, @Attrib("user") User user) {
        return user.name;
    }
}
```
```java
public class User extends Model {
    @Column
    public int id;
    
    @Column
    public String name;
}
```



- Docs: https://javawebstack.org/framework/
- Example Project https://github.com/javawebstack/

## Community
### JWS Modules
- [JWS-GraphQL](https://github.com/x7airworker/JWS-GraphQL)
### Projects built with JWS

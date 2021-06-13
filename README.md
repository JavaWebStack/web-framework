<p align="center"><img src="https://raw.githubusercontent.com/JavaWebStack/docs/master/docs/assets/img/icon.svg" width="100">
<br><br>
JWS Web Framework
</p>

![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/JavaWebStack/Web-Framework/Maven%20Deploy/master)
![GitHub](https://img.shields.io/github/license/JavaWebStack/Web-Framework)
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.javawebstack.org%2Forg%2Fjavawebstack%2FWeb-Framework%2Fmaven-metadata.xml)
![GitHub contributors](https://img.shields.io/github/contributors/JavaWebStack/Web-Framework)
![Lines of code](https://img.shields.io/tokei/lines/github/JavaWebStack/Web-Framework)
![Discord](https://img.shields.io/discord/815612319378833408?color=%237289DA&label=discord)
![Twitter Follow](https://img.shields.io/twitter/follow/JavaWebStack?style=social)

## Introduction
The Web Framework combines multiple JavaWebStack libraries with glue code and helpers to allow fast web development
## Quick Example

```java

import org.javawebstack.httpserver.router.annotation.Body;

class MyWebApp extends WebApplication {

    protected void setupConfig(Config config) {
        config.addEnvFile(".env");
    }

    protected void setupModels(SQL sql) throws ORMConfigurationException {
    }

    protected void setupServer(HTTPServer server) {
        server.get("/", exchange -> {
            return "Hello World";
        });
        
        server.controller(new ExampleController());
    }
}

// Example Controller

@PathPrefix("/api/v1")
public class ExampleController extends HttpController {
    @Get("/{name}")
    public GetSomethingResponse getSomething(@Path("name") String name) {
        GetSomethingResponse response = new GetSomethingResponse();
        response.name = name;
        // Automatically Serializes it to JSON if Accept header is not set.
        return response;
    }

    @Post // If empty it uses the /api/v1 route
    public boolean createSomething(@Body CreateSomethingRequest request) {
        // @Body uses JSON for default to unserialize it into an object of the given type, but if the Accept header is set, you can use yaml or form 
        System.out.println(request.name);
        return true;
    }

    // Example Response Model
    public class GetSomethingResponse {
        public String name;
    }

    // Example Request Model
    public class CreateSomethingRequest {
        public String name;
    }
}
```
## Documentation
You can find the current docs on our [website](https://docs.javawebstack.org/framework). This is a work-in-progress project though so it's not yet complete.

## JWS Modules
- [Passport](https://github.com/javawebstack/Passport): A library for adding OAuth2 to authenticate in your Application

## Community
### JWS Modules
- [JWS-GraphQL](https://github.com/x7airworker/JWS-GraphQL)
### Projects built with JWS
- [Pastefy](https://github.com/interaapps/pastefy)
- [InteraApps Passwords](https://github.com/interaapps/passwords-backend)

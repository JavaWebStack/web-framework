package org.javawebstack.framework.seed;

import org.javawebstack.framework.WebApplication;
import org.javawebstack.injector.Inject;

public class AllSeeder implements Seeder {

    @Inject
    private WebApplication app;

    public void seed() {
        app.getSeeders().forEach((n,s) -> s.seed());
    }

}

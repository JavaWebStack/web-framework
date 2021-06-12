package org.javawebstack.framework.seed;

import org.javawebstack.framework.WebApplication;

public class AllSeeder implements Seeder {

    private WebApplication app;

    public void seed() {
        app.getSeeders().forEach((n, s) -> {
            if (!n.equals("all"))
                s.seed();
        });
    }

}

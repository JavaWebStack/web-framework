package org.javawebstack.framework.seed;

import java.util.Arrays;
import java.util.List;

public class MergedSeeder implements Seeder {

    private final List<Seeder> seeders;

    public MergedSeeder(List<Seeder> seeders){
        this.seeders = seeders;
    }

    public MergedSeeder(Seeder... seeders){
        this(Arrays.asList(seeders));
    }

    public void seed() {
        seeders.forEach(Seeder::seed);
    }

}

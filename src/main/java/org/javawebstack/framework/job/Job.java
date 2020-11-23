package org.javawebstack.framework.job;

public interface Job {

    boolean run();
    int maxRetries();

    default void start(){
        new Thread(this::run).start();
    }

}

package org.javawebstack.framework.job;

public class ImmidiateJobQueue implements JobQueue {
    public void run() {

    }
    public void queue(Job job) {
        job.run();
    }
}

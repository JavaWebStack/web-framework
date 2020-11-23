package org.javawebstack.framework.job;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class SyncThreadedJobQueue implements JobQueue {
    private final Queue<Job> queue;
    public SyncThreadedJobQueue(int limit){
        queue = new ArrayBlockingQueue<>(limit);
    }
    public void queue(Job job) {
        queue.add(job);
    }
    public void run() {
        while (true){
            Job job = queue.poll();
            job.run();
        }
    }
    public void start(){
        new Thread(this).start();
    }
}

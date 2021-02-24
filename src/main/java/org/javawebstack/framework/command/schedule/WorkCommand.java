package org.javawebstack.framework.command.schedule;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.WebApplication;
import org.javawebstack.injector.Inject;
import org.javawebstack.scheduler.Worker;
import org.javawebstack.scheduler.job.JobQueue;
import org.javawebstack.scheduler.job.JobWorker;
import org.javawebstack.scheduler.scheduler.Schedule;
import org.javawebstack.scheduler.scheduler.Scheduler;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WorkCommand implements Command {
    @Inject
    private WebApplication webApplication;

    @Inject
    private JobQueue jobQueue;

    @Inject
    private Schedule schedule;

    public CommandResult execute(CommandSystem commandSystem, List<String> list, Map<String, List<String>> map) {

        Worker worker = new Worker();
        worker.addWork(new JobWorker(jobQueue), new Scheduler(schedule, jobQueue));

        int delay = 6000;

        if (list.size() > 0) {
            delay = Integer.parseInt(list.get(0));
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                worker.run();
            }
        }, delay, delay);

        return null;
    }
}

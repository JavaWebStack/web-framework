package org.javawebstack.framework.job;

import org.javawebstack.graph.GraphElement;
import org.javawebstack.graph.GraphMapper;
import org.javawebstack.graph.NamingPolicy;
import org.javawebstack.orm.Repo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseJobQueue implements WorkerJobQueue {

    private static final GraphMapper mapper = new GraphMapper().setNamingPolicy(NamingPolicy.SNAKE_CASE);
    private final String name;
    private final Map<Class<? extends Job>, String> typeMap = new HashMap<>();

    public DatabaseJobQueue(String name){
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void queue(Job job){
        DatabaseQueuedJob queued = new DatabaseQueuedJob();
        queued.setType(getType(job));
        queued.setData(mapper.toGraph(job).toJsonString());
        queued.setMaxRetries(job.maxRetries());
        queued.setQueue(name);
        queued.save();
    }

    public DatabaseQueuedJob pull(UUID processUUID){
        DatabaseQueuedJob job = Repo.get(DatabaseQueuedJob.class).where("queue", name).isNull("processUUID").get();
        if(job != null) {
            job.setProcessUUID(processUUID);
            job.save();
            job.refresh();
            if (processUUID.equals(job.getProcessUUID()))
                return job;
        }
        return null;
    }

    public void process(UUID processUUID){
        DatabaseQueuedJob queuedJob = pull(processUUID);
        if(queuedJob != null){
            Job job = mapper.fromGraph(GraphElement.fromJson(queuedJob.getData()), getType(queuedJob.getType()));
            boolean success;
            try {
                success = job.run();
            }catch (Throwable t){
                t.printStackTrace();
                success = false;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
            if(success || queuedJob.getRetries() >= queuedJob.getMaxRetries()){
                queuedJob.delete();
            }else{
                queuedJob.setRetries(queuedJob.getRetries()+1);
                queuedJob.setProcessUUID(null);
                queuedJob.save();
            }
        }else{
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }
    }

    public void register(String name, Class<? extends Job> type){
        typeMap.put(type, name);
    }

    public void register(Class<? extends Job> type){
        register(type.getSimpleName(), type);
    }

    public String getType(Class<? extends Job> type){
        return typeMap.get(type);
    }

    public String getType(Job job){
        return getType(job.getClass());
    }

    public Class<? extends Job> getType(String name){
        for(Class<? extends Job> type : typeMap.keySet()){
            if(typeMap.get(type).equals(name))
                return type;
        }
        return null;
    }

}

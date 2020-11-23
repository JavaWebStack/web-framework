package org.javawebstack.framework.job;

import java.util.UUID;

public interface WorkerJobQueue extends JobQueue {

    void process(UUID processUUID);

}

package org.javawebstack.framework.job;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Dates @Table("jobs")
public class DatabaseQueuedJob extends Model {

    @Column
    private int id;
    @Column
    private String queue;
    @Column
    private UUID processUUID;
    @Column
    private String type;
    @Column
    private String data;
    @Column
    private Integer retries = 0;
    @Column
    private Integer maxRetries = 0;
    @Column
    private Timestamp createdAt;
    @Column
    private Timestamp updatedAt;

    public int getId() {
        return id;
    }

    public String getQueue(){
        return queue;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public Integer getRetries() {
        return retries;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public UUID getProcessUUID() {
        return processUUID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setProcessUUID(UUID processUUID) {
        this.processUUID = processUUID;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

}

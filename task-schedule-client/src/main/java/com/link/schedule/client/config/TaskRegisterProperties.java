package com.link.schedule.client.config;

/**
 * Created by rocky on 15/10/17.
 */
public class TaskRegisterProperties {

    private String owner;

    private String application;

    private String cronExpress;

    private String desc;

    private String key;

    private boolean isConcurrent;

    private String host;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public void setCronExpress(String cronExpress) {
        this.cronExpress = cronExpress;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isConcurrent() {
        return isConcurrent;
    }

    public void setIsConcurrent(boolean isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}

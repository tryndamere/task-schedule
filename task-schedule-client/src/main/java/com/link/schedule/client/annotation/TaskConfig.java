package com.link.schedule.client.annotation;

/**
 * Created by rocky on 2015/10/15.
 */
public class TaskConfig {

    private String owner;

    private String application;

    private String cronExpress;

    private String desc;

    private String key;

    private boolean isConcurrent;

    private String host;

    private String methodName;

    private String className;

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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}

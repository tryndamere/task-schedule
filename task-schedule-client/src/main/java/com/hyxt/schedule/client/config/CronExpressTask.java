package com.hyxt.schedule.client.config;

import com.hyxt.schedule.client.util.NetUtils;

/**
 * Created by rocky on 2015/10/19.
 */
public class CronExpressTask {

    private String cronExpress;

    private String desc;

    private String key;

    private boolean isConcurrent;

    private String application;

    private String owner;

    private String ip;

    public CronExpressTask() {}

    public CronExpressTask(String cronExpress, String desc, String key, boolean isConcurrent) {
        this(cronExpress , desc , key , isConcurrent , null , null);
    }

    public CronExpressTask(String key, String cronExpress) {
        this(cronExpress , null , key , false);
    }

    private CronExpressTask(String cronExpress, String desc, String key, boolean isConcurrent,
                            String application, String owner) {
        this.cronExpress = cronExpress;
        this.desc = desc;
        this.key = key;
        this.isConcurrent = isConcurrent;
        this.application = application;
        this.owner = owner;
    }

    public CronExpressTask setApplication(String application) {
        return new CronExpressTask(cronExpress , desc , key , isConcurrent , application , owner);
    }

    public CronExpressTask setOwner(String owner) {
        return new CronExpressTask(cronExpress , desc , key , isConcurrent , application , owner);
    }

    public CronExpressTask setKey(String key) {
        return new CronExpressTask(cronExpress , desc , key , isConcurrent , application , owner);
    }

    public CronExpressTask setCronExpress(String cronExpress) {
        return new CronExpressTask(cronExpress , desc , key , isConcurrent , application , owner);
    }

    public CronExpressTask setDesc(String desc) {
        return new CronExpressTask(cronExpress , desc , key , isConcurrent , application , owner);
    }

    public CronExpressTask setIsConcurrent(boolean isConcurrent) {
        return new CronExpressTask(cronExpress, desc, key, isConcurrent, application, owner);
    }

    public String getCronExpress() {
        return cronExpress;
    }

    public String getDesc() {
        return desc;
    }

    public String getKey() {
        return key;
    }

    public boolean isConcurrent() {
        return isConcurrent;
    }

    public String getApplication() {
        return application;
    }

    public String getOwner() {
        return owner;
    }

    public String getIp() {
        if (ip == null) {
            this.ip = NetUtils.getLocalAddress().getHostAddress();
        }
        return ip;
    }

}

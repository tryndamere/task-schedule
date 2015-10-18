package com.link.schedule.client.config;

/**
 * Created by rocky on 2015/10/15.
 */
public class CronTaskConfiguration {

    private String cronExpress;

    private String desc;

    private String key;

    private boolean isConcurrent;

    private String host;

    private String ip;

    public CronTaskConfiguration(String cronExpress, String desc,
                                        String key, boolean isConcurrent, String host , String ip) {
        this.cronExpress = cronExpress;
        this.desc = desc;
        this.key = key;
        this.isConcurrent = isConcurrent;
        this.host = host;
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

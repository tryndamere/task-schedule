package com.hyxt.boot.autoconfigure.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by rocky on 2015/10/12.
 */
@ConfigurationProperties(prefix = "spring.zookeeper")
public class ZookeeperProperties {

    private String connectionString;

    private int connectionTimeoutMs;

    private int sessionTimeoutMs;

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }
}

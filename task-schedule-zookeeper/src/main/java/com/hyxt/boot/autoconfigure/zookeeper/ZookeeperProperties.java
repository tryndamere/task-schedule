package com.hyxt.boot.autoconfigure.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by rocky on 2015/10/12.
 */
@ConfigurationProperties(prefix = "hyxt.zookeeper")
public class ZookeeperProperties {

    private String connectionString;

    private int connectionTimeoutMs = 60 * 1000;

    private int sessionTimeoutMs = 5 * connectionTimeoutMs;

    private boolean isBlockUntilConnectedOrTimedOut = true;

    private int baseSleepTimeMs = 3000;

    private int maxSleepTimeMs = Integer.MAX_VALUE;

    private int maxRetryCount = 29;

    private int maxCloseWaitMs = 15000;

    public int getMaxCloseWaitMs() {
        return maxCloseWaitMs;
    }

    public void setMaxCloseWaitMs(int maxCloseWaitMs) {
        this.maxCloseWaitMs = maxCloseWaitMs;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public boolean isBlockUntilConnectedOrTimedOut() {
        return isBlockUntilConnectedOrTimedOut;
    }

    public void setIsBlockUntilConnectedOrTimedOut(boolean isBlockUntilConnectedOrTimedOut) {
        this.isBlockUntilConnectedOrTimedOut = isBlockUntilConnectedOrTimedOut;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxSleepTimeMs() {
        return maxSleepTimeMs;
    }

    public void setMaxSleepTimeMs(int maxSleepTimeMs) {
        this.maxSleepTimeMs = maxSleepTimeMs;
    }

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

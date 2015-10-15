package com.link.schedule.client.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

/**
 * Created by rocky on 2015/10/15.
 */
@ConfigurationProperties(prefix = "hyxt.task")
public class TaskProperties {

    private String owner;

    private String application;

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
}

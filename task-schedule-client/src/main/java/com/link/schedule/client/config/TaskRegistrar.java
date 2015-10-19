package com.link.schedule.client.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by rocky on 15/10/17.
 */
public class TaskRegistrar implements InitializingBean , DisposableBean {

    @Value("${schedule.project.application}")
    private String application;

    @Value("${schedule.project.owner}")
    private String owner;

    public void destroy() throws Exception {

    }

    public void afterPropertiesSet() throws Exception {

    }
}

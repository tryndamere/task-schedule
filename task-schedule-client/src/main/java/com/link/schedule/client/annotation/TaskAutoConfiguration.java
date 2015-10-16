package com.link.schedule.client.annotation;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Schedules;

import java.lang.annotation.*;

/**
 * Created by rocky on 2015/10/15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TaskAutoConfiguration {

    /**
     * this key must be unique in the project
     * eg: beanId
     * @return key
     */
    String key();

    /**
     * The task description
     *
     * @return
     */
    String desc() default "";

    /**
     * The code owner
     *
     * @return
     */
    String owner() default "";

    /**
     * The project name
     *
     * @return
     */
    String application() default "";

    /**
     * quartz cron express
     *
     * @return
     */
    String cronExpress();

    /**
     * whether concurrent
     *
     * @return
     */
    boolean isConcurrent() default false;

}

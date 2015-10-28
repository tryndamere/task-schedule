package com.hyxt.schedule.client.annotation;

import java.lang.annotation.*;

/**
 * Created by rocky on 2015/10/15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TaskDefinitionAndHandler {

    /**
     * job的唯一key，在项目中是不能重复。必须唯一
     *
     * @return key
     */
    String key();

    /**
     * 任务描述
     *
     * @return
     */
    String desc() default "";

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

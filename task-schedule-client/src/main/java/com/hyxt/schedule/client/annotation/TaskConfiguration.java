package com.hyxt.schedule.client.annotation;

import com.hyxt.boot.autoconfigure.serializer.DefaultZookeeperSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rocky on 2015/10/26.
 */
@Configuration
public class TaskConfiguration {

    @Bean
    public TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor() {
        TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor = new TaskAnnotationBeanPostProcessor();
        taskAnnotationBeanPostProcessor.setZookeeperSerializer(new DefaultZookeeperSerializer());
        return taskAnnotationBeanPostProcessor;
    }

}

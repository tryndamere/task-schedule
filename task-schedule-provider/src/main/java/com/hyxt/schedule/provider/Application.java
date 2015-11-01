package com.hyxt.schedule.provider;

import com.hyxt.boot.autoconfigure.ZookeeperOperation;
import com.hyxt.boot.autoconfigure.serializer.DefaultZookeeperSerializer;
import com.hyxt.schedule.provider.leader.ScheduleLeader;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * Created by rocky on 2015/10/28.
 */
@Configuration
@ComponentScan(basePackages = "com.hyxt")
public class Application {

    @Bean
    public Scheduler getSchedulerFactory() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        return schedulerFactory.getScheduler();
    }

    @Bean
    public ScheduleLeader getScheduleLeader(Scheduler scheduler , ZookeeperOperation zookeeperOperation) {
        ScheduleLeader scheduleLeader = new ScheduleLeader();
        scheduleLeader.setScheduler(scheduler);
        scheduleLeader.setZookeeperOperation(zookeeperOperation);
        scheduleLeader.setZookeeperSerializer(new DefaultZookeeperSerializer());
        return scheduleLeader;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class , args);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

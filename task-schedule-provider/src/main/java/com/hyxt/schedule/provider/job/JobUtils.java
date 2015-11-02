package com.hyxt.schedule.provider.job;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobUtils {

    public static final String JOB_DATA_KEY = "path";

    public static JobDetail jobDetail(String path , String group) {
        return newJob(JobImpl.class).withIdentity(new JobKey(path)).usingJobData(JOB_DATA_KEY, path).withIdentity(path , group).build();
    }

    public static Trigger trigger(String path , String group , String cronExpression) {
        return newTrigger().withIdentity(path , group).withSchedule(cronSchedule(cronExpression)).startNow().build();
    }

}
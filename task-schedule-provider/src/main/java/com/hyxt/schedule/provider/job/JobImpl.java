package com.hyxt.schedule.provider.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by rocky on 15/10/31.
 */
public class JobImpl implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobKeyPath = context.getJobDetail().getJobDataMap().getString(JobUtils.JOB_DATA_KEY);
    }

}

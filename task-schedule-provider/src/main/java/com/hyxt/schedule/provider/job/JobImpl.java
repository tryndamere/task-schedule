package com.hyxt.schedule.provider.job;

import com.hyxt.boot.autoconfigure.ZookeeperOperation;
import com.hyxt.schedule.common.config.TaskExecutorStateEnum;
import com.hyxt.schedule.provider.cluster.LoadBalance;
import com.hyxt.schedule.provider.cluster.RandomLoadBalance;
import com.hyxt.schedule.provider.util.SpringContextHolder;
import org.quartz.*;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by rocky on 15/10/31.
 */
public class JobImpl implements Job {

    private ZookeeperOperation zookeeperOperation;

    private Scheduler scheduler;

    public JobImpl() {
        this.zookeeperOperation = SpringContextHolder.getBean(ZookeeperOperation.class);
        this.scheduler = SpringContextHolder.getBean(Scheduler.class);
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobKeyPath = context.getJobDetail().getJobDataMap().getString(JobUtils.JOB_DATA_KEY);
        List<String> children = this.zookeeperOperation.getChildren(jobKeyPath);
        if (children != null && children.size() > 0) {
            LoadBalance loadBalance = new RandomLoadBalance();
            String selector = loadBalance.selector(children);
            String command = TaskExecutorStateEnum.DOING.toString() + ";" + System.currentTimeMillis();
            this.zookeeperOperation.setData(selector , command.getBytes(Charset.defaultCharset()));
        } else {
            try {
                this.scheduler.deleteJob(new JobKey(jobKeyPath));
            } catch (SchedulerException e) {
                throw new RuntimeException(String.format("delete job failed , key : %s" , jobKeyPath) , e);
            }
        }
    }

}

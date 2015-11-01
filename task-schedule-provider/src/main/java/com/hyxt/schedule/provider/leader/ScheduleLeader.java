package com.hyxt.schedule.provider.leader;

import com.hyxt.boot.autoconfigure.ZookeeperConstants;
import com.hyxt.boot.autoconfigure.ZookeeperOperation;
import com.hyxt.boot.autoconfigure.serializer.ZookeeperSerializer;
import com.hyxt.schedule.provider.config.CronExpressTask;
import com.hyxt.schedule.provider.job.JobUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by rocky on 2015/10/28.
 */
public class ScheduleLeader implements InitializingBean , DisposableBean {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ZookeeperOperation zookeeperOperation;

    private Scheduler scheduler;

    private LeaderSelector leaderSelector;

    private ZookeeperSerializer zookeeperSerializer;

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setZookeeperOperation(ZookeeperOperation zookeeperOperation) {
        this.zookeeperOperation = zookeeperOperation;
    }

    public void setZookeeperSerializer(ZookeeperSerializer zookeeperSerializer) {
        this.zookeeperSerializer = zookeeperSerializer;
    }

    public void destroy() throws Exception {
        this.leaderSelector.close();
        this.zookeeperOperation.close();
    }

    public void afterPropertiesSet() throws Exception {
         this.leaderSelector = new LeaderSelector(this.zookeeperOperation.getCuratorFramework(), ZookeeperConstants.ZK_SCHEDULE_LEADER,
                 new LeaderSelectorListener() {

            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                List<String> applications = ScheduleLeader.this.zookeeperOperation.getChildren(ZookeeperConstants.ZK_SCHEDULE_JOB);
                if (applications != null && applications.size() > 0) {
                    for (String application : applications) {
                        List<String> jobKeys = ScheduleLeader.this.zookeeperOperation.getChildren(application);
                        if (jobKeys != null && jobKeys.size() > 0) {
                            for (final String jobKey : jobKeys) {
                                if (!createJob(jobKey)) {
                                    final PathChildrenCache pathChildrenCache = new PathChildrenCache(ScheduleLeader.this.zookeeperOperation.getCuratorFramework() ,
                                            jobKey , false);
                                    pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                                        public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                                            createJob(jobKey);
                                        }
                                    });
                                    pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
                                    LOGGER.info("create path listener because that path has not children , jobKey : {} " , jobKey);
                                }
                            }
                            if (ScheduleLeader.this.scheduler.isStarted()) {
                                ScheduleLeader.this.scheduler.start();
                            }
                        }
                    }
                }
                new CountDownLatch(1).await();
            }

            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.LOST || newState == ConnectionState.SUSPENDED) {
                    try {
                        scheduler.shutdown(true);
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                    throw new CancelLeadershipException();
                }
            }

            private boolean createJob(String jobKey) throws SchedulerException {
                boolean isCreate = false;
                CronExpressTask cronExpressTask = ScheduleLeader.this.zookeeperSerializer.deserializer(
                        ScheduleLeader.this.zookeeperOperation.getData(jobKey), CronExpressTask.class);
                List<String> children = ScheduleLeader.this.zookeeperOperation.getChildren(jobKey);
                //如果启动时，发现无业务节点则此job无效
                if (children != null && children.size() > 0) {
                    JobKey jobKeyObject = new JobKey(jobKey);
                    if (ScheduleLeader.this.scheduler.checkExists(jobKeyObject)) {
                        ScheduleLeader.this.scheduler.deleteJob(jobKeyObject);
                    }
                    JobDetail jobDetail = JobUtils.jobDetail(jobKey, cronExpressTask.getApplication());
                    Trigger trigger = JobUtils.trigger(jobKey, cronExpressTask.getApplication(), cronExpressTask.getCronExpress());
                    ScheduleLeader.this.scheduler.scheduleJob(jobDetail , trigger);
                    isCreate = true;
                    LOGGER.info("create job , jobKey : {}" , jobKey);
                }
                return isCreate;
            }

        });

        this.leaderSelector.autoRequeue();
        this.leaderSelector.start();
    }
}

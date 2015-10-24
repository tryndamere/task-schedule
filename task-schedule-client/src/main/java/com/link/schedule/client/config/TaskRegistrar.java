package com.link.schedule.client.config;

import com.hyxt.boot.autoconfigure.ZookeeperConstants;
import com.link.schedule.client.serializer.ZookeeperSerializer;
import com.link.schedule.client.support.TaskMethodRunnable;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.*;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rocky on 15/10/17.
 */
public class TaskRegistrar implements InitializingBean , DisposableBean {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String application;

    private String owner;

    private List<CronExpressTask> cronExpressTasks;

    private CuratorFramework curatorFramework;

    private ZookeeperSerializer zookeeperSerializer;

    private Map<String , Runnable> runnableMap ;

    private List<NodeCache> nodeCaches ;

    public void setApplication(String application) {
        this.application = application;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setZookeeperSerializer(ZookeeperSerializer zookeeperSerializer) {
        this.zookeeperSerializer = zookeeperSerializer;
    }

    public void setCuratorFramework(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }

    public void addCronExpressTask(CronExpressTask cronExpressTask) {
        if (this.cronExpressTasks == null) {
            this.cronExpressTasks = new ArrayList<CronExpressTask>(10);
        }
        this.cronExpressTasks.add(cronExpressTask);
    }

    public void addTaskRunnable(TaskMethodRunnable taskMethodRunnable) {
        if (runnableMap == null) {
           this.runnableMap = new ConcurrentHashMap<String, Runnable>(10);
        }
        this.runnableMap.put(taskMethodRunnable.getKey() , taskMethodRunnable);
    }

    public void destroy() throws Exception {
        this.curatorFramework.close();
        this.runnableMap.clear();
    }

    public void afterPropertiesSet() throws Exception {
        scheduleTasks();
        createConnectionStateListener();
    }

    private void scheduleTasks() {
        if (this.cronExpressTasks != null && this.cronExpressTasks.size() > 0) {
            for (CronExpressTask cronExpressTask : this.cronExpressTasks) {
                cronExpressTask.setApplication(this.application);
                cronExpressTask.setOwner(this.owner);
                String rootPath = "/" + ZookeeperConstants.ZK_NAMESPACE + "/";
                String applicationPath = this.createAndCheckedPath(rootPath + cronExpressTask.getApplication() , CreateMode.PERSISTENT);
                String jobKeyPath = this.createAndCheckedPath(applicationPath + "/" + cronExpressTask.getKey(), cronExpressTask , CreateMode.PERSISTENT);
                final String executorPath = this.createAndCheckedPath(jobKeyPath + "/" + cronExpressTask.getIp(), CreateMode.EPHEMERAL);
                createNotify(executorPath);
            }
        }
    }

    private void createConnectionStateListener() {
        this.curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if(newState == ConnectionState.SUSPENDED || newState == ConnectionState.LOST) {
                    destroyNodeCaches();
                    scheduleTasks();
                }
            }
        });
    }

    private void createNotify(final String executorPath) {
        final NodeCache nodeCache = new NodeCache(this.curatorFramework , executorPath);
        try {
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    String[] allPath = executorPath.split("\\/");
                    String key = allPath[allPath.length - 2];
                    String deserializer = zookeeperSerializer.deserializer(nodeCache.getCurrentData().getData());
                    LOGGER.info("notify client , data : {}", deserializer);
                    if (deserializer.equals(TaskExecutorStateEnum.DOING)) {
                        LOGGER.info("notify client , executor : {}" , deserializer);
                        runnableMap.get(key).run();
                    }
                    LOGGER.info("client execute finished");
                }
            });
            nodeCache.start();
            if (this.nodeCaches == null) {
                this.nodeCaches = new ArrayList<NodeCache>(10);
            }
            this.nodeCaches.add(nodeCache);
        } catch (Exception e) {
            throw new RuntimeException("node create erorr!");
        }
    }

    private void destroyNodeCaches() {
        if (nodeCaches != null && nodeCaches.size() > 0) {
            for (NodeCache nodeCache : nodeCaches) {
                try {
                    nodeCache.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String createAndCheckedPath(String path , CreateMode createMode) {
        return createAndCheckedPath(path , null , createMode);
    }

    private String createAndCheckedPath(String path , CronExpressTask cronExpressTask , CreateMode createMode) {
        String resultStr = path;
        try {
            Stat stat = this.curatorFramework.checkExists().forPath(path);
            if (stat == null) {
                ACLBackgroundPathAndBytesable<String> stringACLBackgroundPathAndBytesable = this.curatorFramework.create().withMode(createMode);
                if (cronExpressTask == null) {
                    resultStr = stringACLBackgroundPathAndBytesable.forPath(application);
                } else {
                    resultStr = stringACLBackgroundPathAndBytesable.forPath(path, this.zookeeperSerializer.serializer(cronExpressTask));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("create path failed" , e);
        }
        return resultStr;
    }
}

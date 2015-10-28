package com.hyxt.schedule.client.config;

import com.hyxt.boot.autoconfigure.ZookeeperConstants;
import com.hyxt.schedule.client.serializer.ZookeeperSerializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.*;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

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
            this.cronExpressTasks = new ArrayList<CronExpressTask>(1);
        }
        this.cronExpressTasks.add(cronExpressTask);
    }

    public void addTaskRunnable(Runnable runnable , String key) {
        if (runnableMap == null) {
           this.runnableMap = new ConcurrentHashMap<String, Runnable>(10);
        }
        this.runnableMap.put(key , runnable);
    }

    public void destroy() throws Exception {
        this.curatorFramework.close();
        this.runnableMap.clear();
    }

    public void afterPropertiesSet() {
        scheduleTasks();
        createConnectionStateListener();
    }

    private void scheduleTasks() {
        if (this.cronExpressTasks != null && this.cronExpressTasks.size() > 0) {
            for (CronExpressTask cronExpressTask : this.cronExpressTasks) {
                cronExpressTask = cronExpressTask.setApplication(this.application).setOwner(this.owner);
                String rootPath = "/" + ZookeeperConstants.ZK_NAMESPACE + "/";
                String applicationPath = this.createAndCheckedPath(rootPath + cronExpressTask.getApplication() , CreateMode.PERSISTENT);
                String jobKeyPath = this.createAndCheckedPath(applicationPath + "/" + cronExpressTask.getKey() ,  CreateMode.PERSISTENT);
                this.createAndCheckedPath(jobKeyPath + "/" + cronExpressTask.getIp() , cronExpressTask , CreateMode.EPHEMERAL);
                createNotify(jobKeyPath);
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
                    resultStr = stringACLBackgroundPathAndBytesable.forPath(path);
                } else {
                    resultStr = stringACLBackgroundPathAndBytesable.forPath(path, this.zookeeperSerializer.serializer(cronExpressTask));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("create path failed , path : %s" , path) , e);
        }
        return resultStr;
    }
}

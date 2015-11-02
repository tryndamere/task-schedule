package com.hyxt.schedule.client.config;

import com.hyxt.boot.autoconfigure.ZookeeperConstants;
import com.hyxt.boot.autoconfigure.ZookeeperOperation;
import com.hyxt.boot.autoconfigure.serializer.ZookeeperSerializer;
import com.hyxt.schedule.common.config.CronExpressTask;
import com.hyxt.schedule.common.config.TaskExecutorStateEnum;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rocky on 15/10/17.
 */
public class TaskRegistrar implements InitializingBean, DisposableBean {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private String application;

    private String owner;

    private List<CronExpressTask> cronExpressTasks;

    private ZookeeperOperation zookeeperOperation;

    private ZookeeperSerializer zookeeperSerializer;

    private Map<String, Runnable> runnableMap;

    private List<NodeCache> nodeCaches;

    public void setApplication(String application) {
        this.application = application;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setZookeeperSerializer(ZookeeperSerializer zookeeperSerializer) {
        this.zookeeperSerializer = zookeeperSerializer;
    }

    public void setZookeeperOperation(ZookeeperOperation zookeeperOperation) {
        this.zookeeperOperation = zookeeperOperation;
    }

    public ZookeeperOperation getZookeeperOperation() {
        return zookeeperOperation;
    }

    public void addCronExpressTask(CronExpressTask cronExpressTask) {
        if (this.cronExpressTasks == null) {
            this.cronExpressTasks = new ArrayList<CronExpressTask>(1);
        }
        this.cronExpressTasks.add(cronExpressTask);
    }

    public void addTaskRunnable(Runnable runnable, String key) {
        if (runnableMap == null) {
            this.runnableMap = new ConcurrentHashMap<String, Runnable>(10);
        }
        this.runnableMap.put(key, runnable);
    }

    public void destroy() throws Exception {
        this.zookeeperOperation.close();
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
                String applicationPath = this.zookeeperOperation.createPersistentWithValidator(
                        this.spellPath(ZookeeperConstants.ZK_SCHEDULE_JOB , cronExpressTask.getApplication()));
                String jobKeyPath = this.zookeeperOperation.createPersistent(this.spellPath(applicationPath , cronExpressTask.getKey()) ,
                        this.zookeeperSerializer.serializer(cronExpressTask));
                String executePath = this.zookeeperOperation.createEphemeral(this.spellPath(jobKeyPath, cronExpressTask.getIp()));
                createNotify(executePath);
            }
        }
    }

    private String spellPath(String ...paths) {
        Assert.notNull(paths, "paths must be not null");
        StringBuffer sb = new StringBuffer(20);
        for (String path : paths) {
            sb.append(path).append("/");
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }

    private void createConnectionStateListener() {
        this.zookeeperOperation.getCuratorFramework().getConnectionStateListenable().addListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.SUSPENDED || newState == ConnectionState.LOST) {
                    destroyNodeCaches();
                    scheduleTasks();
                }
            }
        });
    }

    private void createNotify(final String executorPath) {
        final NodeCache nodeCache = new NodeCache(this.zookeeperOperation.getCuratorFramework(), executorPath);
        try {
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    CronExpressTask cronExpressTask = TaskRegistrar.this.zookeeperSerializer.deserializer(TaskRegistrar.this.zookeeperOperation
                            .getParentData(executorPath), CronExpressTask.class);
                    String deserializer = nodeCache.getCurrentData().getData() == null ? "" :
                            TaskRegistrar.this.zookeeperSerializer.deserializer(nodeCache.getCurrentData().getData());
                    LOGGER.info("notify client , data : {}", deserializer);
                    InterProcessMutex interProcessMutex = !cronExpressTask.isConcurrent()?
                            new InterProcessMutex(TaskRegistrar.this.zookeeperOperation.getCuratorFramework() , "") : null;
                    if (interProcessMutex != null) {
                        interProcessMutex.acquire();
                    }
                    if (deserializer.equals(TaskExecutorStateEnum.DOING)) {
                        LOGGER.info("notify client , executor : {}", deserializer);
                        runnableMap.get(cronExpressTask.getKey()).run();
                    }
                    if (interProcessMutex != null) {
                        interProcessMutex.release();
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
            throw new RuntimeException("nodeCache create error!" , e);
        }
    }

    private void destroyNodeCaches() {
        if (nodeCaches != null && nodeCaches.size() > 0) {
            for (NodeCache nodeCache : nodeCaches) {
                try {
                    nodeCache.close();
                } catch (IOException e) {
                    throw new RuntimeException("destroy nodeCaches error!" , e);
                }
            }
        }
    }

}

package com.hyxt.boot.autoconfigure.zookeeper;

import com.hyxt.boot.autoconfigure.ZookeeperConstants;
import com.hyxt.boot.autoconfigure.ZookeeperOperation;
import com.hyxt.boot.autoconfigure.ZookeeperOperationImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rocky on 2015/10/12.
 */
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
@ConditionalOnProperty(prefix = "hyxt.zookeeper", name = "connectionString")
public class ZookeeperAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperAutoConfiguration.class);

    @Bean(name = "curatorFramework")
    @ConditionalOnClass({CuratorFramework.class, CuratorFrameworkFactory.class})
    public CuratorFramework createCuratorFramework(ZookeeperProperties zookeeperProperties) throws InterruptedException {
        LOGGER.info("curator init paramters , connectString : {} , connectionTimeout : {} , sessionTimeout : {}", zookeeperProperties.getConnectionString()
                , zookeeperProperties.getConnectionTimeoutMs(), zookeeperProperties.getSessionTimeoutMs());

        BoundedExponentialBackoffRetry boundedExponentialBackoffRetry = new BoundedExponentialBackoffRetry(zookeeperProperties.getBaseSleepTimeMs(),
                zookeeperProperties.getMaxSleepTimeMs(), zookeeperProperties.getMaxRetryCount());

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(zookeeperProperties.getConnectionString())
//                .namespace(ZookeeperConstants.ZK_SCHEDULE_ROOT)
                .sessionTimeoutMs(zookeeperProperties.getSessionTimeoutMs())
                .connectionTimeoutMs(zookeeperProperties.getConnectionTimeoutMs())
                .retryPolicy(boundedExponentialBackoffRetry)
                .maxCloseWaitMs(zookeeperProperties.getMaxCloseWaitMs())
                .build();
        curatorFramework.start();

        if (zookeeperProperties.isBlockUntilConnectedOrTimedOut()) {
            curatorFramework.blockUntilConnected();
        }

        initRoot(curatorFramework);
        LOGGER.info("curatorFramework is started successful");
        return curatorFramework;
    }

    private void initRoot(CuratorFramework curatorFramework) throws RuntimeException {
        try {
            Stat stat = curatorFramework.checkExists().forPath(ZookeeperConstants.ZK_SCHEDULE_ROOT);
            if (stat == null) {
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(ZookeeperConstants.ZK_SCHEDULE_ROOT);
            }
        } catch (Exception e) {
            throw new RuntimeException("root init failed", e);
        }
    }

    @Bean
    public ZookeeperOperation createZookeeperOperation(CuratorFramework curatorFramework) {
        return new ZookeeperOperationImpl(curatorFramework);
    }


}

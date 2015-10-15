package com.hyxt.boot.autoconfigure.zookeeper;

import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rocky on 2015/10/12.
 */
@Configuration
@ConditionalOnClass(CuratorFrameworkFactory.class)
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperAutoConfiguration {

    @Autowired
    ZookeeperProperties zookeeperProperties;



}

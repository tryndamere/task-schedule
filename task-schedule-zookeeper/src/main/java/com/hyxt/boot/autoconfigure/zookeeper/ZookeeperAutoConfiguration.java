package com.hyxt.boot.autoconfigure.zookeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rocky on 2015/10/12.
 */
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
@ConditionalOnProperty(prefix = "hyxt.zookeeper", name = "connectionString" , matchIfMissing = false)
public class ZookeeperAutoConfiguration {

    @Autowired
    private ZookeeperProperties zookeeperProperties;



}

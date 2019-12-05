package com.yanerbo.datatransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * 
 * @author jihaibo
 *
 */
@Configuration
public class RegCenterConfig {

	
	/**
     * 配置zookeeper
     * @param serverList
     * @param namespace
     * @return
     */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(
            @Value("${zk.serverList}") final String serverList,
            @Value("${zk.namespace}") final String namespace) {
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverList, namespace));
    }
}

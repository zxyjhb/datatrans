package com.yanerbo.datatransfer.console.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.yanerbo.datatransfer.console.consumer.DataTransConcurrentlyConsumer;
import com.yanerbo.datatransfer.console.consumer.DataTransConcurrentlyProducer;

/**
 * MQ配置
 * @author 274818
 *
 */
//@Configuration
public class MQConfig {

	@Value("${rocketmq.namesrvAddr}")
	private String namesrvAddr;
		
	@Value("${rocketmq.producer.group}")
	private String producerGroup;

	@Value("${rocketmq.consumer.group}")
	private String consumerGroup;
	
	@Value("${rocketmq.topic}")
	private String topic;
	
	@Value("${rocketmq.tag}")
	private String tag;
	
	@Bean(initMethod = "startup", destroyMethod = "shutdown")
	public DataTransConcurrentlyConsumer getDataTransConcurrentlyConsumer() {
		return new DataTransConcurrentlyConsumer(namesrvAddr, consumerGroup, topic, tag);
	}
	
	@Bean(initMethod = "startup", destroyMethod = "shutdown")
	public DataTransConcurrentlyProducer getDataTransConcurrentlyProducer() {
		return new DataTransConcurrentlyProducer(namesrvAddr, producerGroup, topic, tag);
	}
}

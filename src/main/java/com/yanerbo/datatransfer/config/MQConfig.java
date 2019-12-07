package com.yanerbo.datatransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * MQ配置
 * @author 274818
 *
 */
@Configuration
public class MQConfig {

	@Value("${mq.namesrvAddr}")
	private String namesrvAddr;
		
	@Value("${mq.producerGroup}")
	private String producerGroup;

	@Value("${mq.consumerGroup}")
	private String consumerGroup;
	
	@Value("${mq.topic}")
	private String topic;
	
	@Value("${mq.tag}")
	private String tag;
}

package com.yanerbo.datatransfer.consumer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.yanerbo.datatransfer.shared.domain.ErrorCode;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;

/**
 * 增量数据发送
 * @author 274818
 *
 */
public class DataTransConcurrentlyProducer{
	
	public static Logger logger = LoggerFactory.getLogger(DataTransConcurrentlyProducer.class);
		
	protected DefaultMQProducer producer = new DefaultMQProducer();
	
	private String namesrvAddr;
	
	private String producerGroup;
	
	private String topic;
	
	private String tag;
	
	public String getNamesrvAddr() {
		return namesrvAddr;
	}
	
	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public DataTransConcurrentlyProducer(String namesrvAddr, String producerGroup, String topic, String tag) {
		this.namesrvAddr = namesrvAddr;
		this.producerGroup = producerGroup;
		this.topic = topic;
		this.tag = tag;
	}
	/**
	 * 开启生产者
	 */
	public void startup() {
//		producer.setInstanceName(instanceName);
		// 多个namesrvAddr多个地址之间用;分隔，如：10.230.20.224:8765;10.230.20.225:8765
		producer.setNamesrvAddr(namesrvAddr);
		// 指定一个生产者名，如不指定，则随机生成，随机生成则有可能影响消息接续消费
		producer.setProducerGroup(producerGroup);
		// 对消息发送失败，重试次数，默认为3。不要求重试，可设定为0，如果网络抖动，容易造成消息丢失。请根据实际情况调整
		producer.setRetryTimesWhenSendFailed(0);
		// 设置消费发送最大值 最大512
		producer.setMaxMessageSize(1024 * 512);
		try {
			producer.start();
		} catch (Throwable e) {
			logger.error("DataTransConcurrentlyProducer start fail,", e);
		}
	}
	/**
	 * 停止生产者
	 */
	public void shutdown() {
		producer.shutdown();
	}
	/**
	 * 发送消息
	 * @param key
	 * @param message
	 * @return
	 */
	public String send(String key, Object message) {
		try{
			Message msg = new Message(topic, tag, key, JSONObject.toJSONString(message).getBytes());
			SendResult result = producer.send(msg);
			return result.getMsgId();
		}catch(Exception e){
			throw new DataTransRuntimeException(ErrorCode.ERR004, e);
		}
	}
}

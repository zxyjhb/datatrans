package com.yanerbo.datatransfer.consumer;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanerbo.datatransfer.server.manager.IDataTransManager;

/**
 * 增量数据接收监听
 * @author 274818
 *
 */
public class DataTransConcurrentlyConsumer implements MessageListenerConcurrently{
	
	public static Logger logger = LoggerFactory.getLogger(DataTransConcurrentlyConsumer.class);
	
	private DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
	
	private String namesrvAddr;
	
	private String consumerGroup;
	
	private String topic;
	
	private String tag;
	
	private IDataTransManager dataTransManager;
	/**
	 * 初始化配置
	 * @param namesrvAddr
	 * @param consumerGroup
	 * @param topic
	 * @param tag
	 */
	public DataTransConcurrentlyConsumer(String namesrvAddr, String consumerGroup, String topic, String tag) {
		this.namesrvAddr = namesrvAddr;
		this.consumerGroup = consumerGroup;
		this.topic = topic;
		this.tag = tag;
	}

	/**
	 * 停止消费者监听
	 */
	public void shutdown() {
		consumer.shutdown();
	}

	/**
	 * 启动消费者监听
	 * @throws Exception
	 */
	public void startup() throws Exception{
		// 多个namesrvAddr多个地址之间用;分隔，如：10.230.20.224:8765;10.230.20.225:8765
		consumer.setNamesrvAddr(namesrvAddr);
		// 一定要设置消费者组名,如不指定，则随机生成，随机生成则有可能影响消息接续消费
		consumer.setConsumerGroup(consumerGroup);
		// 设置消费点位置，CONSUME_FROM_LAST_OFFSET：新加入broker的消费者，从队尾开始消费，历史的消息将不进行消费。
		// CONSUME_FROM_FIRST_OFFSET:新加入broker的消费者，从队首开始消费，历史的消息将全部推送过来，容易造成消息重复消费，默认是CONSUME_FROM_FIRST_OFFSET
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		// 为*表示消费所有的tag，请根据业务实际消费情况，设置tag，
		consumer.subscribe(topic, tag);
		consumer.setMessageListener(this);
		// 消息消费类型，默认为CLUSTERING，即相同ConsumerGroup的多个消费者，默认随机消费同topic下的消息。
		consumer.setMessageModel(MessageModel.CLUSTERING);
		//线程池扩容最大值，默认64 请根据业务实际情况，调整消费线程数，
		consumer.setConsumeThreadMax(64);
		//线程池初始化最小值，默认20 请根据业务实际情况，调整消费线程数，
		consumer.setConsumeThreadMin(20);
		// 拉消息批次大小，默认为32
		consumer.setPullBatchSize(32);
		// 消费消息批次大小，默认为1
		consumer.setConsumeMessageBatchMaxSize(1);
		try {
			consumer.start();
		} catch (Throwable e) {
			logger.error("DataTransConcurrentlyConsumer start fail,", e);
		}
	}

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		for(MessageExt msg: msgs){
			System.out.println("add trans: " + msg);
			dataTransManager.addTrans(msg.getTags(), msg.getBody());
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}

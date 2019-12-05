package com.yanerbo.datatransfer.support;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.config.DataTransConfig;
import com.yanerbo.datatransfer.entity.DataTransEntity;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.job.DataTransJob;


/**
 * 定时任务配置
 * 
 * @author jihaibo
 *
 */
@Component
public class ElasticJobConfigSupport implements InitializingBean{
	
	/**
	 * 日志
	 */
	private Logger log = Logger.getLogger(ElasticJobConfigSupport.class);
	/**
	 * 已注册job
	 */
	private static final Map<String, String> JOB_CONFIG_MAP = new HashMap<String, String>();
	/**
	 * 注册中心配置
	 */
	@Resource
	private ZookeeperRegistryCenter zookeeperRegistryCenter;
	/**
	 * 数据传输job配置
	 */
	@Resource
	private DataTransConfig dataTransConfig;
	
	/**
	 * job配置
	 */
	@Resource
	private DataTransJob dataTransJob;
	/**
	 * job配置
	 */
	@Resource
	private JobEventConfiguration jobEventConfiguration;

	/**
	 * 根据jobName获取配置
	 * 
	 * @param jobName
	 * @return
	 */
	public static String getJobConfig(String jobName) {
		return JOB_CONFIG_MAP.get(jobName);
	}

	/**
	 * 更改jobConfig
	 * 
	 * @param jobName
	 * @param jobConfig
	 */
	public static void setJobConfig(String jobName, String jobConfig) {
		JOB_CONFIG_MAP.put(jobName, jobConfig);
	}
	/**
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		for (final DataTransEntity entity : dataTransConfig.getSchedules()) {
			try {
				if("none".equals(entity.getMode())) {
					log.info("初始化定时任务 ：{ "+ entity.toString()+" } 模式为none，不用启动");
					continue;
				}
	 			SpringJobScheduler jobScheduler = jobScheduler(dataTransJob, entity);
	 			jobScheduler.init();
	 			log.info("初始化定时任务 ：{ "+ entity.toString()+" } ");
	 		} catch (Exception e) {
	 			log.error("注册Job出错：{ " + entity.toString() + "} ", e);
	 		}
		}
	}
		
		
	/**
	 * 注册SpringJobScheduler
	 * 
	 * @param elasticJob
	 * @param jobClass
	 * @param elasticJobConfigBean
	 * @return
	 */
	private SpringJobScheduler jobScheduler(ElasticJob elasticJob, DataTransEntity entity) {
		LiteJobConfiguration build = LiteJobConfiguration.newBuilder(jobConfiguration(elasticJob, entity))
				.overwrite(true).build();
		SpringJobScheduler springJobScheduler = new SpringJobScheduler(elasticJob, zookeeperRegistryCenter, build,
				jobEventConfiguration);
		return springJobScheduler;
	}

	/**
	 * job配置
	 * 
	 * @param elasticJob
	 * @param elasticJobConfigBean
	 * @return
	 */
	private JobTypeConfiguration jobConfiguration(final ElasticJob elasticJob, DataTransEntity entity) {
		JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
				.newBuilder(entity.getName(), entity.getCron(), entity.getShardingTotalCount())
				.shardingItemParameters(entity.getShardingItemParameters())
				.build();
		if (elasticJob instanceof SimpleJob) {
			return new SimpleJobConfiguration(jobCoreConfiguration, elasticJob.getClass().getCanonicalName());
		}
		throw new DataTransRuntimeException("未知类型定时任务：" + elasticJob.getClass().getName());
	}

}

package com.yanerbo.datatransfer.support;

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
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.RunType;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.job.DataTransJob;
import com.yanerbo.datatransfer.server.dao.impl.DataTransDao;
import com.yanerbo.datatransfer.support.util.DataTransContext;


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
	
	@Resource
	private DataTransDao dataTransDao;
	/**
	 * job运行事件配置（其实暂时不需要）
	 */
	@Resource
	private JobEventConfiguration jobEventConfiguration;

	
	/**
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		for (final DataTrans entity : dataTransConfig.getSchedules()) {
			try {
				if(RunType.none.name().equals(entity.getMode())) {
					log.info("初始化定时任务 ：{ "+ entity.toString()+" } 模式为none，不用启动");
					continue;
				}
	 			SpringJobScheduler jobScheduler = jobScheduler(dataTransJob, entity);
	 			DataTransContext.setJobConfig(entity.getName(), jobScheduler);
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
	private SpringJobScheduler jobScheduler(ElasticJob elasticJob, DataTrans entity) {
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
	private JobTypeConfiguration jobConfiguration(final ElasticJob elasticJob, DataTrans entity) {
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

package com.yanerbo.datatransfer.support;

import javax.annotation.Resource;

import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
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
import com.dangdang.ddframe.job.lite.internal.election.LeaderService;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.config.DataTransConfig;
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.entity.RunType;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.job.DataTransJob;
import com.yanerbo.datatransfer.server.dao.impl.DataTransDao;
import com.yanerbo.datatransfer.support.util.DataTransContext;
import com.yanerbo.datatransfer.support.util.SqlUtil;


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
	 * 清除数据锁
	 */
	private static final String CLEARLOCK = "/%s/clearLock";
	
	/**
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		for (final DataTrans entity : dataTransConfig.getDataTransConfigs()) {
			try {
				if(RunType.none.name().equals(entity.getMode())) {
					log.info("初始化定时任务 ：{ "+ entity.toString()+" } 模式为none，不用启动");
					continue;
				}
	 			SpringJobScheduler jobScheduler = jobScheduler(dataTransJob, entity);
	 			DataTransContext.setJobConfig(entity.getName(), jobScheduler);
	 			jobScheduler.init();
	 			clearTargetData(entity);
	 			log.info("初始化定时任务 ：{ "+ entity.toString()+" } ");
	 		} catch (Exception e) {
	 			log.error("注册Job出错：{ " + entity.toString() + "} ", e);
	 		}
		}
	}
		
	
	/**
	 * 清空目标表数据（全量初始化时使用）
	 * @param dataTrans
	 * @throws Exception
	 */
	private void clearTargetData(DataTrans dataTrans) throws Exception{
		
		if(RunType.init.name().equals(dataTrans.getMode())){
			String lockKey = String.format(CLEARLOCK, dataTrans.getName());
			//主节点
			LeaderService leaderService = new LeaderService(zookeeperRegistryCenter, dataTrans.getName());
			//使用zk排他锁（这里主要是在删除的时候 其他节点也阻塞一下）
			InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(zookeeperRegistryCenter.getClient(), lockKey);
			try{
				//调用该方法后，会一直堵塞，直到抢夺到锁资源，或者zookeeper连接中断后，上抛异常
				lock.acquire();
				//主节点去删除数据（不需要每个节点都去操作）
				if(leaderService.isLeader()){
					//清空目标表数据
					log.info("【clear data】");
					dataTransDao.delete(DataType.target, SqlUtil.delete(dataTrans.getTargetTable()));
				}
			}finally{
				//释放锁
				lock.release();
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

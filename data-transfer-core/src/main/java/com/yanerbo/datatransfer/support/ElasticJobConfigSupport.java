package com.yanerbo.datatransfer.support;

import javax.annotation.Resource;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Charsets;
import com.yanerbo.datatransfer.shared.domain.DataTrans;
import com.yanerbo.datatransfer.shared.util.Constant;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.job.DataTransJob;
import com.yanerbo.datatransfer.support.util.DataTransContext;


/**
 * 定时任务配置(基于zk实现分布式配置中心)
 * 定时任务采用elasticJob进行分片处理
 * 
 * @author jihaibo
 *
 */
@Component
public class ElasticJobConfigSupport implements InitializingBean, Constant{
	
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
	 * job配置
	 */
	@Resource
	private DataTransJob dataTransJob;
	
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
		
		//监听config目录
		for(String key : zookeeperRegistryCenter.getChildrenKeys(CONFIG_ROOT)) {
			zookeeperRegistryCenter.addCacheData(String.format(CONFIG_PATH, key));
			TreeCache cache = (TreeCache) zookeeperRegistryCenter.getRawCache(String.format(CONFIG_PATH, key));
			cache.getListenable().addListener(treeCacheListener);
		}
	}
	
	/**
	 * job注册
	 * @param dataTrans
	 */
	private void register(DataTrans dataTrans) {
		try {
 			SpringJobScheduler jobScheduler = jobScheduler(dataTransJob, dataTrans);
 			DataTransContext.setJobConfig(dataTrans.getName(), jobScheduler);
 			DataTransContext.setDataTrans(dataTrans.getName(), dataTrans);
 			jobScheduler.init();
 			log.info("初始化定时任务 ：{ "+ dataTrans.toString()+" } ");
 		} catch (Exception e) {
 			log.error("注册Job出错：{ " + dataTrans.toString() + "} ", e);
 		}
	}
	
	/**
	 * job注销
	 * @param dataTrans
	 */
	private void unRegister(DataTrans dataTrans) {
		try {
			
 			SpringJobScheduler jobScheduler = DataTransContext.getJobConfig(dataTrans.getName());
 			jobScheduler.getSchedulerFacade().shutdownInstance();
 			DataTransContext.setJobConfig(dataTrans.getName(), jobScheduler);
 			DataTransContext.removeDataTrans(dataTrans.getName());
 			log.info("注销定时任务 ：{ "+ dataTrans.toString()+" } ");
 		} catch (Exception e) {
 			log.error("注销Job出错：{ " + dataTrans.toString() + "} ", e);
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
	
	
	TreeCacheListener treeCacheListener = new TreeCacheListener() {
		
        @Override
        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        	
            switch (event.getType()) {
                case NODE_ADDED:{
                	DataTrans dataTrans = JSONObject.parseObject(new String(client.getData().forPath(event.getData().getPath()), Charsets.UTF_8), DataTrans.class);
                	log.info("add node: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                	register(dataTrans);
                	break;
                }
                case NODE_REMOVED:{
                	DataTrans dataTrans = JSONObject.parseObject(new String(client.getData().forPath(event.getData().getPath()), Charsets.UTF_8), DataTrans.class);
                	log.info("remove node: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                	unRegister(dataTrans);
                	break;
                }
                case NODE_UPDATED:{
                	DataTrans dataTrans = JSONObject.parseObject(new String(client.getData().forPath(event.getData().getPath()), Charsets.UTF_8), DataTrans.class);
                	DataTransContext.setDataTrans(ZKPaths.getNodeFromPath(event.getData().getPath()), dataTrans);
                	log.info("update node: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                	break;
                }
			default:
				break;

            }
        }
    };

}

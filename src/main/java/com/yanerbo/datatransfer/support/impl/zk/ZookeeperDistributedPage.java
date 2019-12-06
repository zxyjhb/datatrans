package com.yanerbo.datatransfer.support.impl.zk;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.config.DataTransConfig;
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.server.dao.impl.DataTransDao;
import com.yanerbo.datatransfer.support.impl.IDistributedPage;
import com.yanerbo.datatransfer.support.util.SqlUtil;

/**
 * 
 * @author 274818
 *
 */
@Component
public class ZookeeperDistributedPage implements IDistributedPage{

	/**
	 * 日志
	 */
	private final static Logger log = LoggerFactory.getLogger(ZookeeperDistributedPage.class);
	/**
	 * zk当前页
	 */
	private static final String CURRENTPAGE = "/%s/%s/currentPage";
	/**
	 * zk总行数（当前运行时最大ID）
	 */
	private static final String TOTALCOUNT = "/%s/%s/totalCount";
	/**
	 * 列表
	 */
	private static final Map<String, DistributedAtomicInteger> atomicIntegerMap = new HashMap<String, DistributedAtomicInteger>();
	
	@Autowired
	@Qualifier("zookeeperRegistryCenter")
	private ZookeeperRegistryCenter regCenter;
	
	/**
	 * 数据传输job配置
	 */
	@Resource
	private DataTransConfig dataTransConfig;
	
	@Autowired
	private DataTransDao dataTransDao;
	
	/**
	 * 获取当前总页数
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	@Override
	public int getTotalCount(String jobName, int shardingItem, int shardingTotal){
		
		String key = String.format(TOTALCOUNT, jobName, shardingItem);
		DataTrans dataTrans = dataTransConfig.getDataTrans(jobName);
		DistributedAtomicInteger totalCount = getAtomicInteger(key);
		try{
			if(totalCount.get().postValue() == 0){
				//获取分片当前页
				int count = dataTransDao.count(DataType.source, SqlUtil.getAllCount(dataTrans.getSourceTable(), dataTrans.getSourceKey(), shardingItem, shardingTotal));
				totalCount.add(count);
			}
			return totalCount.get().postValue();
		}catch(Exception e){
			log.error("getTotalCount fail ",e);
		}
		return -1;
	}
	
	
	/**
	 * 获取当前页数
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	@Override
	public int getCurrentPage(String jobName, int shardingItem, int shardingTotal){
		
		String key = String.format(CURRENTPAGE, jobName, shardingItem);
		try {
			return getAtomicInteger(key).increment().postValue();
		}catch(Exception e) {
			log.error("getCurrentPage fail ",e);
		}
		return -1;
	}
	
	
	private DistributedAtomicInteger getAtomicInteger(String key){
		
		if(atomicIntegerMap.containsKey(key)){
			return atomicIntegerMap.get(key);
		}else{
			DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(regCenter.getClient(), key, new RetryNTimes(3, 1000));
			atomicIntegerMap.put(key, atomicInteger);
			return atomicInteger;
		}
	}
}

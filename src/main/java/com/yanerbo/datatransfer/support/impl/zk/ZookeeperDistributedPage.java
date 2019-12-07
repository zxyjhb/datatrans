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
import com.yanerbo.datatransfer.entity.Page;
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
	 * 默认
	 */
	private static final int DEFAULT = 0;
	/**
	 * zk当前页
	 */
	private static final String CURRENTPAGE = "/%s/%s/currentPage";
	/**
	 * zk当前起始位置
	 */
	private static final String PAGESTART = "/%s/%s/pageStart";
	/**
	 * zk数据操作列表
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
	/**
	 * 
	 */
	@Autowired
	private DataTransDao dataTransDao;
	
	
	/**
	 * 获取当前总页数
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	@Override
	public Page pageInfo(String jobName, int shardingItem, int shardingTotal){
		
		String key = String.format(PAGESTART, jobName, shardingItem);
		//赋值
		DataTrans dataTrans = dataTransConfig.getDataTrans(jobName);
		return pageInfoByPost(shardingItem, shardingTotal, key, dataTrans);
	}
	
	/**
	 * 按顺序
	 * @param shardingItem
	 * @param shardingTotal
	 * @param key
	 * @param dataTrans
	 * @return
	 */
	private Page pageInfoBySeq(int shardingItem, int shardingTotal, String key, DataTrans dataTrans) {
		//获取分片当前页（这里不需要分布式锁，本地锁就够了）
		synchronized (this) {
			try{
				//获取当前页
				DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
				int pageStart = atomicInteger.get().postValue();
				//查询当前页信息
				Page page = dataTransDao.pageInfo(DataType.source, SqlUtil.getPagePost(dataTrans.getSourceTable(), dataTrans.getSourceKey(), shardingItem, shardingTotal,pageStart ,dataTrans.getPageCount()));
				//当前结束值，作为下一个开始值
				atomicInteger.forceSet(page.getPageEnd());
				return page;
				
			}catch(Exception e) {
				log.error("pageInfo fail ", e);
			}
		}
		return null;
	}
	/**
	 * 按起始位置
	 * @param shardingItem
	 * @param shardingTotal
	 * @param key
	 * @param dataTrans
	 * @return
	 */
	private Page pageInfoByPost(int shardingItem, int shardingTotal, String key, DataTrans dataTrans) {
		//获取分片当前页（这里不需要分布式锁，本地锁就够了）
		synchronized (this) {
			try{
				//获取当前页
				DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
				int pageStart = atomicInteger.get().postValue();
				//查询当前页信息
				Page page = dataTransDao.pageInfo(DataType.source, SqlUtil.getPagePost(dataTrans.getSourceTable(), dataTrans.getSourceKey(), shardingItem, shardingTotal,pageStart ,dataTrans.getPageCount()));
				//当前结束值，作为下一个开始值
				atomicInteger.forceSet(page.getPageEnd());
				return page;
				
			}catch(Exception e) {
				log.error("pageInfo fail ", e);
			}
		}
		return null;
	}

	/**
	 * 将数据保存到zk
	 * @param key
	 * @param data
	 * @return
	 */
	public int getData(String key){
		try {
			return getAtomicInteger(key).get().postValue();
		}catch(Exception e) {
			log.error("setData fail ",e);
		}
		return -1;
	}
	/**
	 * 将数据保存到zk
	 * @param key
	 * @param data
	 * @return
	 */
	public int setData(String key, int data){
		DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
		try {
			if(atomicInteger.get().postValue() == DEFAULT){
				atomicInteger.add(data);
			}
			return atomicInteger.get().postValue();
		}catch(Exception e) {
			log.error("setData fail ",e);
		}
		return -1;
	}
	/**
	 * (这里因为是job去跑，理论上是没有锁的问题)
	 * @param key
	 * @return
	 */
	private DistributedAtomicInteger getAtomicInteger(String key){
		
		//不存在，则初始化
		if(!atomicIntegerMap.containsKey(key)){
			synchronized (atomicIntegerMap) {
				DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(regCenter.getClient(), key, new RetryNTimes(3, 1000));
				try{
					//初始化的时候强制归零
					atomicInteger.forceSet(DEFAULT);
				}catch(Exception e) {
					log.error("atomicInteger initialize fail ", e);
				}
				atomicIntegerMap.put(key, atomicInteger);
			}
		}
		return atomicIntegerMap.get(key);
	}
}

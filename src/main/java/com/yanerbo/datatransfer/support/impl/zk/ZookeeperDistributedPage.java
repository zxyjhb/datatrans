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
	private static final String PAGE = "/%s/%s/page";
	/**
	 * zk当前页
	 */
	private static final String CURRENTPAGE = "/%s/%s/currentPage";
	/**
	 * zk当前起始位置
	 */
	private static final String STARTPOSTPAGE = "/%s/%s/startPostPage";
	/**
	 * zk当前结束位置
	 */
	private static final String ENDPOSTPAGE = "/%s/%s/endPostPage";
	
	/**
	 * zk总行数（当前运行时最大ID）
	 */
	private static final String TOTALCOUNT = "/%s/%s/totalCount";
	/**
	 * 列表
	 */
	private static final Map<String, DistributedAtomicInteger> atomicIntegerMap = new HashMap<String, DistributedAtomicInteger>();
	/**
	 * 本地分页列表(这里将分页信息保存在本地)
	 */
	private static final Map<String, Page> pageMap = new HashMap<String, Page>();
	
	
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
	public Page getPage(String jobName, int shardingItem, int shardingTotal){
		
		Page page = null;
		String key = String.format(PAGE, jobName, shardingItem);
		//如果已经存在分页信息
		if(pageMap.containsKey(key)){
			page = pageMap.get(key);
		}
		//没有分页信息，从数据库查询
		else{
			//
			DataTrans dataTrans = dataTransConfig.getDataTrans(jobName);
			//获取分片当前页
			System.out.println(SqlUtil.getPageInfo(dataTrans.getSourceTable(), dataTrans.getSourceKey(), shardingItem, shardingTotal));
			page = dataTransDao.pageInfo(DataType.source, SqlUtil.getPageInfo(dataTrans.getSourceTable(), dataTrans.getSourceKey(), shardingItem, shardingTotal));
			//保存到zk一份
			setData(String.format(STARTPOSTPAGE, jobName, shardingItem), page.getStartPostPage());
			setData(String.format(ENDPOSTPAGE, jobName, shardingItem), page.getEndPostPage());
			setData(String.format(TOTALCOUNT, jobName, shardingItem), page.getTotalCount());
		}
		return new Page(getCurrentPage(jobName, shardingItem, shardingTotal), page.getStartPostPage(),page.getEndPostPage(), page.getTotalCount());
	}
	
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
			if(totalCount.get().postValue() == DEFAULT){
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
	 * 获取当前页数
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	@Override
	public int getCurrentPage(String jobName, int shardingItem, int shardingTotal){
		try {
			return getAtomicInteger(String.format(CURRENTPAGE, jobName, shardingItem)).increment().postValue();
		}catch(Exception e) {
			log.error("getCurrentPage fail ",e);
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
			DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(regCenter.getClient(), key, new RetryNTimes(3, 1000));
			try{
				//初始化的时候强制归零
				atomicInteger.forceSet(DEFAULT);
			}catch(Exception e) {
				log.error("atomicInteger initialize fail ", e);
			}
			atomicIntegerMap.put(key, atomicInteger);
		}
		return atomicIntegerMap.get(key);
	}
}

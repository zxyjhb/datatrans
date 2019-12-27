package com.yanerbo.datatransfer.support.impl.zk;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.lite.internal.election.LeaderService;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.config.DataTransConfig;
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.Page;
import com.yanerbo.datatransfer.entity.PageType;
import com.yanerbo.datatransfer.entity.RunType;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.entity.ErrorCode;
import com.yanerbo.datatransfer.server.dao.impl.DataTransDao;
import com.yanerbo.datatransfer.support.impl.IDistributedPage;
import com.yanerbo.datatransfer.support.util.SqlUtil;

/**
 * 
 * 这里采用zk作为分布式计数存储：
 * 其实可以做一个高可用，当zk不可用的时候，使用本地缓存进行存储
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
	private static final String CURRENTPAGE = "/%s/currentPage/%s";
	/**
	 * zk当前起始位置
	 */
	private static final String STARTPAGE = "/%s/startPage/%s";
	/**
	 * 清除数据锁
	 */
	private static final String CLEARLOCK = "/%s/clearLock";
	
	
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
	 * 这里处理并发分页
	 * 按数据分布，进行不同的分页逻辑
	 * 1、存在数字FID，且存在连续性（最大FID不大于总行数的两倍）,按页数去分页
	 * 2、存在数字FID，且存在不连续性（最大FID大于总行数的两倍）,按当前FID进行顺序分页
	 * 3、其他分页字段（可以自定义分页逻辑）
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	@Override
	public Page pageInfo(String jobName, int shardingItem, int shardingTotal){
		//赋值
		DataTrans dataTrans = dataTransConfig.getDataTrans(jobName);
		//如果是按起始位置分页
		if(PageType.post.name().equals(dataTrans.getPageType())){
			return pageInfoByPost(dataTrans, shardingItem, shardingTotal);
		}
		else if(PageType.seq.name().equals(dataTrans.getPageType())){
			return pageInfoBySeq(dataTrans, shardingItem, shardingTotal);
		}
		throw new DataTransRuntimeException(ErrorCode.ERR003);
	}
	
	/**
	 * 按顺序分页
	 * @param shardingItem
	 * @param shardingTotal
	 * @param key
	 * @param dataTrans
	 * @return
	 */
	private Page pageInfoBySeq(DataTrans dataTrans, int shardingItem, int shardingTotal) {
		
		String key = String.format(CURRENTPAGE, dataTrans.getName(), shardingItem);
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
				log.error("dataTrans: " + dataTrans + " pageInfoBySeq fail ", e);
			}
		}
		return null;
	}
	/**
	 * 按起始位置（每次去获取当前执行的位置）
	 * @param shardingItem
	 * @param shardingTotal
	 * @param key
	 * @param dataTrans
	 * @return
	 */
	private Page pageInfoByPost(DataTrans dataTrans, int shardingItem, int shardingTotal) {
		
		String key = String.format(STARTPAGE, dataTrans.getName(), shardingItem);
		//获取分片当前页（这里不需要分布式锁，本地锁就够了）
		synchronized (this) {
			try{
				//获取当前页
				DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
				int pageStart = atomicInteger.get().postValue();
				//查询当前页信息
				Page page = dataTransDao.pageInfo(DataType.source, SqlUtil.getPagePost(dataTrans.getSourceTable(), dataTrans.getSourceKey(), shardingItem, shardingTotal,pageStart ,dataTrans.getPageCount()));
				//如果开始和结束位置为同一个了，那么说明分页搞完了
				if(page.getPageStart() == page.getPageEnd()){
					atomicInteger.forceSet(DEFAULT);
				}else{
					//当前结束值，作为下一个开始值
					atomicInteger.compareAndSet(pageStart, page.getPageEnd());
				}
				return page;
			}catch(Exception e) {
				log.error("dataTrans: " + dataTrans + " pageInfoByPost fail ", e);
			}
		}
		return null;
	}

	
	/**
	 * 清空目标表数据（全量时使用）
	 * @param dataTrans
	 * @throws Exception
	 */
	private void clearTargetData(DataTrans dataTrans) throws Exception{
		
		if(RunType.all.name().equals(dataTrans.getMode())){
			String lockKey = String.format(CLEARLOCK, dataTrans.getName());
			//主节点
			LeaderService leaderService = new LeaderService(regCenter, dataTrans.getName());
			//使用zk排他锁（这里主要是在删除的时候 其他节点也阻塞一下）
			InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(regCenter.getClient(), lockKey);
			try{
				//调用该方法后，会一直堵塞，直到抢夺到锁资源，或者zookeeper连接中断后，上抛异常
				lock.acquire();
				//主节点去删除数据（不需要每个节点都去操作）
				if(leaderService.isLeader()){
					//清空目标表数据
					log.info("clear data");
					dataTransDao.delete(DataType.target, SqlUtil.delete(dataTrans.getTargetTable()));
				}
			}finally{
				//释放锁
				lock.release();
			}
		}
	}
	
	/**
	 * 是否重新计数
	 * @param key
	 * @param initialize
	 * @return
	 */
	private DistributedAtomicInteger getAtomicInteger(String key, boolean initialize){
		
		//不存在，则初始化
		if(!atomicIntegerMap.containsKey(key)){
			synchronized (this) {
				DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(regCenter.getClient(), key, new RetryNTimes(3, 1000));
				try{
					//初始化的时候强制归零
					if(initialize){
						log.info("key: " + key + " atomicInteger initialize " + DEFAULT);
						atomicInteger.forceSet(DEFAULT);
//						clearTargetData(dataTransConfig.getLikeDataTrans(key));
					}
				}catch(Exception e) {
					log.error("key: " + key + " atomicInteger initialize fail ", e);
				}
				atomicIntegerMap.put(key, atomicInteger);
			}
		}
		return atomicIntegerMap.get(key);
	}
	
	/**
	 * (这里因为是job去跑，理论上是没有锁的问题)
	 * @param key
	 * @return
	 */
	private DistributedAtomicInteger getAtomicInteger(String key){
		return getAtomicInteger(key, true);
	}
}

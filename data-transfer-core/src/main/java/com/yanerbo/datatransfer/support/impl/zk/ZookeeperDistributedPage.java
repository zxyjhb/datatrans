package com.yanerbo.datatransfer.support.impl.zk;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.config.DataTransConfig;
import com.yanerbo.datatransfer.shared.domain.DataTrans;
import com.yanerbo.datatransfer.shared.domain.Page;
import com.yanerbo.datatransfer.shared.domain.PageType;
import com.yanerbo.datatransfer.shared.util.Constant;
import com.yanerbo.datatransfer.shared.util.SqlUtil;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.shared.domain.DataType;
import com.yanerbo.datatransfer.shared.domain.ErrorCode;
import com.yanerbo.datatransfer.server.dao.impl.DataTransDao;
import com.yanerbo.datatransfer.support.impl.IDistributedPage;

/**
 * 
 * 这里采用zk作为分布式计数存储：
 * 其实可以做一个高可用，当zk不可用的时候，使用本地缓存进行存储
 * 
 * @author 274818
 *
 */
@Component
public class ZookeeperDistributedPage implements IDistributedPage, Constant{

	/**
	 * 日志
	 */
	private final static Logger log = LoggerFactory.getLogger(ZookeeperDistributedPage.class);
	/**
	 * zk数据操作列表（用于存储数据）
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
		//按照起始位置分页并且分片
		else if(PageType.post_sharding.name().equals(dataTrans.getPageType())){
			return pageInfoByPostSharding(dataTrans, shardingItem, shardingTotal);
		}
		//按照顺序进行分页
		else if(PageType.seq.name().equals(dataTrans.getPageType())){
			return pageInfoBySeq(dataTrans, shardingItem, shardingTotal);
		}
		//按照顺序进行分页并且分片
		else if(PageType.seq_sharding.name().equals(dataTrans.getPageType())){
			return pageInfoBySeqSharding(dataTrans, shardingItem, shardingTotal);
		}
		throw new DataTransRuntimeException(ErrorCode.ERR003);
	}
	
	/**
	 * 按顺序分页
	 * 使用
	 * @param shardingItem
	 * @param shardingTotal
	 * @param key
	 * @param dataTrans
	 * @return
	 */
	private Page pageInfoBySeq(DataTrans dataTrans, int shardingItem, int shardingTotal) {
		
		String key = String.format(SEQ_PATH, dataTrans.getName(), "no-sharding");
		try{
			//获取当前页
			DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
			atomicInteger.increment();
			Page page = new Page(atomicInteger.get().preValue(),atomicInteger.get().postValue()*dataTrans.getPageCount(), dataTrans.getPageCount());
			return page;
			
		}catch(Exception e) {
			log.error("dataTrans: " + dataTrans + " pageInfoBySeq fail ", e);
		}
		return null;
	}
	/**
	 * 按顺序分页
	 * @param shardingItem
	 * @param shardingTotal
	 * @param key
	 * @param dataTrans
	 * @return
	 */
	private Page pageInfoBySeqSharding(DataTrans dataTrans, int shardingItem, int shardingTotal) {
		
		String key = String.format(SEQ_PATH, dataTrans.getName(), shardingItem);
		try{
			//获取当前页
			DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
			atomicInteger.increment();
			Page page = new Page(atomicInteger.get().preValue(),atomicInteger.get().postValue()*dataTrans.getPageCount(), dataTrans.getPageCount());
			return page;
			
		}catch(Exception e) {
			log.error("dataTrans: " + dataTrans + " pageInfoBySeq fail ", e);
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
		
		String key = String.format(PAGE_PATH, dataTrans.getName(), "no-sharding");
		String lockkey =String.format(LOCK_PATH, dataTrans.getName());
		//获取分片当前页（这里不需要分布式锁，本地锁就够了）
		try{
			//使用zk排他锁（这里主要是在删除的时候 其他节点也阻塞一下）
			InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(regCenter.getClient(), lockkey);
			//调用该方法后，会一直堵塞，直到抢夺到锁资源，或者zookeeper连接中断后，上抛异常
			if(lock.acquire(1000, TimeUnit.MICROSECONDS)) {
				try{
					//获取当前页
					DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
					int pageStart = atomicInteger.get().postValue();
					//查询当前页信息
					Page page = dataTransDao.pageInfo(DataType.source, SqlUtil.getPagePost(dataTrans.getSourceTable(), dataTrans.getSourceKey(), pageStart ,dataTrans.getPageCount()));
					//如果开始和结束位置为同一个了，那么说明分页搞完了
					if(page.getPageStart() == page.getPageEnd()){
						log.info(key + " 分页完成！");
					}else{
						atomicInteger.forceSet(page.getPageEnd());
						log.info("pageend: " + page.getPageEnd() + ", pageStart: " + atomicInteger.get().postValue());
					}
					return page;
				}finally{
					//释放锁
					lock.release();
				}
			}
		}catch(Exception e) {
			log.error("dataTrans: " + dataTrans + " pageInfoByPost fail ", e);
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
	private Page pageInfoByPostSharding(DataTrans dataTrans, int shardingItem, int shardingTotal) {
		
		String key = String.format(PAGE_PATH, dataTrans.getName(), shardingItem);
		//获取分片当前页（这里不需要分布式锁，本地锁就够了）
		synchronized (this) {
			try{
				//获取当前页
				DistributedAtomicInteger atomicInteger = getAtomicInteger(key);
				int pageStart = atomicInteger.get().postValue();
				//查询当前页信息
				Page page = dataTransDao.pageInfo(DataType.source, SqlUtil.getPagePostSharding(dataTrans.getSourceTable(), dataTrans.getSourceKey(), shardingItem, shardingTotal,pageStart ,dataTrans.getPageCount()));
				//如果开始和结束位置为同一个了，那么说明分页搞完了
				if(page.getPageStart() == page.getPageEnd()){
					log.info(key + " 分页完成！");
				}else{
					atomicInteger.forceSet(page.getPageEnd());
					log.info("pageend: " + page.getPageEnd() + ", pageStart: " + atomicInteger.get().postValue());
				}
				return page;
			}catch(Exception e) {
				log.error("dataTrans: " + dataTrans + " pageInfoByPostSharding fail ", e);
			}
		}
		return null;
	}

	/**
	 * 是否重新计数
	 * @param key
	 * @param initialize
	 * @return
	 */
	private DistributedAtomicInteger getAtomicInteger(String key){
		
		//不存在，则初始化
		if(!atomicIntegerMap.containsKey(key)){
			synchronized (this) {
				DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(regCenter.getClient(), key, new RetryNTimes(3, 1000));
//				try{
//					//初始化的时候强制归零
//					if(initialize){
//						log.info("key: " + key + " atomicInteger initialize " + DEFAULT);
//						atomicInteger.forceSet(DEFAULT);
//					}
//				}catch(Exception e) {
//					log.error("key: " + key + " atomicInteger initialize fail ", e);
//				}
				atomicIntegerMap.put(key, atomicInteger);
			}
		}
		return atomicIntegerMap.get(key);
	}
}

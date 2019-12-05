package com.yanerbo.datatransfer.server.manager.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.yanerbo.datatransfer.config.DataTransConfig;
import com.yanerbo.datatransfer.entity.DataTransEntity;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.server.dao.IDataTransDao;
import com.yanerbo.datatransfer.server.manager.IDataTransManager;
import com.yanerbo.datatransfer.support.impl.IDistributedHandle;
import com.yanerbo.datatransfer.support.util.SqlUtil;

/**
 * 
 * 数据传输manager
 * @author jihaibo
 *
 */
@Component
public class DataTransManager implements IDataTransManager{

	/**
	 * 日志
	 */
	private final static Logger log = LoggerFactory.getLogger(DataTransManager.class);
	/**
	 * zk当前页
	 */
	private static final String CURRENTPAGE = "/%s/%s/currentPage";
	/**
	 * 线程池
	 */
	private final static ExecutorService executor = Executors.newFixedThreadPool(50);
	/**
	 * 分布式处理
	 */
	@Autowired
	@Qualifier("zookeeperDistributedHandle")
	private IDistributedHandle distributedHandle;
	/**
	 * 数据传输job配置
	 */
	@Resource
	private DataTransConfig dataTransConfig;
	
	/**
	 * 数据库操作
	 */
	@Autowired
	private IDataTransDao dataTransDao;

	/**
	 * 参数校验
	 * @param config
	 */
	private void validate(DataTransEntity config) {
		//配置信息为空
		if(config == null){
			throw new DataTransRuntimeException("配置信息为空");
		}
		//配置信息名称为空
		if(config.getName()==null || config.getName().isEmpty()){
			throw new DataTransRuntimeException("配置信息名称为空");
		}
		//配置信息名称为空
		if(config.getMode() == null || !config.getMode().equals("all")){
			throw new DataTransRuntimeException("配置信息传输模式不为全量");
		}
	}
	
	/**
	 * 数据传输
	 */
	@Override
	public boolean trans(String jobName) {
		return trans(jobName, 0, 0);
	}
	/**
	 * 数据分片传输
	 */
	@Override
	public boolean trans(String jobName, int shardingItem, int shardingTotal) {
		//获取传输配置信息
		DataTransEntity config = getDataTransEntity(jobName);
		/**
		 * 校验参数
		 */
		validate(config);
		//开始进行数据迁移		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				
				String key = String.format(CURRENTPAGE, jobName, shardingItem);;
				//获取分片当前页
				int currentPage = distributedHandle.increment(key);
				//获取原表总数
//				int count = dataTransDao.count(DataType.source, SqlUtil.getAllCount(config.getSourceTable(), config.getSourceKey(), shardingItem, shardingTotal));
//				log.info("dataTrans " + config.getName() + " total count:" + count + ", pageCount: " + config.getPageCount() +",currentPage: " + currentPage);
				//获取原表数据
				long startTime = System.currentTimeMillis();
				List<Map<String, Object>> datas = dataTransDao.select(DataType.source, SqlUtil.builderSelect(config, shardingItem, shardingTotal, currentPage));
				log.info("job name: " + config.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + " query " + datas.size() + " 执行耗时：" + (System.currentTimeMillis() - startTime));
				startTime = System.currentTimeMillis();
				dataTransDao.insertBatch(DataType.target, SqlUtil.builderInsert(config), datas);
				log.info("job name: " + config.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + " insert total " + datas.size() + " 执行耗时：" + (System.currentTimeMillis() - startTime));
			}
		
		});
		return true;
	}
	
	private boolean allTrans(DataTransEntity config, int shardingItem, int shardingTotal){
		//开始进行数据迁移		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				
				String key = String.format(CURRENTPAGE, config.getName(), shardingItem);;
				//获取分片当前页
				int currentPage = distributedHandle.increment(key);
//				int count = dataTransDao.count(DataType.source, SqlUtil.getAllCount(config.getSourceTable(), config.getSourceKey(), shardingItem, shardingTotal));
//				log.info("dataTrans " + config.getName() + " total count:" + count + ", pageCount: " + config.getPageCount() +",currentPage: " + currentPage);
				//获取原表数据
				long startTime = System.currentTimeMillis();
				List<Map<String, Object>> datas = dataTransDao.select(DataType.source, SqlUtil.builderSelect(config, shardingItem, shardingTotal, currentPage));
				dataTransDao.insertBatch(DataType.target, SqlUtil.builderInsert(config), datas);
				log.info("job name: " + config.getName() + ", 当前分片：" + shardingItem + ",总分片" + shardingTotal + "insert total " + datas.size() + " 执行耗时：" + (System.currentTimeMillis() - startTime));
			}
		
		});
		return true;
	}
	
	/**
	 * 获取分片配置信息
	 * @param jobName
	 * @return
	 */
	private DataTransEntity getDataTransEntity(String jobName) {
		
		for (final DataTransEntity entity : dataTransConfig.getSchedules()) {
			if(entity.getName().equals(jobName)) {
				return entity;
			}
		}
		throw new DataTransRuntimeException("没有找到对应job配置");
	}
}

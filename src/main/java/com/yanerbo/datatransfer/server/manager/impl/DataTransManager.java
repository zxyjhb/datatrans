package com.yanerbo.datatransfer.server.manager.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
	
	private final Map<String, AtomicInteger> currentPages = new HashMap<String, AtomicInteger>();
	
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
		//TODO
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
				long startTime = System.currentTimeMillis();
				String key = "/" + config.getName() + "/" + shardingItem + "/page";
				//获取分片当前页
				int currentPage = distributedHandle.increment(key);
				//获取原表总数
				int count = dataTransDao.count(DataType.source, SqlUtil.getAllCount(config.getSourceTable(), config.getSourceKey(), shardingItem, shardingTotal));
				log.info("dataTrans " + config.getName() + " total count:" + count + ", pageCount: " + config.getPageCount() +",currentPage: " + currentPage);
				//获取原表数据
				List<Map<String, Object>> datas = dataTransDao.select(DataType.source, SqlUtil.builderSelect(config, shardingItem, shardingTotal, currentPage));
				dataTransDao.insert(DataType.target, SqlUtil.builderInsert(config.getTargetTable(), config.getTargetColumns(), config.getTargetSql()), datas);
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

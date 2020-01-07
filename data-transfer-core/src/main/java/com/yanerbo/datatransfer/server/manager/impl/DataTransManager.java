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
import com.yanerbo.datatransfer.shared.domain.DataTrans;
import com.yanerbo.datatransfer.shared.domain.DataType;
import com.yanerbo.datatransfer.shared.domain.Page;
import com.yanerbo.datatransfer.shared.domain.RunType;
import com.yanerbo.datatransfer.shared.util.SqlUtil;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.server.dao.IDataTransDao;
import com.yanerbo.datatransfer.server.manager.IDataTransManager;
import com.yanerbo.datatransfer.support.impl.IDistributedPage;

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
	 * 线程池(双倍的当前机器核数)
	 * 再扩两倍吧
	 */
	private final static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
	/**
	 * 分布式处理分页
	 */
	@Autowired
	@Qualifier("zookeeperDistributedPage")
	private IDistributedPage distributedPage;
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
	 * @param dataTrans
	 */
	private DataTrans validate(DataTrans dataTrans) {
		//配置信息为空
		if(dataTrans == null){
			throw new DataTransRuntimeException("配置信息为空");
		}
		//配置信息名称为空
		if(dataTrans.getName()==null || dataTrans.getName().isEmpty()){
			throw new DataTransRuntimeException("配置信息名称为空");
		}
		return dataTrans;
	}
	
	/**
	 * 数据传输
	 */
	@Override
	public boolean allTrans(String name) {
		return allTrans(name, 1, 1);
	}
	
	
	
	/**
	 * 数据分片传输（多线程处理）
	 */
	@Override
	public boolean allTrans(String name, int shardingItem, int shardingTotal) {
		//获取传输配置信息
		DataTrans dataTrans = validate(dataTransConfig.getDataTrans(name));
		//如果runtype不为全量，说明不用运行
		if(!RunType.all.name().equals(dataTrans.getMode())) {
			log.info("job name: " + dataTrans.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + ",全量已经完毕，不用执行");
			return true;
		}
		//多线程并行去处理
		for(int i = 1; i<dataTrans.getMaxThread(); i++){
			//开始进行数据迁移
			executor.execute(new Runnable() {
				@Override
				public void run() {
					long startTime = System.currentTimeMillis();
					//这里处理并发分页
					Page page = distributedPage.pageInfo(dataTrans.getName(), shardingItem, shardingTotal);
					//开始位置和结束位置一样，那就是跑完了
					if(page==null || page.getPageStart() == page.getPageEnd()){
						return;
					}
					log.info("job name: " + dataTrans.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + ",分页耗时：" + (System.currentTimeMillis() - startTime) +  page);
					//获取原表数据
					List<Map<String, Object>> datas = dataTransDao.select(DataType.source, SqlUtil.builderSelect(dataTrans, page.getPageStart(), page.getPageEnd(), shardingItem, shardingTotal));
					log.info("job name: " + dataTrans.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + ",查询耗时：" + (System.currentTimeMillis() - startTime));
					startTime = System.currentTimeMillis();
					dataTransDao.insertBatch(DataType.target, SqlUtil.builderInsert(dataTrans), datas);
					log.info("job name: " + dataTrans.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + ",保存耗时：" + (System.currentTimeMillis() - startTime));
				}
			});
		}
		return true;
	}
	
	@Override
	public boolean addTrans(String name, Object data) {
		//获取传输配置信息
//		DataTrans dataTrans = validate(dataTransConfig.getDataTrans(name));
//		dataTransDao.save(data);
		return true;
	}
}

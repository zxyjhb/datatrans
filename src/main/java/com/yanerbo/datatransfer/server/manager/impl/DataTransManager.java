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
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.entity.Page;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.server.dao.IDataTransDao;
import com.yanerbo.datatransfer.server.manager.IDataTransManager;
import com.yanerbo.datatransfer.support.impl.IDistributedPage;
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
	 * 线程池
	 */
	private final static ExecutorService executor = Executors.newFixedThreadPool(50);
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
		//配置信息名称为空
		if(dataTrans.getMode() == null || !dataTrans.getMode().equals("all")){
			throw new DataTransRuntimeException("配置信息传输模式不为全量");
		}
		return dataTrans;
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
		DataTrans dataTrans = validate(dataTransConfig.getDataTrans(jobName));
		//开始进行数据迁移		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				
				long startTime = System.currentTimeMillis();
				Page page = distributedPage.getPage(jobName, shardingItem, shardingTotal);
				log.info("job name: " + dataTrans.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + ",分页耗时：" + (System.currentTimeMillis() - startTime) +  page);
				//当前分片已经跑完，就停止了
				if(page.getStartPostPage() + page.getCurrentPage()*(dataTrans.getPageCount()+1) > page.getEndPostPage()){
					log.info("当前分片已经跑完，运行完毕！");
					return;
				}
				//获取原表数据
				List<Map<String, Object>> datas = dataTransDao.select(DataType.source, SqlUtil.builderSelect(dataTrans, shardingItem, shardingTotal, page.getCurrentPage(), page.getStartPostPage()));
				log.info("job name: " + dataTrans.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + ",查询耗时：" + (System.currentTimeMillis() - startTime) +  page);
				startTime = System.currentTimeMillis();
				dataTransDao.insertBatch(DataType.target, SqlUtil.builderInsert(dataTrans), datas);
				log.info("job name: " + dataTrans.getName() + ", 当前分片：" + shardingItem + ",总分片 " + shardingTotal + ",保存耗时：" + (System.currentTimeMillis() - startTime) +  page);
			}
		
		});
		return true;
	}
}

package com.yanerbo.datatransfer.job;

import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.yanerbo.datatransfer.server.manager.impl.DataTransManager;
import com.yanerbo.datatransfer.support.util.SpringContextUtil;

/**
 * 
 * 定时任务
 * @author jihaibo
 *
 */
@Component
public class DataTransJob implements SimpleJob{
	
	@Override
	public void execute(ShardingContext shardingContext) {
		DataTransManager dataTransManager = SpringContextUtil.getBean(DataTransManager.class, "dataTransManager");
		//这里主要是因为该定时器跑起来之后，SpringContextUtil上下文还没有加载完成
		if(dataTransManager!=null){
			dataTransManager.allTrans(shardingContext.getJobName(), shardingContext.getShardingItem(), shardingContext.getShardingTotalCount());
		}
	}

}
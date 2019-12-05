package com.yanerbo.datatransfer.job;

import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.yanerbo.datatransfer.server.manager.impl.DataTransManager;
import com.yanerbo.datatransfer.support.util.ElasticJobSpringContextUtil;

/**
 * 
 * @author jihaibo
 *
 */
@Component
public class DataTransJob implements SimpleJob{
	
	@Override
	public void execute(ShardingContext shardingContext) {
		DataTransManager dataTransManager = ElasticJobSpringContextUtil.getBean(DataTransManager.class, "dataTransManager");
		dataTransManager.trans(shardingContext.getJobName(), shardingContext.getShardingItem(), shardingContext.getShardingTotalCount());
	}

}

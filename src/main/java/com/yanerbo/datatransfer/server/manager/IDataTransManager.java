package com.yanerbo.datatransfer.server.manager;
/**
 * 
 * @author jihaibo
 *
 */
public interface IDataTransManager {
	
	/**
	 * 数据传输（进行数据迁移）
	 * @param config
	 * @return
	 */
	public boolean trans(String jobName);
	
	/**
	 * 数据传输（进行数据迁移）分片
	 * @param config
	 * @return
	 */
	public boolean trans(String jobName, int shardingItem, int shardingTotal);

}

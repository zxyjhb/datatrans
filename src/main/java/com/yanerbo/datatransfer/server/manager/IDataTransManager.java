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
	public boolean allTrans(String name);
	
	/**
	 * 数据传输（进行数据迁移）分片
	 * @param config
	 * @return
	 */
	public boolean allTrans(String name, int shardingItem, int shardingTotal);
	
	/**
	 * 数据传输（进行增量）
	 * @param config
	 * @return
	 */
	public boolean addTrans(String name, Object data);
	
	

}

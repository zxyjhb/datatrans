package com.yanerbo.datatransfer.support.impl;

public interface IDistributedPage {
	
	/**
	 * 获取当前总页数
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	public int getTotalCount(String jobName, int shardingItem, int shardingTotal);

	/**
	 * 获取当前页数
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	public int getCurrentPage(String jobName, int shardingItem, int shardingTotal);

}

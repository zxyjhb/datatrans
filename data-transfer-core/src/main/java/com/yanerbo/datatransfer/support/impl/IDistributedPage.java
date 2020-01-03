package com.yanerbo.datatransfer.support.impl;

import com.yanerbo.datatransfer.entity.Page;

public interface IDistributedPage {
	/**
	 * 获取当前页数
	 * @param jobName
	 * @param shardingItem
	 * @return
	 */
	public Page pageInfo(String jobName, int shardingItem, int shardingTotal);

}

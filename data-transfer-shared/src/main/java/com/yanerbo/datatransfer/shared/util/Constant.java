package com.yanerbo.datatransfer.shared.util;

/**
 * 
 * @author jihaibo
 *
 */
public interface Constant {

	/**
	 * data-transfer 配置根路径
	 */
	static final String ROOT_PATH = "/datatrans-config";
	/**
	 * data-transfer 路径
	 */
	static final String CONFIG_PATH = ROOT_PATH + "/config/%s";
	/**
	 * data-transfer 配置根路径
	 */
	static final String CONFIG_ROOT = ROOT_PATH + "/config";
	/**
	 * zk分页-位置 根路径
	 */
	static final String PAGE_ROOT = ROOT_PATH + "/page/%s";
	/**
	 * zk分页-位置 路径
	 */
	static final String PAGE_PATH = ROOT_PATH + "/page/%s/%s";
	/**
	 * zk分页-顺序 根路径
	 */
	static final String SEQ_ROOT = ROOT_PATH + "/seq/%s";
	/**
	 * zk分页-顺序 路径
	 */
	static final String SEQ_PATH = ROOT_PATH + "/seq/%s/%s";
	/**
	 * zk分布式锁路径
	 */
	static final String LOCK_PATH = ROOT_PATH + "/lock/%s";
	
	
	
	
}

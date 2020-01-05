package com.yanerbo.datatransfer.shared.util;

/**
 * 
 * @author jihaibo
 *
 */
public interface Constant {

	/**
	 * datatransfer 配置根路径
	 */
	static final String ROOT_PATH = "/datatrans-config";
	/**
	 * datatransfer 路径
	 */
	static final String CONFIG_PATH = ROOT_PATH + "/config/%s";
	/**
	 * zk分页
	 */
	static final String PAGE_PATH = ROOT_PATH + "/page/%s/%s";
	/**
	 * zk当前页
	 */
	static final String CURRENTPAGE_PATH = ROOT_PATH + "/currentpage/%s/%s";
	/**
	 * zk当前起始位置
	 */
	static final String STARTPAGE_PATH = ROOT_PATH + "/startpage/%s/%s";
	/**
	 * datatransfer 路径
	 */
	static final String CONFIG_ROOT = ROOT_PATH + "/config";

	/**
	 * zk 路径
	 */
	static final String CURRENTPAGE_ROOT = ROOT_PATH + "/currentpage";

	/**
	 * zk 路径
	 */
	static final String STARTPAGE_ROOT = ROOT_PATH + "/startpage";
	/**
	 * zk分页 路径
	 */
	static final String PAGE_ROOT = ROOT_PATH + "/page";
	
	
}

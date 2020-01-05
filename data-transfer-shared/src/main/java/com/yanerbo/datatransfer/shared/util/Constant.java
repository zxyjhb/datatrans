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
	 * zk当前页
	 */
	static final String CURRENTPAGE = ROOT_PATH + "/currentpage/%s/%s";
	/**
	 * zk当前起始位置
	 */
	static final String STARTPAGE = ROOT_PATH + "/startpage/%s/%s";
	/**
	 * datatransfer 路径
	 */
	static final String CONFIG_ROOT = ROOT_PATH + "/config";

	/**
	 * datatransfer 路径
	 */
	static final String CURRENTPAGE_ROOT = ROOT_PATH + "/currentpage";

	/**
	 * datatransfer 路径
	 */
	static final String STARTPAGE_ROOT = ROOT_PATH + "/startpage";
	
	
}

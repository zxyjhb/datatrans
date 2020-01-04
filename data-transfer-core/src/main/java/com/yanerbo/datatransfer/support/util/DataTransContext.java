package com.yanerbo.datatransfer.support.util;

import java.util.HashMap;
import java.util.Map;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.yanerbo.datatransfer.shared.domain.DataTrans;
import com.yanerbo.datatransfer.shared.domain.Page;

/**
 * 上下文信息
 * @author 274818
 *
 */
public class DataTransContext {

	/**
	 * 配置信息
	 */
	private static final Map<String,DataTrans> CONFIG_MAP = new HashMap<String, DataTrans>();
	/**
	 * 分页信息
	 */
	private static final Map<String, Page> PAGE_MAP = new HashMap<String, Page>();
	/**
	 * JOB配置信息
	 */
	private static final Map<String, SpringJobScheduler> JOB_MAP = new HashMap<String, SpringJobScheduler>();
	
	
	/**
	 * 根据jobName获取配置
	 * 
	 * @param jobName
	 * @return
	 */
	public static DataTrans getDataTrans(String name) {
		return CONFIG_MAP.get(name);
	}
	/**
	 * 更改jobConfig
	 * 
	 * @param jobName
	 * @param jobConfig
	 */
	public static void setDataTrans(String name, DataTrans dataTrans) {
		CONFIG_MAP.put(name, dataTrans);
	}

	/**
	 * 根据jobName获取配置
	 * 
	 * @param jobName
	 * @return
	 */
	public static Page getInitPage(String name) {
		return PAGE_MAP.get(name);
	}
	/**
	 * 更改jobConfig
	 * 
	 * @param jobName
	 * @param jobConfig
	 */
	public static void setInitPage(String name, Page page) {
		PAGE_MAP.put(name, page);
	}
	
	/**
	 * 根据jobName获取配置
	 * 
	 * @param jobName
	 * @return
	 */
	public static SpringJobScheduler getJobConfig(String name) {
		return JOB_MAP.get(name);
	}
	/**
	 * 更改jobConfig
	 * 
	 * @param jobName
	 * @param jobConfig
	 */
	public static void setJobConfig(String name, SpringJobScheduler jobConfig) {
		JOB_MAP.put(name, jobConfig);
	}
	
}

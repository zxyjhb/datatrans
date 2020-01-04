package com.yanerbo.datatransfer.console.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.yanerbo.datatransfer.console.server.manager.IDataTransConfigManager;
import com.yanerbo.datatransfer.shared.domain.DataTrans;

/**
 * 
 * @author jihaibo
 *
 */
@RestController
public class DataTransConfigController {
	
	@Resource
	private IDataTransConfigManager dataTransConfigManager;
	/**
	 * 配置列表
	 * @return
	 */
	@GetMapping(value="/data-transfer/configs")
	public String getConfigs() {
		String json = JSON.toJSONString(dataTransConfigManager.getDataTransList());
		return json;
	}
	/**
	 * 配置信息
	 * @return
	 */
	@GetMapping(value="/data-transfer/configs/{name}")
	public String getConfig(@PathVariable("name") String name) {
		String json = JSON.toJSONString(dataTransConfigManager.getDataTrans(name));
		return json;
	}
	
	/**
	 * 修改配置信息
	 * @return
	 */
	@PostMapping(value="/data-transfer/configs/")
	public String updateConfig(DataTrans dataTrans) {
		dataTransConfigManager.updateDataTrans(dataTrans);
		return "OK";
	}
	
	/**
	 * (临时用来修改数据的，其实缺一个界面)
	 * @return
	 */
	@GetMapping(value="/data-transfer/configs/{name}/update")
	public String updateConfig(@PathVariable("name") String name, 
			@RequestParam(value="sourceSql", required=false) String sourceSql,
			@RequestParam(value="targetColumns", required=false) String targetColumns,
			@RequestParam(value="pageCount", required=false) Integer pageCount,
			@RequestParam(value="mode", required=false) String mode) {
		
		DataTrans dataTrans = dataTransConfigManager.getDataTrans(name);
		if(sourceSql != null) {
			dataTrans.setSourceSql(sourceSql);
		}
		if(mode!=null) {
			dataTrans.setMode(mode);
		}
		if(targetColumns!=null) {
			dataTrans.setTargetColumns(targetColumns);
		}
		if(pageCount!=null) {
			dataTrans.setPageCount(pageCount);
		}
		dataTransConfigManager.updateDataTrans(dataTrans);
		return "OK";
	}
	/**
	 * 
	 * @return
	 */
	@GetMapping(value="/data-transfer/console/{name}/startup")
	public String startup(@PathVariable("name") String name) {
		dataTransConfigManager.startup(name);
		return "startup Success";
	}
	/**
	 * 
	 * @return
	 */
	@GetMapping(value="/data-transfer/console/{name}/shutdown")
	public String shutdown(@PathVariable("name") String name) {
		dataTransConfigManager.shutdown(name);
		return "shutdown Success";
	}
	/**
	 * 
	 * @return
	 */
	@GetMapping(value="/data-transfer/console/{name}/restart")
	public String restart(@PathVariable("name") String name) {
		dataTransConfigManager.restart(name);
		return "restart Success";
	}
	

}

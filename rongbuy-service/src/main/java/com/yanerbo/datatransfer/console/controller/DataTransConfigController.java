package com.yanerbo.datatransfer.console.controller;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.yanerbo.datatransfer.console.server.manager.IDataTransConfigManager;
import com.yanerbo.datatransfer.shared.domain.DataTrans;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author jihaibo
 *
 */
@RestController
@Api(tags = "data transfer operate doc")
public class DataTransConfigController {
	
	@Resource
	private IDataTransConfigManager dataTransConfigManager;
	
	@ApiOperation("查询配置列表")
	@GetMapping(value="/data-transfer/test")
	public String test() {
		while(true) {
			System.out.println("ee:" + Thread.currentThread().getName());	
		}
	}
	
	/**
	 * 配置列表
	 * @return
	 */
	@ApiOperation("查询配置列表")
	@GetMapping(value="/data-transfer/config")
	public String getConfigs() {
		String json = JSON.toJSONString(dataTransConfigManager.getDataTransList());
		return json;
	}
	
	/**
	 * 修改配置信息
	 * @return
	 */
	@ApiOperation("修改配置信息")
	@PostMapping(value="/data-transfer/config")
	public String updateConfig(@RequestBody DataTrans dataTrans) {
		dataTransConfigManager.updateDataTrans(dataTrans);
		return "update Success";
	}
	
	/**
	 * 新增配置信息
	 * @return
	 */
	@ApiOperation("新增配置信息")
	@PutMapping(value="/data-transfer/config")
	public String insertConfig(@RequestBody DataTrans dataTrans) {
		dataTransConfigManager.insertDataTrans(dataTrans);
		return "insert Success";
	}
	
	/**
	 * 按名称查询配置信息
	 * @return
	 */
	@ApiOperation("按名称查询配置信息")
	@GetMapping(value="/data-transfer/config/{name}")
	public String selectConfig(@PathVariable("name") String name) {
		String json = JSON.toJSONString(dataTransConfigManager.getDataTrans(name));
		return json;
	}
	
	/**
	 * 按名称删除配置信息
	 * @return
	 */
	@ApiOperation("按名称删除配置信息")
	@DeleteMapping(value="/data-transfer/config/{name}")
	public String deleteConfig(@PathVariable("name") String name) {
		dataTransConfigManager.deleteDataTrans(name);
		return "delete Success";
	}
	/**
	 * 
	 * @return
	 */
	@ApiOperation("启动")
	@GetMapping(value="/data-transfer/console/{name}/startup")
	public String startup(@PathVariable("name") String name) {
		dataTransConfigManager.startup(name);
		return "startup Success";
	}
	
	
	/**
	 * 
	 * @return
	 */
	@ApiOperation("暂停")
	@GetMapping(value="/data-transfer/console/{name}/suspend")
	public String suspend(@PathVariable("name") String name) {
		dataTransConfigManager.suspend(name);
		return "startup Success";
	}
	
	
	/**
	 * 
	 * @return
	 */
	@ApiOperation("停止")
	@GetMapping(value="/data-transfer/console/{name}/shutdown")
	public String shutdown(@PathVariable("name") String name) {
		dataTransConfigManager.shutdown(name);
		return "shutdown Success";
	}
	/**
	 * 
	 * @return
	 */
	@ApiOperation("重启")
	@GetMapping(value="/data-transfer/console/{name}/restart")
	public String restart(@PathVariable("name") String name) {
		dataTransConfigManager.restart(name);
		return "restart Success";
	}
	

	
	/**
	 * 按名称查询配置信息
	 * @return
	 */
	@ApiOperation("运行")
	@GetMapping(value="/data-transfer/config/execute")
	public String execute(@RequestParam("sql") String sql) {
		dataTransConfigManager.execute(sql);
		return "execute Success";
	}
}

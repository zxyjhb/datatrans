package com.yanerbo.datatransfer.controller;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yanerbo.datatransfer.config.DataTransConfig;
import com.yanerbo.datatransfer.server.manager.IDataTransManager;
/**
 * 
 * @author jihaibo
 *
 */
@RestController
public class DataTransController {
	
	@Resource
	private DataTransConfig dataTransConfig;
	@Resource
	private IDataTransManager dataTransManager;
	
	@GetMapping(value="/datatrans/config/list")
	public String getConfigs() {
		
		return "OK";
	}
	
	/**
	 * 
	 * 
	 */
	@GetMapping(value="/datatrans/add/send")
	public String send() {
		 return "OK";
	}
	
	/**
	 * 
	 * 
	 */
	@GetMapping(value="/datatrans/config/{name}")
	public String updatedataTransConfig() {
		return null;
	}

}

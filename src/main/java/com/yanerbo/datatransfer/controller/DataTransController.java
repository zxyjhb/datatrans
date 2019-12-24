package com.yanerbo.datatransfer.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yanerbo.datatransfer.consumer.DataTransConcurrentlyProducer;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.server.manager.IConfigManager;
import com.yanerbo.datatransfer.server.manager.IDataTransManager;
import com.yanerbo.datatransfer.support.util.DataSourceManager;

/**
 * 
 * @author jihaibo
 *
 */
@RestController
public class DataTransController {
	
	@Resource
	DataTransConcurrentlyProducer dataTransConcurrentlyProducer;
	@Resource
	private IDataTransManager dataTransManager;
	@Autowired
	private IConfigManager configManager;
	
	@GetMapping(value="/datatrans/config/list")
	public String getConfigs() {
		
		configManager.getConfigs();
		return "OK";
	}

	
	
	/**
	 * 
	 * 
	 */
	@GetMapping(value="/datatrans/add/send")
	public String send() {
		 dataTransConcurrentlyProducer.send("key", "message");
		 return "OK";
	}
	
	/**
	 * 
	 * 
	 */
	@GetMapping(value="/datatrans/get")
	public String get() {
		return null;
	}

}

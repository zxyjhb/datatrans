package com.yanerbo.datatransfer.controller;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yanerbo.datatransfer.server.manager.IDataTransManager;

/**
 * 
 * @author jihaibo
 *
 */
@RestController
public class DataTransController {
	
	@Resource
	private IDataTransManager dataTransManager;
	
	/**
	 * 
	 * 
	 */
	@GetMapping(value="/datatrans/get")
	public String get() {
		
		return null;
	}
	
	/**
	 * 获取目标所有的表信息
	 * target/source
	 * 
	 * @return
	 */
	public String getTargetTables() {
		return null;
	}
	
	/**
	 * 获取源所有的表信息
	 * target/source
	 * 
	 * @return
	 */
	public String getSourceTables() {
		return null;
	}
	/**
	 * 
	 * @return
	 */
	public String trans() {
		return null;
	}

}

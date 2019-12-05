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

}

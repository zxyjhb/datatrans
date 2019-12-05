package com.yanerbo.datatransfer.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.yanerbo.datatransfer.entity.DataTransEntity;
/**
 * 
 * @author jihaibo
 *
 */
@Component
@ConfigurationProperties(prefix = "datatrans")
public class DataTransConfig {
	
	private List<DataTransEntity> schedules = new ArrayList<>();

	public List<DataTransEntity> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<DataTransEntity> schedules) {
		this.schedules = schedules;
		System.out.println(this.schedules);
		
	}

}
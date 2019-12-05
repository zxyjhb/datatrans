package com.yanerbo.datatransfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;

/**
 * 
 * @author jihaibo
 *
 */
@Configuration
public class JobEventConfig {
	
	@Bean
	public JobEventConfiguration jobEventConfiguration() {
		return new DefualtJobEventConfiguration();
	}

}

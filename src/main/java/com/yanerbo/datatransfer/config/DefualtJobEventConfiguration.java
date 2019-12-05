package com.yanerbo.datatransfer.config;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.JobEventListener;
import com.dangdang.ddframe.job.event.JobEventListenerConfigurationException;
import com.dangdang.ddframe.job.event.type.JobExecutionEvent;
import com.dangdang.ddframe.job.event.type.JobStatusTraceEvent;

/**
 * 这里因为job源码里面需要创建运行的表，但是源码里面的写法貌似不支持oracle11g
 * 因此这里给一个null的实现。用来屏蔽掉
 * @author 274818
 *
 */
public class DefualtJobEventConfiguration implements JobEventConfiguration{

	@Override
	public String getIdentity() {
		return "defualt";
	}

	@Override
	public JobEventListener createJobEventListener()
			throws JobEventListenerConfigurationException {
		return new JobEventListener(){

			@Override
			public String getIdentity() {
				return "defualt";
			}

			@Override
			public void listen(JobExecutionEvent jobExecutionEvent) {
				
			}

			@Override
			public void listen(JobStatusTraceEvent jobStatusTraceEvent) {
				
			}
	};
};
}

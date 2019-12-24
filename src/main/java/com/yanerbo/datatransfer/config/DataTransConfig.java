package com.yanerbo.datatransfer.config;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;
import com.yanerbo.datatransfer.server.dao.IDataTransConfigDao;
/**
 * 
 * @author jihaibo
 *
 */
@Component
@ConfigurationProperties(prefix = "datatrans")
public class DataTransConfig implements InitializingBean{
	
	@Autowired
	private IDataTransConfigDao dataTransConfigDao;
	/**
	 * 数据列表
	 */
	private List<DataTrans> schedules = new ArrayList<>();

	public List<DataTrans> getSchedules() {
		
		return schedules;
	}

	public void setSchedules(List<DataTrans> schedules) {
		this.schedules = schedules;	
	}
	
	/**
	 * 获取分片配置信息
	 * @param jobName
	 * @return
	 */
	public DataTrans getDataTrans(String jobName) {
		
		for (final DataTrans entity : schedules) {
			if(entity.getName().equals(jobName)) {
				return entity;
			}
		}
		throw new DataTransRuntimeException("没有找到对应job配置");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		List<DataTrans> dataTrans = dataTransConfigDao.getDataTrans();
		if(dataTrans != null){
			setSchedules(dataTrans);
		}
	}

}
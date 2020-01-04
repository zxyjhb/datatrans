package com.yanerbo.datatransfer.config;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.shared.domain.DataTrans;
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
	
	/**
	 * zk path
	 */
	private static final String CONFIG_PATH = "/%s/datatrans-config";
	
	/**
	 * 数据列表（配置文件加载）
	 */
	private List<DataTrans> schedules = new ArrayList<>();
	/**
	 * 数据库加载
	 */
	@Autowired
	private IDataTransConfigDao dataTransConfigDao;
	/**
	 * zk
	 */
	@Autowired
	@Qualifier("zookeeperRegistryCenter")
	private ZookeeperRegistryCenter regCenter;
	
	

	public List<DataTrans> getDataTransConfigs() {		
		return schedules;
	}
	/**
	 * 更新配置文件
	 * @param dataTrans
	 */
	public void setDataTransConfig(DataTrans dataTrans) {
		
		for (int i=0; i<schedules.size(); i++) {
			if(schedules.get(i).getName().equals(dataTrans.getName())) {
				schedules.set(i, dataTrans);
				regCenter.persist(String.format(CONFIG_PATH, dataTrans.getName()), JSON.toJSONString(dataTrans));
				dataTransConfigDao.updateDataTrans(dataTrans);
			}
		}
	}
	
	/**
	 * 获取分片配置信息
	 * @param jobName
	 * @return
	 */
	public DataTrans getLikeDataTrans(String name) {
		if(name != null){
			for (final DataTrans entity : schedules) {
				if(name.contains(entity.getName())) {
					return entity;
				}
			}
		}
		throw new DataTransRuntimeException("没有找到对应job配置");
	}
	/**
	 * 获取分片配置信息
	 * @param jobName
	 * @return
	 */
	public DataTrans getDataTrans(String name) {
		
		for (final DataTrans entity : schedules) {
			if(entity.getName().equals(name)) {
				return entity;
			}
		}
		throw new DataTransRuntimeException("没有找到对应job配置");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//从数据库加载
		List<DataTrans> dataTransList = dataTransConfigDao.getDataTransList();
		if(dataTransList != null){
			this.schedules = dataTransList;
			for(DataTrans dataTrans : dataTransList){
				regCenter.persist(String.format(CONFIG_PATH, dataTrans.getName()), JSON.toJSONString(dataTrans));
			}
		}
	}
}
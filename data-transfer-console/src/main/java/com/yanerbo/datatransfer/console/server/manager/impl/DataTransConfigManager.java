package com.yanerbo.datatransfer.console.server.manager.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.yanerbo.datatransfer.shared.domain.DataTrans;
import com.yanerbo.datatransfer.shared.domain.RunType;
import com.yanerbo.datatransfer.shared.util.Constant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.yanerbo.datatransfer.console.server.dao.IDataTransConfigDao;
import com.yanerbo.datatransfer.console.server.manager.IDataTransConfigManager;
/**
 * 
 * @author jihaibo
 *
 */
@Component
public class DataTransConfigManager implements IDataTransConfigManager, Constant{

	@Autowired
	private IDataTransConfigDao dataTransConfigDao;
	/**
	 * zk注册中心
	 */
	@Autowired
	@Qualifier("zookeeperRegistryCenter")
	private ZookeeperRegistryCenter regCenter;	
	
	@Override
	public DataTrans getDataTrans(String name) {
		DataTrans dataTrans = JSONObject.parseObject(regCenter.get(String.format(CONFIG_PATH, name)), DataTrans.class);
		return dataTrans;
	}

	@Override
	public List<DataTrans> getDataTransList() {
		
		List<DataTrans> dataTransList = new ArrayList<DataTrans>();
		for(String key : regCenter.getChildrenKeys(CONFIG_ROOT)) {
			dataTransList.add(getDataTrans(key));
		}
		return dataTransList;
	}

	@Override
	public void updateDataTrans(DataTrans dataTrans) {
		
		regCenter.persist(String.format(CONFIG_PATH, dataTrans.getName()), JSON.toJSONString(dataTrans));
		
	}
	

	@Override
	public void insertDataTrans(DataTrans dataTrans) {
		
		regCenter.persist(String.format(CONFIG_PATH, dataTrans.getName()), JSON.toJSONString(dataTrans));
	}
	
	@Override
	public void deleteDataTrans(String name) {
		
		regCenter.remove(String.format(CONFIG_PATH, name));
	}

	/**
	 * 重启
	 */
	@Override
	public void restart(String name) {
		//获取配置信息
		DataTrans dataTrans = getDataTrans(name);
		//没有找到配置信息
		if(dataTrans == null) {
			return;
		}
		//设置为none模式
		dataTrans.setMode(RunType.none.name());
		//更新数据
		updateDataTrans(dataTrans);
		//清空表数据
		dataTransConfigDao.truncate(dataTrans.getTargetTable());
		//清空zk分页信息
		for (String key : regCenter.getChildrenKeys(String.format(PAGE_ROOT, dataTrans.getName()))) {
			regCenter.remove(String.format(PAGE_PATH, dataTrans.getName(), key));
		}
		//设置为全量模式
		dataTrans.setMode(RunType.all.name());
		//获取初始分页信息
		dataTransConfigDao.updateDataTrans(dataTrans);
	}

	/**
	 * 停止
	 */
	@Override
	public void shutdown(String name) {
		
		//获取配置信息
		DataTrans dataTrans = getDataTrans(name);
		//没有找到配置信息
		if(dataTrans == null) {
			return;
		}
		//设置none模式
		dataTrans.setMode(RunType.none.name());
		//更新数据
		updateDataTrans(dataTrans);
		
	}

	/**
	 * 启动
	 */
	@Override
	public void startup(String name) {
		//获取配置信息
		DataTrans dataTrans = getDataTrans(name);
		//没有找到配置信息
		if(dataTrans == null) {
			return;
		}
		//清空表数据
		dataTransConfigDao.truncate(dataTrans.getTargetTable());
		//清空zk分页信息
		for (String key : regCenter.getChildrenKeys(String.format(PAGE_ROOT, dataTrans.getName()))) {
			regCenter.remove(String.format(PAGE_PATH, dataTrans.getName(), key));
		}
		//设置为全量模式
		dataTrans.setMode(RunType.all.name());
		//更新数据
		updateDataTrans(dataTrans);
	}

	/**
	 * 暂停
	 */
	@Override
	public void suspend(String name) {
		//获取配置信息
		DataTrans dataTrans = getDataTrans(name);
		//没有找到配置信息
		if(dataTrans == null) {
			return;
		}
		//设置为全量模式
		dataTrans.setMode(RunType.hook.name());
		//更新数据
		updateDataTrans(dataTrans);
	}
}

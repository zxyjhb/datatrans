package com.yanerbo.datatransfer.console.server.manager.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yanerbo.datatransfer.shared.domain.DataTrans;
import com.yanerbo.datatransfer.console.server.dao.IDataTransConfigDao;
import com.yanerbo.datatransfer.console.server.manager.IDataTransConfigManager;

@Component
public class DataTransConfigManager implements IDataTransConfigManager{

	@Autowired
	private IDataTransConfigDao dataTransConfigDao;
	
	@Override
	public DataTrans getDataTrans(String name) {
		return dataTransConfigDao.getDataTrans(name);
	}

	@Override
	public List<DataTrans> getDataTransList() {
		return dataTransConfigDao.getDataTransList();
	}

	@Override
	public void updateDataTrans(DataTrans dataTrans) {
		dataTransConfigDao.updateDataTrans(dataTrans);
		
	}

	@Override
	public void restart(String name) {
		dataTransConfigDao.init(name);
	}

	@Override
	public void shutdown(String name) {
		
		dataTransConfigDao.init(name);
		
	}

	@Override
	public void startup(String name) {
		dataTransConfigDao.init(name);
		
	}

}

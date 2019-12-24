package com.yanerbo.datatransfer.server.manager;

import java.util.List;
import com.yanerbo.datatransfer.entity.DataTrans;

/**
 * 
 * @author 274818
 *
 */
public interface IConfigManager {

	
	public List<DataTrans> getConfigs();
	
	public void setConfig(DataTrans dataTrans);
	
	public void insertConfig(DataTrans dataTrans);
	
	public void updateConfig(DataTrans dataTrans);
	
	public void deleteConfig(DataTrans dataTrans);
}

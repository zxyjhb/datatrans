package com.yanerbo.datatransfer.console.server.manager;

import java.util.List;
import com.yanerbo.datatransfer.shared.domain.DataTrans;

/**
 * 
 * @author jihaibo
 *
 */
public interface IDataTransConfigManager {
	
	public DataTrans getDataTrans(String name);
	
	public List<DataTrans> getDataTransList();

	public void updateDataTrans(DataTrans dataTrans);
	
	public void insertDataTrans(DataTrans dataTrans);
	
	public void deleteDataTrans(String name);

	public void restart(String name); 
	
	public void shutdown(String name); 
	
	public void startup(String name);

	public void suspend(String name);

	public void execute(String sql);

	

}

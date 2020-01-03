package com.yanerbo.datatransfer.console.server.dao;

import java.util.List;

import com.yanerbo.datatransfer.console.entity.DataTrans;

/**
 * 
 * @author 274818
 *
 */
public interface IDataTransConfigDao {

	public DataTrans getDataTrans(String name);
	
	public List<DataTrans> getDataTransList();
	
	public void updateDataTrans(DataTrans dataTrans); 
}

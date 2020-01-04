package com.yanerbo.datatransfer.server.dao;

import java.util.List;

import com.yanerbo.datatransfer.shared.domain.DataTrans;

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

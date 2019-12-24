package com.yanerbo.datatransfer.server.dao;

import java.util.List;

import com.yanerbo.datatransfer.entity.DataTrans;

/**
 * 
 * @author 274818
 *
 */
public interface IDataTransConfigDao {

	
	public List<DataTrans> getDataTrans();
	
	public void updateDataTrans(DataTrans dataTrans); 
}

package com.yanerbo.datatransfer.server.dao;

import java.util.List;
import java.util.Map;

import com.yanerbo.datatransfer.entity.DataType;


/**
 * 
 * @author jihaibo
 *
 */
public interface IDataTransDao {

	public int count(DataType type, String sql);

	List<Map<String, Object>> select(DataType type, String sql);

	void insert(DataType type, String sql, List<Map<String, Object>> datas);
	
	

}

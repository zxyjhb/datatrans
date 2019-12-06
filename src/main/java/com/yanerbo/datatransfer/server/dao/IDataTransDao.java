package com.yanerbo.datatransfer.server.dao;

import java.util.List;
import java.util.Map;

import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.entity.Page;


/**
 * 
 * @author jihaibo
 *
 */
public interface IDataTransDao {

	public int count(DataType type, String sql);

	public List<Map<String, Object>> select(DataType type, String sql);

	public void insertBatch(DataType type, String sql, List<Map<String, Object>> datas);

	public void insert(DataType type, String sql, Map<String, Object> data);

	public Page pageInfo(DataType type, String sql);
	
	

}

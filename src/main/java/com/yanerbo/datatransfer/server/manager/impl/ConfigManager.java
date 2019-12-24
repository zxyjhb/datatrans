package com.yanerbo.datatransfer.server.manager.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.server.manager.IConfigManager;
import com.yanerbo.datatransfer.support.util.DataSourceManager;

/**
 * 
 * @author 274818
 *
 */
@Component
public class ConfigManager implements IConfigManager{

	@Autowired
	private DataSourceManager dataSourceManager;
	
	private final static String create_table =
		"create table t_datatrans_config("
		+ "id int primary key not null, "
		+ "name 					varchar(100) not null, "
		+ "mode 					varchar(10), "
		+ "pagetype 				varchar(10), "
		+ "sourcetable  			varchar(100), "
		+ "sourcekey 				varchar(100), "
		+ "sourcecolumns    		clob,"
		+ "sourcesql       			clob, "
		+ "targettable       		varchar(100), "
		+ "targetkey       			varchar(100), "
		+ "targetcolumns       		clob, "
		+ "targetsql       			clob, "
		+ "pagecount       			int, "
		+ "maxthread       			int, "
		+ "cron       				varchar(200), "
		+ "shardingtotalcount       int, "
		+ "shardingitemparameters 	varchar(200) "
		+ ")";
	
	
	private void initTables(){
		try {
			Connection conn = dataSourceManager.getJdbcTemplate(DataType.target).getDataSource().getConnection();
			DatabaseMetaData dbMetaData = conn.getMetaData();
	        try (ResultSet resultSet = dbMetaData.getTables(null, null, table_datatrans_config, new String[]{"TABLE"})) {
	            System.out.println("resultSet:" + resultSet);
	        	if (!resultSet.next()) {
	                createJobExecutionTable(conn);
	            }
	        }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	 private void createJobExecutionTable(final Connection conn) throws SQLException {
		 String create_table =
					"create table t_datatrans_config("
					+ "id int primary key not null, "
					+ "name 					varchar(100) not null, "
					+ "mode 					varchar(10), "
					+ "pagetype 				varchar(10), "
					+ "sourcetable  			varchar(100), "
					+ "sourcekey 				varchar(100), "
					+ "sourcecolumns    		clob,"
					+ "sourcesql       			clob, "
					+ "targettable       		varchar(100), "
					+ "targetkey       			varchar(100), "
					+ "targetcolumns       		clob, "
					+ "targetsql       			clob, "
					+ "pagecount       			int, "
					+ "maxthread       			int, "
					+ "cron       				varchar(200), "
					+ "shardingtotalcount       int, "
					+ "shardingitemparameters 	varchar(200) "
					+ ")";
		 System.out.println(create_table);
	        try (PreparedStatement preparedStatement = conn.prepareStatement(create_table)) {
	            preparedStatement.execute();
	        }
	    }
	
	private final static String select_sql = "select * from t_datatrans_config";
	
	private final static String table_datatrans_config = "t_datatrans_config";
	
	
	private final static String insert_sql = "";
	
	private final static String update_sql = "";
	
	private final static String delete_sql = "";
	
	@Override
	public List<DataTrans> getConfigs() {
		initTables();
		return dataSourceManager.getJdbcTemplate(DataType.target).queryForList(select_sql, DataTrans.class);
	}

	public DataTrans getConfig(String name) {
		
		return dataSourceManager.getJdbcTemplate(DataType.target).queryForObject(select_sql, DataTrans.class);
	}
	
	@Override
	public void setConfig(DataTrans dataTrans) {
		
	}
	
	@Override
	public void insertConfig(DataTrans dataTrans) {
		// TODO Auto-generated method stub
//		dataSourceManager.getJdbcTemplate(DataType.config)
	}

	@Override
	public void updateConfig(DataTrans dataTrans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteConfig(DataTrans dataTrans) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
	
}

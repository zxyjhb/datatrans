package com.yanerbo.datatransfer.server.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.server.dao.IDataTransConfigDao;
import com.yanerbo.datatransfer.support.util.DataSourceUtil;

/**
 * 
 * @author 274818
 *
 */
@Repository
public class DataTransConfigDao implements IDataTransConfigDao{
	/**
	 * select
	 */
	private static final String config_select ="select name, mode, pageType, sourceTable, sourceKey, sourceColumns, sourceSql, targetTable, targetKey, targetColumns, targetSql, pageCount, maxThread, cron, shardingTotalCount, shardingItemParameters from t_datatrans_config";
	/**
	 * select
	 */
	private static final String config_select_by_name ="select name, mode, pageType, sourceTable, sourceKey, sourceColumns, sourceSql, targetTable, targetKey, targetColumns, targetSql, pageCount, maxThread, cron, shardingTotalCount, shardingItemParameters from t_datatrans_config where name=? ";
	/**
	 * update
	 */
	private static final String config_update ="update t_datatrans_config set name = ?, mode = ?, pageType = ?, sourceTable = ?, sourceKey = ?, sourceColumns = ?, sourceSql = ?, targetTable = ?, targetKey = ?, targetColumns = ?, targetSql = ?, pageCount = ?, maxThread = ?, cron = ?, shardingTotalCount = ?, shardingItemParameters = ? where name=? ";
	
	
	@Autowired
	private DataSourceUtil dataSourceUtil;
	
	@Override
	public DataTrans getDataTrans(String name) {
		return dataSourceUtil.getJdbcTemplate(DataType.target).queryForObject(config_select_by_name, new RowMapper<DataTrans>(){
			@Override
			public DataTrans mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataTrans dataTran = new DataTrans(); 
				dataTran.setCron(rs.getString("cron"));
				dataTran.setMaxThread(rs.getInt("maxthread"));
				dataTran.setMode(rs.getString("mode"));
				dataTran.setName(rs.getString("name"));
				dataTran.setPageCount(rs.getInt("pagecount"));
				dataTran.setPageType(rs.getString("pagetype"));
				dataTran.setShardingItemParameters(rs.getString("shardingitemparameters"));
				dataTran.setShardingTotalCount(rs.getInt("shardingtotalcount"));
				dataTran.setSourceColumns(rs.getString("sourcecolumns"));
				dataTran.setSourceKey(rs.getString("sourcekey"));
				dataTran.setSourceSql(rs.getString("sourcesql"));
				dataTran.setSourceTable(rs.getString("sourcetable"));
				dataTran.setTargetColumns(rs.getString("targetcolumns"));
				dataTran.setTargetKey(rs.getString("targetkey"));
				dataTran.setTargetSql(rs.getString("targetsql"));
				dataTran.setTargetTable(rs.getString("targettable"));
				return dataTran;
			}
		}, name);
	}

	@Override
	public void updateDataTrans(DataTrans dataTrans) {
		
		dataSourceUtil.getJdbcTemplate(DataType.target).update(config_update, new PreparedStatementSetter(){
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1, dataTrans.getName());
				ps.setString(2, dataTrans.getMode());
				ps.setString(3, dataTrans.getPageType());
				ps.setString(4, dataTrans.getSourceTable());
				ps.setString(5, dataTrans.getSourceKey());
				ps.setString(6, dataTrans.getSourceColumns());
				ps.setString(7, dataTrans.getSourceSql());
				ps.setString(8, dataTrans.getTargetTable());
				ps.setString(9, dataTrans.getTargetKey());
				ps.setString(10, dataTrans.getTargetColumns());
				ps.setString(11, dataTrans.getTargetSql());
				ps.setInt(12, dataTrans.getPageCount());
				ps.setInt(13, dataTrans.getMaxThread());
				ps.setString(14, dataTrans.getCron());
				ps.setInt(15, dataTrans.getShardingTotalCount());
				ps.setString(16, dataTrans.getShardingItemParameters());
				ps.setString(17, dataTrans.getName());
			}
		});
	}



	@Override
	public List<DataTrans> getDataTransList() {
		return dataSourceUtil.getJdbcTemplate(DataType.target).query(config_select, new RowMapper<DataTrans>(){
			@Override
			public DataTrans mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataTrans dataTran = new DataTrans(); 
				dataTran.setCron(rs.getString("cron"));
				dataTran.setMaxThread(rs.getInt("maxthread"));
				dataTran.setMode(rs.getString("mode"));
				dataTran.setName(rs.getString("name"));
				dataTran.setPageCount(rs.getInt("pagecount"));
				dataTran.setPageType(rs.getString("pagetype"));
				dataTran.setShardingItemParameters(rs.getString("shardingitemparameters"));
				dataTran.setShardingTotalCount(rs.getInt("shardingtotalcount"));
				dataTran.setSourceColumns(rs.getString("sourcecolumns"));
				dataTran.setSourceKey(rs.getString("sourcekey"));
				dataTran.setSourceSql(rs.getString("sourcesql"));
				dataTran.setSourceTable(rs.getString("sourcetable"));
				dataTran.setTargetColumns(rs.getString("targetcolumns"));
				dataTran.setTargetKey(rs.getString("targetkey"));
				dataTran.setTargetSql(rs.getString("targetsql"));
				dataTran.setTargetTable(rs.getString("targettable"));
				return dataTran;
			}
		});
	}

}

package com.yanerbo.datatransfer.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import com.yanerbo.datatransfer.entity.DataTrans;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.server.dao.IDataTransConfigDao;
import com.yanerbo.datatransfer.support.util.DataSourceUtil;
import com.yanerbo.datatransfer.support.util.SqlUtil;

/**
 * 
 * @author 274818
 *
 */
public class DataTransConfigDao implements IDataTransConfigDao{

	
	@Autowired
	private DataSourceUtil dataSourceUtil;
	
	@Override
	public List<DataTrans> getDataTrans() {
		
		return dataSourceUtil.getJdbcTemplate(DataType.target).query(SqlUtil.getConfigSql(), new RowMapper<DataTrans>(){
			@Override
			public DataTrans mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataTrans dataTran = new DataTrans(); 
				dataTran.setCron(rs.getString("cron"));
				dataTran.setMaxThread(rs.getInt("maxthread"));
				dataTran.setMode(rs.getString("mode"));
				dataTran.setName(rs.getString("name"));
				dataTran.setPageCount(rs.getInt("pagecount"));
				dataTran.setPageType(rs.getString("pagetype"));
				dataTran.setShardingItemParameters(rs.getString("ShardingItemParameters"));
				dataTran.setShardingTotalCount(rs.getInt("ShardingTotalCount"));
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

	@Override
	public void updateDataTrans(DataTrans dataTrans) {
	}

}

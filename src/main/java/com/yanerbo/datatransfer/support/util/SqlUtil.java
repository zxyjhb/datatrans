package com.yanerbo.datatransfer.support.util;

import java.util.Arrays;
import com.yanerbo.datatransfer.entity.DataTrans;

/**
 * 
 * @author jihaibo
 *
 */
public class SqlUtil {
	
	
	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String getConfigSql() {
		return "select * from t_datatrans_config";
	}
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String getAllCount(String tableName) {
		return "select count(1) from " + tableName;
	}
	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String delete(String tableName) {
		return "delete from " + tableName;
	}
	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String truncate(String tableName) {
		return "truncate table " + tableName;
	}
	/**
	 * 分片
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @return
	 */
	public static String getAllCount(String tableName, String key, int shardingItem, int shardingTotal) {
		return "select count(1) from " + tableName + " where mod(" + key + "," + shardingTotal + ") = " + shardingItem;
	}
	
	public static String getPagePost(String tableName, String key, int shardingItem, int shardingTotal,int start, int pageCount) {
		return "select min("+ key+ ") pageStart, max(" + key + ") pageEnd, count(1) totalCount from " 
				+ tableName + " where mod(" + key + "," + shardingTotal + ") = " + shardingItem
				+ " and "+ key+ " >= " + start + " and rownum<= " + pageCount;
	}
	
	
	public static String getInitPagePost(String tableName, String key, int shardingItem, int shardingTotal,int start, int pageCount) {
		return "select min("+ key+ ") pageStart, max(" + key + ") pageEnd, count(1) totalCount from " 
				+ tableName + " where mod(" + key + "," + shardingTotal + ") = " + shardingItem
				+ " and "+ key+ " >= " + start + " and rownum<= " + pageCount;
	}
	
	public static String getPageInfo(String tableName, String key, int shardingItem, int shardingTotal) {
		return "select min("+ key+ ") startPostPage, max(" + key + ") endPostPage, count(1) totalCount from " + tableName + " where mod(" + key + "," + shardingTotal + ") = " + shardingItem;
	}
	
	/**
	 * 分片
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @return
	 */
	public static String getMaxKey(DataTrans entity,int shardingItem, int shardingTotal, int currentPage) {
		
		/**
		 * 如果表名和字段名称为null，那么就从sql进行构建
		 */
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select max(").append(entity.getSourceKey()).append(") from ").append(entity.getSourceTable());
		//添加分片分页信息
		sqlBuilder.append(" where mod(" + entity.getSourceKey() + "," + shardingTotal + ") = " + shardingItem);
		sqlBuilder.append(" and ").append(entity.getSourceKey()).append(" >= ").append(currentPage);
		sqlBuilder.append(" and rownum<= ").append(entity.getPageCount());
		System.out.println(sqlBuilder.toString());
		return sqlBuilder.toString();
	}
	
	
	public static String getOracleAllTable() {
		return "select table_name from user_tables";
	}
	
	public static String getMysqlAllTable() {
		return null;
	}
	
	public static String getMysqlColumnsByTable(String table) {
		return null;
	}
	
	public static String getOralceColumnsByTable(String table) {
		return null;
	}
	
	private static boolean isNotEmpty(String sql) {
		if(sql == null || sql.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public static String builderSelect(DataTrans entity,int shardingItem, int shardingTotal, int start, int end) {
		/**
		 * 如果表名和字段名称为null，那么就从sql进行构建
		 */
		StringBuilder sqlBuilder = new StringBuilder();
		if(isNotEmpty(entity.getSourceTable()) && isNotEmpty(entity.getSourceColumns())){
			sqlBuilder.append("select ").append(entity.getSourceColumns()).append(" from ").append(entity.getSourceTable());
		}else {
			sqlBuilder.append(entity.getSourceSql());
		}
		//添加分片分页信息
		sqlBuilder.append(" where mod(" + entity.getSourceKey() + "," + shardingTotal + ") = " + shardingItem);
		sqlBuilder.append(" and ").append(entity.getSourceKey()).append(" >= ").append(start);
		sqlBuilder.append(" and ").append(entity.getSourceKey()).append(" < ").append(end);
//		sqlBuilder.append(" and rownum<= ").append(entity.getPageCount());
		System.out.println(sqlBuilder.toString());
		return sqlBuilder.toString();
	}
	
	public static String builderInsert(DataTrans entity) {
		if(isNotEmpty(entity.getTargetTable()) && isNotEmpty(entity.getTargetColumns())){
			return builderInsert(entity.getTargetTable(), entity.getTargetColumns());
		}
		return entity.getTargetSql();
	}
	
	public static String builderInsert(String table, String columns){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("insert  into ").append(table).append(" (").append(columns).append(") values (");
		String[] fields = columns.split(",");
		for(int i=1; i<=fields.length; i++) {
			if(i<fields.length) {
				sqlBuilder.append("?,");
			}else {
				sqlBuilder.append("?)");
			}
		}
		return sqlBuilder.toString();
	}
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static String[] getFields(String sql){
		
		int startIndex = sql.toUpperCase().indexOf("(");
		int endIndex = sql.toUpperCase().indexOf(")");
		String[] fields = sql.substring(startIndex+"(".length(), endIndex).split(",");
		return fields;
		
	}
	
	
	public static void main(String[] args) {
		
		String sql = "insert into t_cmc_cust_account (createtime, createuserid, lastupdatetime, lastupdateuserid, custnumber, custname, bankaccount, acountname, relation, accountnature, accounttype, accountuse, isdefaultaccount, linkmanmobile, linkmanphone, bankname, bankid, bankcode, subbankid, subbankcode, subbankname, bankprovinceid, bankprovicecode, bankprovincename, bankcityid, bankcitycode, bankcityname, bankareaid, bankareacode, bankareaname, financelinkman, financelinkmanid, status) values()";
		System.out.println(Arrays.toString(getFields(sql)));
		
	}
	
}

package com.yanerbo.datatransfer.shared.util;

import com.yanerbo.datatransfer.shared.domain.DataTrans;
import com.yanerbo.datatransfer.shared.domain.PageType;

/**
 * 
 * sql构建类
 * @author jihaibo
 *
 */
public class SqlUtil {
	
	/**
	 * 获取指定表名总数据量
	 */
	private final static String sql_all_count = "select count(1) from %s";
	
	/**
	 * 获取指定表名总数据量(分片)
	 */
	private final static String sql_all_count_sharding = "select count(1) from %s where mod(%s,%s) = %s";
	
	/**
	 * 删除指定表数据
	 */
	
	private final static String sql_delete = "delete from %s";
	/**
	 * 清空指定表数据
	 */
	
	private final static String sql_truncate = "truncate table %s";
	
	/**
	 * 标记位置
	 */
	private final static String sql_signpagepost = "select min(%s) pageStart, max(%s) pageEnd, count(1) totalCount from %s";
	/**
	 * 分页位置
	 */
	private final static String sql_pagepost = "select min(%s) pageStart, max(%s) pageEnd, count(1) totalCount from (select %s from %s where %s >= %s and rownum<= %s) %s";

	/**
	 * 标记位置（分片）
	 */
	private final static String sql_signpagepost_sharding = "select min(%s) pageStart, max(%s) pageEnd, count(1) totalCount from %s where mod(%s,%s) = %s";

	/**
	 * 分页位置（分片）
	 */
	private final static String sql_pagepost_sharding = "select min(%s) pageStart, max(%s) pageEnd, count(1) totalCount from ( select %s from %s where mod(%s,%s) = %s and %s >= %s and rownum<= %s) %s";

	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String allCount(String tableName) {
		return String.format(sql_all_count, tableName);
	}
	
	/**
	 * 分片
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @return
	 */
	public static String allCountSharding(String tableName, String key, int shardingItem, int shardingTotal) {
		return String.format(sql_all_count_sharding, tableName, key, shardingItem, shardingTotal);
	}
	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String delete(String tableName) {
		return String.format(sql_delete, tableName);
	}
	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String truncate(String tableName) {
		return String.format(sql_truncate, tableName);
	}
	
	/**
	 * 获取初始分页信息
	 * @param tableName
	 * @param key
	 * @param start
	 * @param pageCount
	 * @return
	 */
	public static String getSignPagePost(String tableName, String key) {
		return String.format(sql_signpagepost, key, key, tableName);
	}
	/**
	 * 获取分页信息
	 * @param tableName
	 * @param key
	 * @param start
	 * @param pageCount
	 * @return
	 */
	public static String getPagePost(String tableName, String key, int start, int pageCount) {
		return String.format(sql_pagepost, key, key, key, tableName, key, start, pageCount, tableName);
	}
	/**
	 * 
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @return
	 */
	public static String getSignPagePostSharding(String tableName, String key, int shardingItem, int shardingTotal) {
		return String.format(sql_signpagepost_sharding, key, key, tableName, key, shardingTotal, shardingItem);
	}

	/**
	 * 分页（分片）
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @param start
	 * @param pageCount
	 * @return
	 */
	public static String getPagePostSharding(String tableName, String key, int shardingItem, int shardingTotal,int start, int pageCount) {
		return String.format(sql_pagepost_sharding, key, key, key, tableName, key, shardingTotal, shardingItem, key, start, pageCount, tableName);
	}
	
	/**
	 * 字段是否为空
	 * @param str
	 * @return
	 */
	private static boolean isNotEmpty(String str) {
		if(str == null || str.isEmpty()) {
			return false;
		}
		return true;
	}
	/**
	 * 
	 * 构建查询语句
	 */
	public static String builderSelect(DataTrans entity, int start, int end,int shardingItem, int shardingTotal) {
		/**
		 * 如果表名和字段名称为null，那么就从sql进行构建
		 */
		StringBuilder sqlBuilder = new StringBuilder();
		if(isNotEmpty(entity.getSourceTable()) && isNotEmpty(entity.getSourceColumns())){
			sqlBuilder.append("select ").append(entity.getSourceColumns()).append(" from ").append(entity.getSourceTable());
		}else {
			sqlBuilder.append(entity.getSourceSql());
		}
		sqlBuilder.append(" where ").append(entity.getSourceKey()).append(" >= ").append(start);
		sqlBuilder.append(" and ").append(entity.getSourceKey()).append(" < ").append(end);
		if(PageType.post_sharding.name().equals(entity.getPageType()) || PageType.seq_sharding.name().equals(entity.getPageType())) {
			//添加分片分页信息
			sqlBuilder.append(" and mod(" + entity.getSourceKey() + "," + shardingTotal + ") = " + shardingItem);
		}
		System.out.println(sqlBuilder.toString());
		return sqlBuilder.toString();
	}
	/**
	 * 构建插入语句
	 * @param entity
	 * @return
	 */
	public static String builderInsert(DataTrans entity) {
		if(isNotEmpty(entity.getTargetTable()) && isNotEmpty(entity.getTargetColumns())){
			return builderInsert(entity.getTargetTable(), entity.getTargetColumns());
		}
		return entity.getTargetSql();
	}
	/**
	 * 构建插入语句
	 * @param entity
	 * @return
	 */
	public static String builderInsert(String table, String columns){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("insert into ").append(table).append(" (").append(columns).append(") values (");
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
	 * 获取字段列表
	 * @param sql
	 * @return
	 */
	public static String[] getInsertFields(String sql){
		
		try {
			int startIndex = sql.toUpperCase().indexOf("(");
			int endIndex = sql.toUpperCase().indexOf(")");
			String[] fields = sql.substring(startIndex+"(".length(), endIndex).split(",");
			return fields;
		}catch(Exception e) {
			return null;
		}
		
	}
	
}

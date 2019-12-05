package com.yanerbo.datatransfer.entity;

/**
 * 
 * @author jihaibo
 *
 */
public class DataTransEntity {
	
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 运行模式
	 */
	private String mode;
	/**
	 * 源表名
	 */
	private String sourceTable;
	/**
	 * 源表主键（分片字段）
	 */
	private String sourceKey;
	/**
	 * 源表字段
	 */
	private String sourceColumns;
	/**
	 * 源sql
	 */
	private String sourceSql;
	/**
	 * 目标表名
	 */
	private String targetTable;
	/**
	 * 目标表字段
	 */
	private String targetColumns;
	/**
	 * 目标sql
	 */
	private String targetSql;
	
	/**
	 * 分页
	 */
	private int pageCount;
	/**
	 * 最大线程数
	 */
	private int maxThread;
	
	/**
	 * 执行表达式
	 */
	private String cron;
	/**
	 * 分片数量
	 */
	private int shardingTotalCount;
	/**
	 * 分片参数
	 */
	private String shardingItemParameters;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getSourceTable() {
		return sourceTable;
	}
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}
	
	public String getSourceKey() {
		return sourceKey;
	}
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	public String getSourceSql() {
		return sourceSql;
	}
	public void setSourceSql(String sourceSql) {
		this.sourceSql = sourceSql;
	}
	public String getTargetTable() {
		return targetTable;
	}
	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}
	public String getTargetSql() {
		return targetSql;
	}
	public void setTargetSql(String targetSql) {
		this.targetSql = targetSql;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getMaxThread() {
		return maxThread;
	}
	public void setMaxThread(int maxThread) {
		this.maxThread = maxThread;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public int getShardingTotalCount() {
		return shardingTotalCount;
	}
	public void setShardingTotalCount(int shardingTotalCount) {
		this.shardingTotalCount = shardingTotalCount;
	}
	public String getShardingItemParameters() {
		return shardingItemParameters;
	}
	public void setShardingItemParameters(String shardingItemParameters) {
		this.shardingItemParameters = shardingItemParameters;
	}
	public String getSourceColumns() {
		return sourceColumns;
	}
	public void setSourceColumns(String sourceColumns) {
		this.sourceColumns = sourceColumns;
	}
	public String getTargetColumns() {
		return targetColumns;
	}
	public void setTargetColumns(String targetColumns) {
		this.targetColumns = targetColumns;
	}
	@Override
	public String toString() {
		return "DataTransEntity [name=" + name + ", mode=" + mode + ", sourceTable=" + sourceTable + ", sourceKey="
				+ sourceKey + ", sourceColumns=" + sourceColumns + ", sourceSql=" + sourceSql + ", targetTable="
				+ targetTable + ", targetColumns=" + targetColumns + ", targetSql=" + targetSql + ", pageCount="
				+ pageCount + ", maxThread=" + maxThread + ", cron=" + cron + ", shardingTotalCount="
				+ shardingTotalCount + ", shardingItemParameters=" + shardingItemParameters + "]";
	}
	

}

package com.yanerbo.datatransfer.shared.domain;

/**
 * 
 * @author jihaibo
 *
 */
public class DataTrans {
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 运行模式（all全量、add增量、none不运行）
	 */
	private String mode;
	/**
	 * 分页模式（post起始位置分页、seq顺序分页）
	 */
	private String pageType;
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
	 * 源表主键（分片字段）
	 */
	private String targetKey;
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
	 * 数据提交方式
	 */
	private String commitType;
	
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
	
	public String getPageType() {
		return pageType;
	}
	public void setPageType(String pageType) {
		this.pageType = pageType;
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
	
	public String getTargetKey() {
		return targetKey;
	}
	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
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
	
	public String getCommitType() {
		return commitType;
	}
	public void setCommitType(String commitType) {
		this.commitType = commitType;
	}
	/**
	 * 
	 * @author jihaibo
	 *
	 */
	class RuntimeObject {
		/**
		 * 运行开始时间
		 */
		private long startTime;
		/**
		 * 运行最小位置
		 */
		private int minPos;
		/**
		 * 运行最大位置
		 */
		private int maxPos;
		/**
		 * 运行状态
		 */
		private String status;
		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		public int getMinPos() {
			return minPos;
		}
		public void setMinPos(int minPos) {
			this.minPos = minPos;
		}
		public int getMaxPos() {
			return maxPos;
		}
		public void setMaxPos(int maxPos) {
			this.maxPos = maxPos;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
	}
	@Override
	public String toString() {
		return "DataTrans [name=" + name + ", mode=" + mode + ", pageType=" + pageType + ", sourceTable=" + sourceTable
				+ ", sourceKey=" + sourceKey + ", sourceColumns=" + sourceColumns + ", sourceSql=" + sourceSql
				+ ", targetTable=" + targetTable + ", targetKey=" + targetKey + ", targetColumns=" + targetColumns
				+ ", targetSql=" + targetSql + ", pageCount=" + pageCount + ", maxThread=" + maxThread + ", commitType="
				+ commitType + ", cron=" + cron + ", shardingTotalCount=" + shardingTotalCount
				+ ", shardingItemParameters=" + shardingItemParameters + "]";
	}
	
	

}

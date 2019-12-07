package com.yanerbo.datatransfer.entity;

/**
 * 
 * @author 274818
 *
 */
public final class Page {
	/**
	 * 当前页
	 */
	private volatile int currentPage;
	/**
	 * 起始位置
	 */
	private volatile int pageStart;
	/**
	 * 结束位置
	 */
	private volatile int pageEnd;
	/**
	 * 总数
	 */
	private volatile int totalCount;
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageStart() {
		return pageStart;
	}
	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}
	public int getPageEnd() {
		return pageEnd;
	}
	public void setPageEnd(int pageEnd) {
		this.pageEnd = pageEnd;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	@Override
	public String toString() {
		return " Page [currentPage=" + currentPage + ", pageStart=" + pageStart + ", pageEnd=" + pageEnd
				+ ", totalCount=" + totalCount + "]";
	}
	
}

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
	private int currentPage = 0;
	/**
	 * 起始位置
	 */
	private int pageStart = 0;
	/**
	 * 结束位置
	 */
	private int pageEnd = 0;
	/**
	 * 总数
	 */
	private int totalCount = 0;
	/**
	 * 
	 */
	public void setEmpty(){
		currentPage = 0;
		pageStart = 0;
		pageEnd = 0;
		totalCount = 0; 
	}
	
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

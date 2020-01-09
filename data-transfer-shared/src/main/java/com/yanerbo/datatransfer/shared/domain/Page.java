package com.yanerbo.datatransfer.shared.domain;

/**
 * 
 * @author 274818
 *
 */
public final class Page {
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
		
		this.pageStart = 0;
		this.pageEnd = 0;
		this.totalCount = 0; 
	}
	
	public Page() {
		
	}
	
	public Page(int pageStart, int pageEnd, int totalCount) {
		super();
		this.pageStart = pageStart;
		this.pageEnd = pageEnd;
		this.totalCount = totalCount;
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
		return "Page [pageStart=" + pageStart + ", pageEnd=" + pageEnd + ", totalCount=" + totalCount + "]";
	}
}

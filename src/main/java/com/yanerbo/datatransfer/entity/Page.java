package com.yanerbo.datatransfer.entity;

/**
 * 
 * @author 274818
 *
 */
public class Page {
	
	/**
	 * 当前页
	 */
	private int currentPage;
	/**
	 * 起始位置
	 */
	private int startPostPage;
	/**
	 * 结束位置
	 */
	private int endPostPage;
	/**
	 * 总数
	 */
	private int totalCount;
	
	public Page(){
		
	}
	
	public Page(int currentPage, int startPostPage, int endPostPage, int totalCount) {
		super();
		this.currentPage = currentPage;
		this.startPostPage = startPostPage;
		this.endPostPage = endPostPage;
		this.totalCount = totalCount;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getStartPostPage() {
		return startPostPage;
	}
	public void setStartPostPage(int startPostPage) {
		this.startPostPage = startPostPage;
	}
	public int getEndPostPage() {
		return endPostPage;
	}
	public void setEndPostPage(int endPostPage) {
		this.endPostPage = endPostPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	@Override
	public String toString() {
		return "Page [currentPage=" + currentPage + ", startPostPage=" + startPostPage + ", endPostPage=" + endPostPage
				+ ", totalCount=" + totalCount + "]";
	}
}

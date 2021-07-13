package com.moglix.wms.api.response;

public abstract class PaginationResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8002174831014578605L;
	
	private Integer pageNumber;
	private Integer pageCount;
	private Integer totalCount;
	private Integer size;
	private String prev;
	private String next;

	public PaginationResponse(String message, boolean status, int code) {
		super(message, status, code);
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getPageCount() {
		return pageCount;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getPrev() {
		return prev;
	}
	public void setPrev(String prev) {
		this.prev = prev;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}

}

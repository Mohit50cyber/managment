package com.moglix.wms.api.response;

import java.util.List;

public class BatchRefResponse  extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8672513050435115875L;
	private List<String> refNumbers;
	public BatchRefResponse(List<String> refNumbers) {
		super();
		this.refNumbers = refNumbers;
	}
	public BatchRefResponse() {
		// TODO Auto-generated constructor stub
	}
	public List<String> getRefNumbers() {
		return refNumbers;
	}
	public void setRefNumbers(List<String> refNumbers) {
		this.refNumbers = refNumbers;
	}
	
	

}

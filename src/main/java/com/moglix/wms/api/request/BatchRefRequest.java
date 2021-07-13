package com.moglix.wms.api.request;

import java.util.List;

public class BatchRefRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5579159781802932625L;
	private List<String> refNumbers;
	private String originalFileName;
	
	
	public void setRefNumbers(List<String> refNumbers) {
		this.refNumbers = refNumbers;
	}

	public List<String> getRefNumbers() {
		return refNumbers;
	}

	
	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	@Override
	public String toString() {
		return "BatchRefRequest [refNumbers=" + refNumbers + ", originalFileName=" + originalFileName
				+ ", getRefNumbers()=" + getRefNumbers() + ", getOriginalFileName()=" + getOriginalFileName()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

	
}

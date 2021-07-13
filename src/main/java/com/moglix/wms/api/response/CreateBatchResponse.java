package com.moglix.wms.api.response;

public class CreateBatchResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1882144558541012146L;

	public CreateBatchResponse(String message, boolean status, int code) {
		super(message, status, code);
	}

	private Integer batchId;
	

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
}

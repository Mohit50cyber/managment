package com.moglix.wms.api.response;

public class ReturnBatchResponse extends BaseResponse {

	public ReturnBatchResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8874412246889055418L;

}

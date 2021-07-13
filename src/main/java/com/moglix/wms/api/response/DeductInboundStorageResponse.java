package com.moglix.wms.api.response;

public class DeductInboundStorageResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3812649966579054687L;
	
	public DeductInboundStorageResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}

}

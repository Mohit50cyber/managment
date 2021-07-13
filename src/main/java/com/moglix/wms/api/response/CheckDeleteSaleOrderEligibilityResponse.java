package com.moglix.wms.api.response;

public class CheckDeleteSaleOrderEligibilityResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5645307997675354855L;

	public CheckDeleteSaleOrderEligibilityResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
}

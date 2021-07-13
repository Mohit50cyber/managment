package com.moglix.wms.api.response;

public class CreateCustomerDebitNoteResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7098478098823628867L;
	
	public CreateCustomerDebitNoteResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}

}

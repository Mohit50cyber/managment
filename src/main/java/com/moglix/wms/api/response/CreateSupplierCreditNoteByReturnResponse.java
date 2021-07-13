package com.moglix.wms.api.response;

public class CreateSupplierCreditNoteByReturnResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4969495960116602264L;

	public CreateSupplierCreditNoteByReturnResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}	
}

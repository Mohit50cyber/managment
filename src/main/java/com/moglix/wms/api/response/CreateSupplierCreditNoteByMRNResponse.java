package com.moglix.wms.api.response;

public class CreateSupplierCreditNoteByMRNResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3187446432689919398L;

	public CreateSupplierCreditNoteByMRNResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
}

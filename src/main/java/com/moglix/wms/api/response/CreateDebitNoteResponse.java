package com.moglix.wms.api.response;

public class CreateDebitNoteResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4103130902883016708L;

	public CreateDebitNoteResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
}

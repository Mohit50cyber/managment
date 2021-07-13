package com.moglix.wms.api.response;

public class CreatePacketResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4988881201438155977L;

	public CreatePacketResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
}

package com.moglix.wms.api.response;

public class DNStatusUpdateResponse extends BaseResponse{

	private static final long serialVersionUID = -4969495960116602264L;

	public DNStatusUpdateResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}	
}

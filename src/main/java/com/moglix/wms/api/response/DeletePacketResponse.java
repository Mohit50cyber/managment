package com.moglix.wms.api.response;

public class DeletePacketResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5466684046007115511L;
	
	public DeletePacketResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}

}

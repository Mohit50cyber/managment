package com.moglix.wms.api.response;

import com.moglix.wms.dto.InboundDTO;

public class GetInboundByIdResponse extends BaseResponse{

	/**
	 * 
	 */
	public GetInboundByIdResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
	private static final long serialVersionUID = -7973923208872165748L;

	private InboundDTO inbound;

	public InboundDTO getInbound() {
		return inbound;
	}

	public void setInbound(InboundDTO inbound) {
		this.inbound = inbound;
	}

	
}

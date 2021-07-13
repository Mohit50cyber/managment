package com.moglix.wms.api.response;

import java.util.List;

public class GetInboundByPoItemIdResponse extends BaseResponse{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2020106399962662639L;

	public GetInboundByPoItemIdResponse(String message, boolean status, int code) {
		super.setCode(code);
		super.setMessage(message);
		super.setStatus(status);
	}
	
	public GetInboundByPoItemIdResponse() {
		// TODO Auto-generated constructor stub
	}

	private List<Integer> inboundIds;

	public List<Integer> getInboundIds() {
		return inboundIds;
	}

	public void setInboundIds(List<Integer> inboundIds) {
		this.inboundIds = inboundIds;
	}

	@Override
	public String toString() {
		return "GetInboundByPoItemIdResponse [inboundIds=" + inboundIds + "]";
	}
	
	
	

}

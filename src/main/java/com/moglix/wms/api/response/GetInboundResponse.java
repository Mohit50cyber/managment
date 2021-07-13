package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.InboundDTO;

public class GetInboundResponse extends PaginationResponse {
	
	
	public GetInboundResponse(String message, boolean status, int code) {
		super(message, status, code);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4840778591918242918L;
	private List<InboundDTO>inbounds = new ArrayList<>();
	
	public List<InboundDTO> getInbounds() {
		return inbounds;
	}
	public void setInbounds(List<InboundDTO> inbounds) {
		this.inbounds = inbounds;
	}
	
}

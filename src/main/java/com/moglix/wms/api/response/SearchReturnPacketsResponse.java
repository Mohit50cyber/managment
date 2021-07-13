package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ReturnPacketDTO;

public class SearchReturnPacketsResponse extends PaginationResponse {

	public SearchReturnPacketsResponse(String message, boolean status, int code) {
		super(message, status, code);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4118925030355238459L;
	
	private List<ReturnPacketDTO> returnPackets = new ArrayList<>();

	public List<ReturnPacketDTO> getReturnPackets() {
		return returnPackets;
	}

	public void setReturnPackets(List<ReturnPacketDTO> returnPackets) {
		this.returnPackets = returnPackets;
	}

}

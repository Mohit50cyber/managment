package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class DeletePacketRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8298629269847134511L;

	@NotNull
	private Integer emsPacketId;

	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}


	
	
}

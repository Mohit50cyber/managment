package com.moglix.wms.controller;

import java.util.List;

import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GeneratePickupListResponse;



public class BulkGeneratePickupListResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8858417023085889751L;

	private List<GeneratePickupListResponse> bulkPickupListResponse;

	public List<GeneratePickupListResponse> getPickupListResponse() {
		return bulkPickupListResponse;
	}

	public void setPickupListResponse(List<GeneratePickupListResponse> pickupListResponse) {
		this.bulkPickupListResponse = pickupListResponse;
	}
	
	public BulkGeneratePickupListResponse(String message, boolean status, int code) {
		
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
}

package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ProductInventoryData;

public class GetInventoryAvailabilityResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9014214789769304683L;

	public GetInventoryAvailabilityResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	public GetInventoryAvailabilityResponse() {
		
	}
	
	private List<ProductInventoryData> data = new ArrayList<>();

	public List<ProductInventoryData> getData() {
		return data;
	}

	public void setData(List<ProductInventoryData> data) {
		this.data = data;
	}
}

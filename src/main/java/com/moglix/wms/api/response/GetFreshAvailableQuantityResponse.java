package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.QuantityDetail;

public class GetFreshAvailableQuantityResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1739496646188830002L;

	
	public GetFreshAvailableQuantityResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	List<QuantityDetail> data = new ArrayList<>();


	public List<QuantityDetail> getData() {
		return data;
	}

	public void setData(List<QuantityDetail> data) {
		this.data = data;
	}
}

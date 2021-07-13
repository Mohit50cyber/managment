package com.moglix.wms.api.response;

import java.util.List;

import com.moglix.wms.dto.ReturnDetail;

public class GetReturnDetailsResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8136597016554315729L;
	
	public GetReturnDetailsResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	private List<ReturnDetail> returnDetails;

	public List<ReturnDetail> getReturnDetails() {
		return returnDetails;
	}

	public void setReturnDetails(List<ReturnDetail> returnDetails) {
		this.returnDetails = returnDetails;
	}
}

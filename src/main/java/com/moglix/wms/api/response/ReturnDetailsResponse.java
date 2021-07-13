package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ReturnDetail;

public class ReturnDetailsResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4121624442005430616L;

	public ReturnDetailsResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	private List<ReturnDetail> returnDetails = new ArrayList<>();

	public List<ReturnDetail> getReturnDetails() {
		return returnDetails;
	}

	public void setReturnDetails(List<ReturnDetail> returnDetails) {
		this.returnDetails = returnDetails;
	}
}

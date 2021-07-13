package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

public class ReturnInvoiceLotResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6243152360013961691L;
	
	private List<ReturnLotInfoDetailsMSNwise> msnwiseLotDetails = new ArrayList<>();
	
	

	public ReturnInvoiceLotResponse(List<ReturnLotInfoDetailsMSNwise> msnwiseLotDetails) {
		super();
		this.msnwiseLotDetails = msnwiseLotDetails;
	}
	
	

	public ReturnInvoiceLotResponse() {
		super();
		// TODO Auto-generated constructor stub
	}



	public ReturnInvoiceLotResponse(String message, boolean status, int code) {
		super(message, status, code);
		// TODO Auto-generated constructor stub
	}



	public List<ReturnLotInfoDetailsMSNwise> getMsnwiseLotDetails() {
		return msnwiseLotDetails;
	}

	public void setMsnwiseLotDetails(List<ReturnLotInfoDetailsMSNwise> msnwiseLotDetails) {
		this.msnwiseLotDetails = msnwiseLotDetails;
	}

	
	
}

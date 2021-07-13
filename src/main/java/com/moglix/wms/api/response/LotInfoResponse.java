package com.moglix.wms.api.response;

import java.util.List;

public class LotInfoResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3073984483770190758L;
	
	private List<InboundLotInfo> lotInfo;
	
	
	
	public LotInfoResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LotInfoResponse(String message, boolean status, int code) {
		super(message, status, code);
		// TODO Auto-generated constructor stub
	}
	
	

	public LotInfoResponse(List<InboundLotInfo> lotInfo) {
		super();
		this.lotInfo = lotInfo;
	}



	public static class InboundLotInfo{
		private Double quantity;
		private String lotNumber;
		private Integer lotId;
		public Double getQuantity() {
			return quantity;
		}
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
		public String getLotNumber() {
			return lotNumber;
		}
		public void setLotNumber(String lotNumber) {
			this.lotNumber = lotNumber;
		}
		public Integer getLotId() {
			return lotId;
		}
		public void setLotId(Integer lotId) {
			this.lotId = lotId;
		}
		
		
		
	}

	public List<InboundLotInfo> getLotInfo() {
		return lotInfo;
	}

	public void setLotInfo(List<InboundLotInfo> lotInfo) {
		this.lotInfo = lotInfo;
	}
	
	

}

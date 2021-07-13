package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

public class ReturnLotInfoDetailsMSNwise {
	 
	private String productMsn;
	
	private List<ReturnLotInfo> lotDetails = new ArrayList<>();
	
	
	
	public String getProductMsn() {
		return productMsn;
	}



	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}



	public List<ReturnLotInfo> getLotDetails() {
		return lotDetails;
	}



	public void setLotDetails(List<ReturnLotInfo> lotDetails) {
		this.lotDetails = lotDetails;
	}



	public static class ReturnLotInfo{
		
		private String lotNumber;
		private Double quantity;
		public String getLotNumber() {
			return lotNumber;
		}
		public void setLotNumber(String lotNumber) {
			this.lotNumber = lotNumber;
		}
		public Double getQuantity() {
			return quantity;
		}
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
		
		
		
		
	}

}

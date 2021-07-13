package com.moglix.wms.api.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetTPByEmsPacketIdResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6017228813662108543L;

	private List<OrderItemIdTPMapping> transferPrices = new ArrayList<>();
	
	public static class OrderItemIdTPMapping implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -781527011793508493L;
		
		private Integer emsOrderItemId;
		
		private Double transferPrice;

		public Integer getEmsOrderItemId() {
			return emsOrderItemId;
		}

		public void setEmsOrderItemId(Integer emsOrderItemId) {
			this.emsOrderItemId = emsOrderItemId;
		}

		public Double getTransferPrice() {
			return transferPrice;
		}

		public void setTransferPrice(Double transferPrice) {
			this.transferPrice = transferPrice;
		}
	}

	public List<OrderItemIdTPMapping> getTransferPrices() {
		return transferPrices;
	}

	public void setTransferPrices(List<OrderItemIdTPMapping> transferPrices) {
		this.transferPrices = transferPrices;
	}
}

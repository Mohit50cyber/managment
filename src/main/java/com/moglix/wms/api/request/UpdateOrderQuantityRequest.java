package com.moglix.wms.api.request;

public class UpdateOrderQuantityRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1775457572279442435L;
	
	private Integer emsOrderItemId;
	
	private Double updatedOrderQuantity;

	public Double getUpdatedOrderQuantity() {
		return updatedOrderQuantity;
	}

	public void setUpdatedOrderQuantity(Double updatedOrderQuantity) {
		this.updatedOrderQuantity = updatedOrderQuantity;
	}

	public Integer getEmsOrderItemId() {
		return emsOrderItemId;
	}

	public void setEmsOrderItemId(Integer emsOrderItemId) {
		this.emsOrderItemId = emsOrderItemId;
	}

	@Override
	public String toString() {
		return "UpdateOrderQuantityRequest [emsOrderItemId=" + emsOrderItemId + ", updatedOrderQuantity="
				+ updatedOrderQuantity + "]";
	}
}

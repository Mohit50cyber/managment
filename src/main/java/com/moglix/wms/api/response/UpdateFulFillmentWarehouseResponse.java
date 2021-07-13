package com.moglix.wms.api.response;

public class UpdateFulFillmentWarehouseResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8551140043733588175L;
	
	private int saleOrderId;
	
	public UpdateFulFillmentWarehouseResponse(String message, boolean status, int code, int saleOrderId) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
		this.saleOrderId = saleOrderId;
	}

	public int getSaleOrderId() {
		return saleOrderId;
	}

	public void setSaleOrderId(int saleOrderId) {
		this.saleOrderId = saleOrderId;
	}

}

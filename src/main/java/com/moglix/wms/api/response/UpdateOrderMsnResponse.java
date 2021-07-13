package com.moglix.wms.api.response;

public class UpdateOrderMsnResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5545961820339747149L;

	private Integer saleOrderId;
	
	public Integer getSaleOrderId() {
		return saleOrderId;
	}


	public void setSaleOrderId(Integer saleOrderId) {
		this.saleOrderId = saleOrderId;
	}


	public UpdateOrderMsnResponse(String message, boolean status, int code, Integer saleOrderId) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
		this.saleOrderId = saleOrderId; 
	}
}

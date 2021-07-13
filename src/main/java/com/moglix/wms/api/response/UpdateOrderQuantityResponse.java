package com.moglix.wms.api.response;

public class UpdateOrderQuantityResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 923667497208968400L;
	
	private Integer saleOrderId;
	
	public UpdateOrderQuantityResponse(String message, boolean status, int code, Integer saleOrderId) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
		this.saleOrderId = saleOrderId;
	}

	public Integer getSaleOrderId() {
		return saleOrderId;
	}

	public void setSaleOrderId(Integer saleOrderId) {
		this.saleOrderId = saleOrderId;
	}
}

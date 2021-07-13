package com.moglix.wms.api.response;

public class SaleOrderDeallocationResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6882907946352856274L;
	
	private Double quantity;

	public SaleOrderDeallocationResponse(Double quantity) {
		super();
		this.quantity = quantity;
	}

	public SaleOrderDeallocationResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SaleOrderDeallocationResponse(String message, boolean status, int code) {
		super(message, status, code);
		// TODO Auto-generated constructor stub
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	

}

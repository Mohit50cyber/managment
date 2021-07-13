package com.moglix.wms.api.response;

import com.moglix.wms.dto.ProductDTO;

public class GetProductByBarcodeResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1864993302586587604L;
	
	public GetProductByBarcodeResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setCode(code);
		this.setStatus(status);
	}
	private ProductDTO product;

	public ProductDTO getProduct() {
		return product;
	}

	public void setProduct(ProductDTO product) {
		this.product = product;
	}
	

}

package com.moglix.wms.api.response;

import com.moglix.wms.dto.ProductDTO;

public class GetProuductExpiryAndLotResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1698559317934554960L;
	
	
	public GetProuductExpiryAndLotResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	private ProductDTO productDetail;


	public ProductDTO getProductDetail() {
		return productDetail;
	}

	public void setProductDetail(ProductDTO productDetail) {
		this.productDetail = productDetail;
	}
}

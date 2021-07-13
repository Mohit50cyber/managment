package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ProductPacketResponseDTO;

public class GetProductByPacketResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4353642912201441940L;
	
	public GetProductByPacketResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
	
	private List<ProductPacketResponseDTO> products = new ArrayList<ProductPacketResponseDTO>();

	public List<ProductPacketResponseDTO> getProducts() {
		return products;
	}

	public void setProducts(List<ProductPacketResponseDTO> products) {
		this.products = products;
	}
}

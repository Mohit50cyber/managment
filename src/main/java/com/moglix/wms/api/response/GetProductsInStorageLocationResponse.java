package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.StorageContent;

public class GetProductsInStorageLocationResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6696131594612433507L;

	public GetProductsInStorageLocationResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
		
	}
	private List<StorageContent> products = new ArrayList<>();

	public List<StorageContent> getProducts() {
		return products;
	}
	public void setProducts(List<StorageContent> products) {
		this.products = products;
	}
}

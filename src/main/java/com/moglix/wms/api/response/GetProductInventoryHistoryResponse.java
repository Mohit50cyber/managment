package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ProductInventoryHistoryDTO;

public class GetProductInventoryHistoryResponse extends PaginationResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1412129690707375331L;

	public GetProductInventoryHistoryResponse(String message, boolean status, int code) {
		super(message, status, code);
	}
	
	List<ProductInventoryHistoryDTO> productInventoryhistory = new ArrayList<>();

	public List<ProductInventoryHistoryDTO> getProductInventoryhistory() {
		return productInventoryhistory;
	}

	public void setProductInventoryhistory(List<ProductInventoryHistoryDTO> productInventoryhistory) {
		this.productInventoryhistory = productInventoryhistory;
	}
}

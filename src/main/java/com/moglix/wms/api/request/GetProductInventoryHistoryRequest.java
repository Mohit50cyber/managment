package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class GetProductInventoryHistoryRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8914477404807840086L;

	@NotNull
	private String productMsn;
	
	@NotNull
	private Integer warehouseId;

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}
}

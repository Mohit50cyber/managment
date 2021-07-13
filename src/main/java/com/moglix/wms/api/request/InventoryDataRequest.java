package com.moglix.wms.api.request;

import com.moglix.wms.validator.CheckValidWarehouse;

public class InventoryDataRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 213095209511434597L;

	@CheckValidWarehouse
	private Integer warehouseId;

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Override
	public String toString() {
		return "InventoryDataRequest [warehouseId=" + warehouseId + "]";
	}
}

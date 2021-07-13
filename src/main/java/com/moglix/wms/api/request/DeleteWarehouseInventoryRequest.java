package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

import com.moglix.wms.validator.CheckValidWarehouse;

public class DeleteWarehouseInventoryRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5639637709748938945L;
	@NotNull
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
		return "DeleteWarehouseInventoryRequest [warehouseId=" + warehouseId + ", getWarehouseId()=" + getWarehouseId()
				+ "]";
	}
	
	
	
}




package com.moglix.wms.dto;

import com.moglix.wms.constants.StorageLocationType;

public interface ProductInventoryData {
	StorageLocationType getType();
	Double getAvailableQuantity();
	String getProductMsn();
	Integer getWarehouseId();
}

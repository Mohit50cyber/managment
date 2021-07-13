package com.moglix.wms.dto;

import java.util.Date;

public interface ExpiredInventoryDTO {
	Integer getQuantity();
	String getProductMsn();
	String getStorageLocation();
	Date getExpiryDate();
	String getWarehouse();
}

package com.moglix.wms.dto;

import java.util.Date;

public interface StorageContent {
	Integer getProductId();

	Integer getStorageLocationId();

	String getProductMsn();

	String getProductName();

	String getBinName();

	Integer getBinId();

	Integer getZoneId();

	Integer getRackId();

	Integer getWarehouseId();

	Double getQuantity();

	Date getExpiryDate();
}
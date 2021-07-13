package com.moglix.wms.dto;

import java.util.Date;

public interface BadInventory {
	Integer getId();

	Double getAvailableQuantity();

	Date getCreated();

	Date getModified();

	Integer getProductId();

	Boolean getConfirmed();

	Integer getWarehouseId();

	Integer getShelfLife();

	Date getExpiryDate();
}

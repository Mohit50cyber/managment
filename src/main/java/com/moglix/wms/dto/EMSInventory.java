package com.moglix.wms.dto;

import java.util.Date;

public interface EMSInventory {

	Integer getWarehouseId();

	Integer getMrnId();

	Integer getPoId();

	Date getMrnDate();

	String getProductMpn();

	String getProductName();

	Double getArrivedQuantity();

	String getBrandName();

	String getProductUnit();

	Integer getSupplierId();

	String getSupplierName();

	Double getTaxRate();

	Double getTransferPrice();

	Integer getPoItemId();
}

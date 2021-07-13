package com.moglix.wms.dto;

public interface FreshAvailableQuantityDetail {
	String getProductMsn();
	Double getQuantity();
	Integer getSupplierPoId();
	Integer getSupplierPoItemId();
	String getMrnId();
}

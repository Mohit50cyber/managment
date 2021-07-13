package com.moglix.wms.dto;

public interface LotInfo {
	String getInvoiceNumber();
	Double getQuantity();
	String getLotNumber();
	String getProductMsn();
	String getUom();
	String getDescription();
}

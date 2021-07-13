package com.moglix.wms.dto;

import java.io.Serializable;

public interface InventoryDataDTO extends Serializable{
	String getWarehouseName();	
	String getProductMsn();
	String getName();
	Double getQuantity();
	String getUom();
	String getZone();
	String getBin();
	String getStatus();
	String getInvoiceNumber();
}

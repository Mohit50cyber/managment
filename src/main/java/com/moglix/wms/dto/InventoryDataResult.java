package com.moglix.wms.dto;

import java.io.Serializable;
import java.util.Date;

import com.moglix.wms.constants.InboundType;

public interface InventoryDataResult extends Serializable{
	 String getWarehouseName();
	 
	 String getProductMsn();
	 
	 String getProductName();
	 
	 InboundType getInventoryType();
	 
	 String getZone();
	 
	 String getBin();
	 
	 Integer getSupplierPoId();
	 
	 Integer getSupplierPoItemId();
	 
	 Double getPurchasePrice();
	 
	 Double getAllocatedQuantity();
	 
	 Double getAvailableQuantity();
	 
	 Double getTotalQuantity();

	 Boolean getInventorize();
	 
	 Date getMrnDate();

	 String getInvoiceNumber();
}

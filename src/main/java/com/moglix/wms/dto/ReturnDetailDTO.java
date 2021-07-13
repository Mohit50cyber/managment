package com.moglix.wms.dto;

import java.io.Serializable;

public interface ReturnDetailDTO extends Serializable {
	
	Integer getInboundId();

	Integer getEmsReturnId();
	
	Double getReturnedQuantity();
	
	Double getQuantity();
	
	Double getDebitDoneQuantity();
	
	Integer getSupplierId();
	
	String getSupplierName();
	
	Integer getWarehouseId();
	
	String getWarehouseName();
	
	Double getPurchasePrice();
	
	Integer getSupplierPoId();
	
	Integer getSupplierPoItemId();
	
	Double getTax();
	
	String getProductMsn();
	
	String getProductName();
	
	String getUom();
}

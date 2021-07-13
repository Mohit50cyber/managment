package com.moglix.wms.dto;

public interface VmiReportDataDTO {
	String getWarehouseName();	
	String getProductMsn();
	Double getMinimumQuantity();
	Double getMaximumQuantity();
	Integer getPlantId();
	Integer getWarehouseId();
	Double getPurchasePrice();
	
}

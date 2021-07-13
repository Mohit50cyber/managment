package com.moglix.wms.dto;
public interface InboundStorageSalesAllocationDTO {
	
	Integer getOrderAllocationId();	
	Double getOrderAllocationQuantity();
	Double getOrderAvailableQuantity();
	Integer getOrderId();
}
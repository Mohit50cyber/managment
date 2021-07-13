package com.moglix.wms.dto;

import java.io.Serializable;

import com.moglix.wms.constants.BatchType;

public interface FreeInventoryData extends Serializable{
	public Integer getInboundId();
	
	public String getRefNumber();
	
	public Integer getSupplierPoId();
	
	public Integer getSupplierPoItemId();
	
	public Double getAvailableQuantity();
	
	public Double getAllocatedQuantity();
	
	public BatchType getBatchType();
}

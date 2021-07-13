package com.moglix.wms.dto;

import java.io.Serializable;

public interface ReturnInventoryData extends Serializable{

	public String getRefNumber();
	
	public Integer getSupplierPoId();
	
	public Integer getSupplierPoItemId();
	
	public Double getFreeQuantity();
	
	public Double getAllocatedQuantity();
}

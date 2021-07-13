package com.moglix.wms.dto;

public interface ProductInventoryDetailsDTO {
	Integer getProductId();
	String getProductMsn();
	Double getAvailableQuantity();
	Double getAllocatedQuantity();
	Double getCurrentQuantity();
	Double getDNInitiatedQuantity();
	Double getDNCreatedQuantity();
	Double getPackedQuantity();
	Integer getWarehouseId();
	String getWarehouseName();
	String getProductName();
	void setBlockedQuantity(Double blockedQuantity);
	void setAvailableQuantity(Double availableQuantity);
	void setCurrentQuantity(Double currentQuantity);
	void setDNInitiatedQuantity(Double dNInitiatedQuantity);
	void setDNCreatedQuantity(Double dNCreatedQuantity);
	void setAllocatedQuantity(Double allocatedQuantity);
	void setPackedQuantity(Double packedQuantity);
}

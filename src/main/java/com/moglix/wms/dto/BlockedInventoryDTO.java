package com.moglix.wms.dto;

import com.moglix.wms.constants.BlockedProductInventoryStatus;
import com.moglix.wms.constants.BulkInvoiceStatus;

public interface BlockedInventoryDTO {
	Double getBlockedQuantity();
	String getProductMsn();
	BlockedProductInventoryStatus getStatus();
	Integer getWarehouseId();
	String getBulkInvoiceId();
	String getBuyersOrderId();
	String getItemRef();
	Double getOrderedQuantity();
	BulkInvoiceStatus getOrderStatus();
}

package com.moglix.wms.dto;

import com.moglix.wms.constants.BlockedProductInventoryStatus;
import com.moglix.wms.constants.BulkInvoiceStatus;

public interface DnDetailItemDTO {

	Integer getEmsReturnId();
	Double getTotalQuantity();
	String getDebitNoteNumber();
	
}

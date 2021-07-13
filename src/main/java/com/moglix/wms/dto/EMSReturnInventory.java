package com.moglix.wms.dto;

import java.util.Date;

public interface EMSReturnInventory {
	Integer returnId();
	
	String getProuductName();
	
	Integer getPacketId();
	
	Double getQuantity();
	
	String getInvoiceNumber();
	
	String getCredtitNoteNo();
	
	String getProductMpn();
	
	Integer getWarehouseId();
	
	Date getCreatedAt();
}

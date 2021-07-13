package com.moglix.wms.dto;

import java.io.Serializable;

public interface InboundPoItemIdQuntityDTO extends Serializable{
	Integer getPoItemId();	
	String getType();
	Double getTotalQuantity();
	
}

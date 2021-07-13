package com.moglix.wms.service;

import com.moglix.wms.api.response.InventoryInboundStorageUpdationResponse;
import com.moglix.wms.api.response.InventoryInboundStorageValidationResponse;

public interface IInventoryInboundStorageValidationService {

	InventoryInboundStorageValidationResponse getInventoryInboundQuantityValidation();
	
	InventoryInboundStorageUpdationResponse updateInventoryInboundQuantity();
	
	

}

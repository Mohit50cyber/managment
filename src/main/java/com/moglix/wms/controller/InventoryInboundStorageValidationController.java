package com.moglix.wms.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.InventoryInboundStorageUpdationResponse;
import com.moglix.wms.api.response.InventoryInboundStorageValidationResponse;
import com.moglix.wms.service.IInventoryInboundStorageValidationService;
import com.moglix.wms.service.IInventorySaleOrderStorageValidationService;

@RestController
@RequestMapping("/api/inventoryInbound/")
public class InventoryInboundStorageValidationController {

	Logger log = LogManager.getLogger(InventoryInboundStorageValidationController.class);
	@Autowired
	@Qualifier("inventoryInboundValidationService")
	private IInventoryInboundStorageValidationService inventoryInboundValidationService;
	
	@Autowired
	private IInventorySaleOrderStorageValidationService inventorySaleOrderValidationService;
	
	@GetMapping("validation")
	public InventoryInboundStorageValidationResponse getInventoryInboundQuantityValidation() {
		return inventoryInboundValidationService.getInventoryInboundQuantityValidation();
	}
	
	@PostMapping("update")
	public InventoryInboundStorageUpdationResponse updateInventoryInboundQuantity() {
		return inventoryInboundValidationService.updateInventoryInboundQuantity();
	}
	
	@PostMapping("validateSaleOrder")
	public BaseResponse getInventorySaleOrderQuantityValidation() {
		return inventorySaleOrderValidationService.getInventorySaleOrderQuantityValidation();
	}
	
}

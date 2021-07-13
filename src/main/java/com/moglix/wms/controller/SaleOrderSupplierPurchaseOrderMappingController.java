package com.moglix.wms.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.moglix.wms.api.request.SaleOrderSupplierPurchaseOrderMappingRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.service.ISaleOrderSupplierPurchaseOrderMappingService;


/**
 * @author sparsh saxena on 9/3/21
 */

@RestController
@RequestMapping("/api/SaleOrderSupplierPurchaseOrderMapping/")
public class SaleOrderSupplierPurchaseOrderMappingController {
	
	private Logger logger = LogManager.getLogger(SaleOrderSupplierPurchaseOrderMappingController.class);
	
    @Autowired
    @Qualifier("saleOrderSupplierPurchaseOrderMappingService")
    private ISaleOrderSupplierPurchaseOrderMappingService saleOrderSupplierPurchaseOrderMappingService;
	
    @PostMapping("Save")
    public BaseResponse saveMapping(@Valid @RequestBody SaleOrderSupplierPurchaseOrderMappingRequest request) {
      
    	logger.info("Request received to save SaleOrderSupplierPurchaseOrderMapping :: " + new Gson().toJson(request.toString()));

        return saleOrderSupplierPurchaseOrderMappingService.saveMapping(request);
    }

}

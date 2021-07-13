package com.moglix.wms.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.moglix.wms.api.request.BlockInventoryRequest;
import com.moglix.wms.api.request.DeleteInventoryRequest;
import com.moglix.wms.api.request.DeleteWarehouseInventoryRequest;
import com.moglix.wms.api.request.GetDnDetailItemsRequest;
import com.moglix.wms.api.request.GetInventoryAvailabilityRequest;
import com.moglix.wms.api.request.GetInventoryStatsRequest;
import com.moglix.wms.api.request.SearchProductInventoryRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GetDnItemsResponse;
import com.moglix.wms.api.response.GetFreshAvailableQuantityResponse;
import com.moglix.wms.api.response.GetInventoryAvailabilityResponse;
import com.moglix.wms.api.response.GetInventoryStatsResponse;
import com.moglix.wms.api.response.SearchProductInventoryResponse;
import com.moglix.wms.service.IProductInventoryService;

/**
 * @author pankaj on 15/5/19
 */
@RestController
@RequestMapping("/api/inventory")
public class ProductInventoryController {
    Logger log = LogManager.getLogger(ProductInventoryController.class);

    @Autowired
    @Qualifier("productInventoryService")
    private IProductInventoryService productInventoryService;


    @PostMapping("search")
    public SearchProductInventoryResponse searchInventory(@Valid @RequestBody SearchProductInventoryRequest request, @RequestHeader Integer countryId, Pageable page) {
        log.info("Search product inventory api :: " + new Gson().toJson(request));
        return productInventoryService.searchInventory(request, countryId, page);
    }

    @PostMapping("getStats")
    public GetInventoryStatsResponse getInventoryStats(@RequestBody GetInventoryStatsRequest request) {
        log.info("get inventory stats :: " + new Gson().toJson(request));
        return productInventoryService.getInventoryStats(request);
    }
    
    @PostMapping("getAvailability")
    public GetInventoryAvailabilityResponse getInventoryAvailability(@Valid @RequestBody GetInventoryAvailabilityRequest request) {
    	log.info("Request received to get product inventory availability :: " + new Gson().toJson(request));
        return productInventoryService.getInventoryAvailability(request);
    }
    
    @PostMapping("getAvailability/realtime")
    public GetInventoryAvailabilityResponse getRealtimeInventoryAvailability(@Valid @RequestBody GetInventoryAvailabilityRequest request) {
        log.info("Request received to get product inventory availability :: " + new Gson().toJson(request));
        return productInventoryService.getRealtimeInventoryAvailability(request);
    }
    
    @PostMapping("/deleteInventory/")
    public BaseResponse deleteInventory(@Valid @RequestBody DeleteInventoryRequest request) {
        log.info("Received request to delete inventory :: " + new Gson().toJson(request));
        return productInventoryService.deleteInventory(request);
    }
    
    @PostMapping("/deleteInventoryByWarehouse/")
    public BaseResponse deleteInventoryByWarehouse(@Valid @RequestBody DeleteWarehouseInventoryRequest request) {
        log.info("Received request to delete inventory :: " + new Gson().toJson(request));
        return productInventoryService.deleteInventoryByWarehouse(request);
    }
    
    @PostMapping("/blockInventory/")
    public BaseResponse blockInventory(@Valid @RequestBody BlockInventoryRequest request) {
        log.info("BlockInventoryRequest Received :: " + new Gson().toJson(request));
        return productInventoryService.blockInventory(request);
    }

    @GetMapping("/uploadProductInventoryConfig/{filename}")
	public BaseResponse uploadProductInventoryConfig(@PathVariable("filename") String filename) throws IOException {
    	log.info("Received Request to upload product inventory config");
		return productInventoryService.uploadProductInventoryConfig(filename);
	}
    
    @GetMapping("/getFreshAvailableQuantity/")
	public GetFreshAvailableQuantityResponse getFreshAvailableQuantity() throws IOException {
    	log.info("Received Request to upload product inventory config");
		return productInventoryService.getFreshAvailableQuantity();
	}
    
    
    @PostMapping("/getDnInititatedItems")
    public GetDnItemsResponse getDnInititatedItems(@Valid @RequestBody GetDnDetailItemsRequest request) {
        log.info("Request Received to get DnInitiated Items :: " + new Gson().toJson(request));
        return productInventoryService.getDnInititatedItems(request);
    }
    
    @PostMapping("/getDnCreatedItems")
    public GetDnItemsResponse getDnCreatedItems(@Valid @RequestBody GetDnDetailItemsRequest request) {
        log.info("Request Received to get DnCreated Items :: " + new Gson().toJson(request));
        return productInventoryService.getDnCreatedItems(request);
    }
}

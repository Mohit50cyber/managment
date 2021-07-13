package com.moglix.wms.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.request.SearchStorageLocationRequest;
import com.moglix.wms.api.request.SearchWithMsnRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GetProductsInStorageLocationResponse;
import com.moglix.wms.api.response.SearchStorageLocationResponse;
import com.moglix.wms.service.IStorageLocationService;
import com.moglix.wms.task.StorageImportTask;

/**
 * @author pankaj on 1/5/19
 */
@RestController
@RequestMapping("/api/storageLocation/")
public class StorageLocationController {

    Logger log = LogManager.getLogger(StorageLocationController.class);

    @Autowired
    @Qualifier("storageLocationService")
    IStorageLocationService storageLocationService;

    @Autowired
    StorageImportTask importTask;
    
    @GetMapping("ping")
    public String ping() {
        return "Welcome to Storage Location Controller";
    }

    @PostMapping("search")
    public SearchStorageLocationResponse searchStorageLocation(@Valid @RequestBody SearchStorageLocationRequest request, @PageableDefault(size = 500) Pageable page) {
        log.info("search storage location api : " + request.toString());
        return storageLocationService.searchStorageLocation(request, page);
    }
    
    @PostMapping("/search/binTransfer")
    public SearchStorageLocationResponse searchStorageLocationForBinTransfer(@Valid @RequestBody SearchStorageLocationRequest request, @PageableDefault(size = 500) Pageable page) {
        log.info("search storage location api : " + request.toString());
        return storageLocationService.searchStorageLocationForBinTransfer(request, page);
    }
    
    @PostMapping("/getProducts/{storageLocationId}")
    public GetProductsInStorageLocationResponse getProductsInStorageLocation(@Valid @RequestBody (required=false) SearchWithMsnRequest request,@PathVariable("storageLocationId") int storageLocationId){
       
        log.info("Received request for storageLocation: "  + storageLocationId);
        return storageLocationService.getProductsInStorageLocation(storageLocationId, request);
    }
    
    
    @PostMapping("/getProductExpiry")
    public GetProductsInStorageLocationResponse getProductExpiry(@Valid @RequestBody SearchWithMsnRequest request){
        log.info("Received request to fetch expiry dates: "  + request);
        return storageLocationService.getProductsInExpiry(request.getStorageLocationId(), request);
    }
       
        @PostMapping("/getProducts")
    public GetProductsInStorageLocationResponse getProductsInStorageLocationWithMsn(@Valid @RequestBody SearchWithMsnRequest request){
       
		if (request.getStorageLocationId() != 0) {
			log.info("Received request for storageLocation: " + request.getStorageLocationId());
			return storageLocationService.getProductsInStorageLocation(request.getStorageLocationId(), request);
		} else {
			log.info("Received request for warehouseId: " + request.getWarehouseId() + " and productMsnList: "
					+ request.getProductMsnList());
			return storageLocationService.getProductsInStorageLocation(request);
		}
       
    }

    @GetMapping("/startStorageImport/{numberOfThreads}")
	public BaseResponse startStorageImport(@PathVariable("numberOfThreads") int numberOfThreads) throws IOException {
		importTask.process(numberOfThreads); 
		return new BaseResponse("Storage Import Task Started", true, 200);
	}
    
    @GetMapping("/startUpdateStorageImport/{numberOfThreads}")
   	public BaseResponse startUpdateStorageImport(@PathVariable("numberOfThreads") int numberOfThreads) throws IOException {
   		importTask.updateProcess(numberOfThreads); 
   		return new BaseResponse("Storage update Import Task Started", true, 200);
   	}
}

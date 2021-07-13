package com.moglix.wms.controller;

import java.util.List;

import javax.validation.Valid;

import com.google.gson.Gson;
import com.moglix.wms.api.request.CreateInboundStorageRequest;
import com.moglix.wms.api.request.GetInventoryLocationsForAvailableQtyRequest;
import com.moglix.wms.api.request.GetInventoryLocationsForTotalQtyRequest;
import com.moglix.wms.api.request.StockTransferRequest;
import com.moglix.wms.api.response.CreateInboundStorageResponse;
import com.moglix.wms.api.response.GetInventoryLocationsForAvailableQtyResponse;
import com.moglix.wms.api.response.GetInventoryLocationsForTotalQtyResponse;
import com.moglix.wms.api.response.StockTransferResponse;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.service.IInboundStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inboundStorages")
public class InboundStorageController {

	Logger log = LogManager.getLogger(InboundStorageController.class);
	
	@Autowired
	private IInboundStorageService inboundStorageService;

	@PostMapping("/")
	public CreateInboundStorageResponse create(@Valid @RequestBody CreateInboundStorageRequest request) {
		log.info("Request recieved for bin assignment :: " + new Gson().toJson(request.toString()));
		log.info("assign bin api: " + request.toString());
        CreateInboundStorageResponse response = new CreateInboundStorageResponse();
		try {
		    response=inboundStorageService.create(request);
        } catch (WMSException ex) {
                response.setStatus(false);
                response.setMessage(ex.getMessage());
        }
		return response;
	}

	@PostMapping("/getForTotalQty")
	public GetInventoryLocationsForTotalQtyResponse getForTotalQty(@Valid @RequestBody GetInventoryLocationsForTotalQtyRequest request) {
		log.info("get locations for total qty: " + request.toString());
		return inboundStorageService.getLocationsForTotalQty(request);
	}

	@PostMapping("/getForAvailableQty")
	public GetInventoryLocationsForAvailableQtyResponse getForAvailableQty(@Valid @RequestBody GetInventoryLocationsForAvailableQtyRequest request) {
		log.info("get locations for available qty: " + request.toString());
		return inboundStorageService.getLocationsForAvailableQty(request);
	}
	
	@PostMapping("/transferStock")
	public List<StockTransferResponse> transferStock(@Valid @RequestBody List<StockTransferRequest> request)  {

       return inboundStorageService.binTransfer(request);

	}

}

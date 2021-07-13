package com.moglix.wms.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.request.GetInboundByIdRequest;
import com.moglix.wms.api.request.GetInboundRequest;
import com.moglix.wms.api.request.InventoriseInboundRequest;
import com.moglix.wms.api.request.UpdateInboundTaxRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse;
import com.moglix.wms.api.response.GetInboundByIdResponse;
import com.moglix.wms.api.response.GetInboundByPoItemIdResponse;
import com.moglix.wms.api.response.GetInboundResponse;
import com.moglix.wms.api.response.InventoriseInboundResponse;
import com.moglix.wms.api.response.LotInfoResponse;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.impl.UpdateInboundRequest;

@RestController
@RequestMapping("/api/inbounds")
public class InboundController {

	Logger log = LogManager.getLogger(InboundController.class);
	
	@Autowired
	@Qualifier("inboundService")
	IInboundService inboundService;

	@Autowired
	IInboundStorageService inboundStorageService;
	
	@PostMapping(value = "/list")
	public GetInboundResponse get(@Valid @RequestBody GetInboundRequest request, Pageable page) {
		log.info("Received request to get Inbounds");
		
		if(request.getSearchKey() != null && request.getSearchKey().toLowerCase().startsWith("msn")) {
			return inboundService.getInboundByProducMsn(request, page);
		}else {
			return inboundService.searchInbounds(request,page);
		}
	}

	@PostMapping(value = "/addToInventory")
	public InventoriseInboundResponse get(@Valid @RequestBody InventoriseInboundRequest request, Pageable page) {
		log.info("Inventorise Inbound: " + request.toString());
		return inboundService.inventoriseInbound(request);
	}
	
	@GetMapping("/{id}")
    public GetInboundByIdResponse getById(@PathVariable("id") Integer id) {
        log.info("Received request to get Inbound by id api : " + id);
        GetInboundByIdRequest request = new GetInboundByIdRequest();
        request.setId(id);
        return inboundService.getInboundById(request);
    }
	
	@GetMapping("/lotinfo/{id}")
    public LotInfoResponse getLotInfoById(@PathVariable("id") Integer id) {
        log.info("Received request to get Inbound by id api : " + id);
        GetInboundByIdRequest request = new GetInboundByIdRequest();
        request.setId(id);
        return inboundService.getLotInfoById(request);
    }
	
	@GetMapping("/batch/{refNo}/poId/{supplierPoId}")
    public GetFreeInventoryResponse getById(@PathVariable("refNo") String refNo, @PathVariable("supplierPoId") Integer supplierPoId) {
        log.info("Received request to get Free inventory of Inbound by refNo : " + refNo + " and po_id: " + supplierPoId);
        return inboundService.getFreeInventory(refNo, supplierPoId);
    }
	
	@PostMapping("/update/")
	public BaseResponse updateInbound(@Valid @RequestBody UpdateInboundRequest request) {
		log.info("Received request to update inbound: " + request);
		return inboundService.updateInboundTransferPrice(request);
	}
	
	@PostMapping("/bulkUpdate/")
	public BaseResponse updateInboundTax(@Valid @RequestBody UpdateInboundTaxRequest request) {
		log.info("Received request to update  inbound tax: " + request);
		return inboundService.updateInboundTax(request);
	}
	
	@GetMapping("/poItemId/{poItemId}")
    public GetInboundByPoItemIdResponse getInBoundByPoItemId(@PathVariable("poItemId") Integer poItemId) {
        log.info("Received request to get Inbound by poItemId : " + poItemId);
        return inboundService.getInBoundByPoItemId(poItemId);
    }
	
	
	
}

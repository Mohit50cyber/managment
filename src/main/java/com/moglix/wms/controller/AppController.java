package com.moglix.wms.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.request.FindStorageLocationRequest;
import com.moglix.wms.api.request.GenerateBarcodeRequest;
import com.moglix.wms.api.request.GeneratePickupListByMSNRequest;
import com.moglix.wms.api.request.GeneratePickupListRequest;
import com.moglix.wms.api.request.GetInboundRequest;
import com.moglix.wms.api.request.MSNListRequest;
import com.moglix.wms.api.request.SearchPacketForPickupRequest;
import com.moglix.wms.api.response.AppVersionHistoryResponse;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.FetchStorageResponse;
import com.moglix.wms.api.response.GenerateBarcodeResponse;
import com.moglix.wms.api.response.GeneratePickupListAppResponse;
import com.moglix.wms.api.response.GeneratePickupListResponse;
import com.moglix.wms.api.response.GetInboundResponse;
import com.moglix.wms.api.response.MSNListResponse;
import com.moglix.wms.api.response.SearchPacketForPickupResponse;
import com.moglix.wms.api.response.CountWarehouseDataResponse;

import com.moglix.wms.entities.AppVersionHistory;
import com.moglix.wms.service.IBarcodeService;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IPacketService;
import com.moglix.wms.service.IPickupListService;
import com.moglix.wms.service.impl.StorageLocationServiceImpl;

@RestController
@RequestMapping("/api/app/")
public class AppController {
	Logger logger = LogManager.getLogger(AppController.class);

	@Autowired
	@Qualifier("packetServiceImpl")
	private IPacketService packetService;
	
	@Autowired
    private IPickupListService pickupListService;
	
	@Autowired
	private StorageLocationServiceImpl storageLocationService;
	
	@Autowired
	@Qualifier("inboundService")
	IInboundService inboundService;
	
	@PostMapping(value = "/MSNlist")
	public MSNListResponse create(@Valid @RequestBody MSNListRequest request) {
		logger.info("Request received tto get MSN list for packet id "  + request.getPacketId());
		return packetService.getMSNList(request);
	}
	
	@PostMapping("/pickupListMSN")
    public GeneratePickupListResponse pickupListMSN(@Valid @RequestBody GeneratePickupListByMSNRequest request) {
        logger.info("Request received to generate Pickuplist : " + request.toString());
        return pickupListService.generatePickListByMSN(request);
    }
	
	@PostMapping("/updateStatusPickupList/{packetId}")
    public BaseResponse updateStatus(@PathVariable("packetId") Integer packetId) {
        logger.info("Request received to update staus of Packet Id : " + packetId);
        return pickupListService.updateStatus(packetId);
    }
	
	@PostMapping("/countWarehouseData/{warehouseId}")
    public CountWarehouseDataResponse countWarehouseData(@PathVariable("warehouseId") Integer warehouseId) {
        logger.info("Request received to get data for warehouseId : " + warehouseId);
        return pickupListService.countWarehouseData(warehouseId);
    }
	
	@PostMapping("/fetchStorageLocation")
	public FetchStorageResponse fetchStorageLocation(@Valid @RequestBody FindStorageLocationRequest request) {
		logger.info("Request received to get dstoragelocation for zonebin" + request.toString());
		return storageLocationService.fetchStorageLocation(request);
	}
	
	@PostMapping("/fetchOneViewPickuplist")
    public GeneratePickupListAppResponse generate(@Valid @RequestBody GeneratePickupListRequest request) {
        logger.info("Request received to generate Pickuplist : " + request.toString());
        return pickupListService.generatePickListApp(request);
    }
	
//	@PostMapping("/progressPickupList/{packetId}")
//    public BaseResponse inprogresspacket(@PathVariable("packetId") Integer packetId, Authentication auth) {
//        logger.info("Request received to update staus of Packet Id : " + packetId);
//        return pickupListService.packetInProgress(packetId,auth.getName());
//    }
	
	@PostMapping("/packetItemPicking/{packetId}/packetitem/{packetItemId}")
    public BaseResponse pickPacketItem(@PathVariable("packetId") Integer packetId,@PathVariable("packetItemId") Integer packetItemId,Authentication auth) {
        logger.info("Request received to update staus of Packet Id : " + packetItemId);
        return pickupListService.updatePacketItem(packetId,packetItemId,auth.getName());
    }
	
	@PostMapping("/searchForPickup")
	public SearchPacketForPickupResponse searchPacketForPickup(@Valid @RequestBody SearchPacketForPickupRequest request, Pageable page,Authentication auth) {
		logger.info("search packet for pickup : " + request.toString());
		return packetService.appSearchPacketForPickup(request, page,auth.getName());
	}
	
	@PostMapping(value = "/inbounds/list")
	public GetInboundResponse get(@Valid @RequestBody GetInboundRequest request, Pageable page) {
		logger.info("Received request to get Inbounds");
			return inboundService.searchAppInbounds(request,page);
		}
	
}

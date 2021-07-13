package com.moglix.wms.controller;

import com.moglix.wms.api.request.*;
import com.moglix.wms.api.response.*;
import com.moglix.wms.service.IPacketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/packets")
public class PacketController {
	
	private Logger log = LogManager.getLogger(PacketController.class);
	
	@Autowired
	@Qualifier("packetServiceImpl")
	private IPacketService packetService;
	
	@PostMapping("/")
	public CreatePacketResponse create(@Valid @RequestBody CreatePacketRequest request) {
		log.info("Request received to create Packet: " + request.toString());
		return packetService.createPacket(request);
	}

	@GetMapping("{packetId}")
	public GetPacketByIdResponse get(@PathVariable("packetId") Integer packetId) {
		GetPacketByIdRequest request = new GetPacketByIdRequest(packetId);
		log.info("Request received to get packet details: " + request.toString());
		return packetService.getPacketById(request);
	}

	@PostMapping("/delete")
	public DeletePacketResponse delete(@Valid @RequestBody DeletePacketRequest request) {
		log.info("Request received to delete packet: " + request.toString());
		return packetService.deletePacket(request);
	}

	@PostMapping("/searchForPickup")
	public SearchPacketForPickupResponse searchPacketForPickup(@Valid @RequestBody SearchPacketForPickupRequest request, Pageable page) {
		log.info("search packet for pickup : " + request.toString());
		return packetService.searchPacketForPickup(request, page);
	}
	
	@PostMapping("/getLotInfo")
	public PacketLotResponse getLotNumbers(@Valid @RequestBody PacketLotInfoRequest request, Pageable page) {
		log.info("Request req : " + request.toString());
		return packetService.getLotInfo(request, page);
	}
	
	@GetMapping("deductStorages/{emsPacketId}")
	public DeductInboundStorageResponse deductInboundStorages(@PathVariable("emsPacketId") Integer emsPacketId) {
		log.info("Request received to deduct Inbound Storage for emsPacketId: " + emsPacketId);
		return packetService.deductInboundStorages(emsPacketId);
	}
	
	@GetMapping("getTransferPrices/{emsPacketId}")
	public GetTPByEmsPacketIdResponse getTransferPrices(@PathVariable("emsPacketId") Integer emsPacketId) {
		log.info("Request received to get transfer prices for emsPacketId: " + emsPacketId);
		return packetService.getTransferPriceByPacketId(emsPacketId);
	}
	
	@GetMapping("markShipped/{emsPacketId}")
	public BaseResponse markShipped(@PathVariable("emsPacketId") Integer emsPacketId) {
		log.info("Request received to mark shipped for emsPacketId: " + emsPacketId);
		return packetService.markShipped(emsPacketId);
	}
	
	@GetMapping("markScanned/{emsPacketId}")
	public BaseResponse markScanned(@PathVariable("emsPacketId") Integer emsPacketId) {
		log.info("Request received to mark shipped for emsPacketId: " + emsPacketId);
		return packetService.markScanned(emsPacketId);
	}
	
	@GetMapping("findUnshippedOrders/")
	public Set<String> findUnshippedOrders(){
		return packetService.findUnshippedOrders();
	}
}

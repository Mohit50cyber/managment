package com.moglix.wms.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

import com.google.gson.Gson;
import com.moglix.wms.api.request.CheckDebitnoteCreationValidityRequest;
import com.moglix.wms.api.request.CreateSupplierCreditNoteByMRNRequest;
import com.moglix.wms.api.request.CreateSupplierCreditNoteByReturnRequest;
import com.moglix.wms.api.request.CreateSupplierDebitNoteByFreeQuantityRequest;
import com.moglix.wms.api.request.DNUpdateStatusRequest;
import com.moglix.wms.api.request.DeductInventorisableRequest;
import com.moglix.wms.api.request.GetReturnFreeInventoryRequest;
import com.moglix.wms.api.request.GetReturnPacketsRequest;
import com.moglix.wms.api.request.GetReturnPickupListByIdRequest;
import com.moglix.wms.api.request.ReturnBatchRequest;
import com.moglix.wms.api.request.ReturnDetailsRequest;
import com.moglix.wms.api.request.SearchReturnPickupListRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreateSupplierCreditNoteByMRNResponse;
import com.moglix.wms.api.response.CreateSupplierCreditNoteByReturnResponse;
import com.moglix.wms.api.response.CreateSupplierDebitNoteByFreeQuantityResponse;
import com.moglix.wms.api.response.DNStatusUpdateResponse;
import com.moglix.wms.api.response.DeleteBatchResponse;
import com.moglix.wms.api.response.GetAvailableInventoryResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse;
import com.moglix.wms.api.response.GetReturnDetailsResponse;
import com.moglix.wms.api.response.GetReturnPickupListByIdResponse;
import com.moglix.wms.api.response.GetSuppliersByPacketResponse;
import com.moglix.wms.api.response.GetSuppliersByReturnPacketResponse;
import com.moglix.wms.api.response.ReturnBatchResponse;
import com.moglix.wms.api.response.ReturnDetailsResponse;
import com.moglix.wms.api.response.ReturnInvoiceLotResponse;
import com.moglix.wms.api.response.ReturnPacketLotInfoRequest;
import com.moglix.wms.api.response.SearchReturnPacketsResponse;
import com.moglix.wms.api.response.SearchReturnPickupListResponse;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.ICustomerReturnService;
import com.moglix.wms.service.ISupplierReturnService;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {
	
	Logger log = LogManager.getLogger(ReturnController.class);
	
	@Autowired
	@Qualifier("batchService")
	private IBatchService biService;

	@Autowired
	@Qualifier("debitNoteService")
	private ISupplierReturnService supplierReturnService;
	
	@Autowired
	@Qualifier("customerReturnServiceImpl")
	private ICustomerReturnService customerReturnService;
	
	@PostMapping("/")
	public ReturnBatchResponse returnBatch(@Valid @RequestBody ReturnBatchRequest request) {
		log.info("Request received to inbound Batch with packet Id :: " + request.getEmsPacketId());
		return customerReturnService.returnBatch(request);
	}
	
	@PostMapping("/getReturnDetails")
	public ReturnDetailsResponse getReturnDetails(@Valid @RequestBody ReturnDetailsRequest request) {
		log.info("Request received to get return details :: " +  new Gson().toJson(request));
		return customerReturnService.getReturnDetails(request);
	}
	
	@PostMapping("/supplier")
	public CreateSupplierCreditNoteByMRNResponse createCreditNoteByMRN(@Valid @RequestBody CreateSupplierCreditNoteByMRNRequest request) {
		log.info("Request received to Create Debit Note by MRN :: " + new Gson().toJson(request));
		return supplierReturnService.createSupplierCreditNoteByMRN(request);
	}
	
	@PostMapping("/supplier/freeQuantity")
	public CreateSupplierDebitNoteByFreeQuantityResponse createDebitNoteByFreeQuantity(@Valid @RequestBody CreateSupplierDebitNoteByFreeQuantityRequest request) {
		log.info("Request received to Create Debit Note by free quantity :: " +  new Gson().toJson(request));
		return supplierReturnService.createSupplierCreditNoteByFreeQuantity(request);
	}
	
	@PostMapping("/supplier/return")
	public CreateSupplierCreditNoteByReturnResponse createCreditNoteByReturn(@Valid @RequestBody CreateSupplierCreditNoteByReturnRequest request) {
		log.info("Request received Create Debit Note by return :: " + new Gson().toJson(request));
		return supplierReturnService.createSupplierCreditNoteByReturn(request);
	}
	
	@PostMapping("/supplier/dnStatusUpdate")
	public DNStatusUpdateResponse dnStatusUpdate(@Valid @RequestBody DNUpdateStatusRequest request) {
		log.info("Request received status or return request :: " + new Gson().toJson(request));
		return supplierReturnService.dnStatusUpdate(request);
	}

	@PostMapping("/checkDebitNoteValidity")
	public BaseResponse createCreditNoteByReturn(@Valid @RequestBody CheckDebitnoteCreationValidityRequest request) {
		log.info("Request received to check validity of debit note creation :: " + new Gson().toJson(request));
		return supplierReturnService.checkDebiNoteEligiblity(request.getEmsReturnId(), request.getSupplierId());
	}
	
	@PostMapping("/pickup/search")
	public SearchReturnPickupListResponse searchReturnPickupList(@Valid @RequestBody SearchReturnPickupListRequest request, Pageable page) {
		log.info("Request received to search return pickup list :: " + new Gson().toJson(request));
		return supplierReturnService.searchReturnPickup(request, page);
	}

	@PostMapping("/pickup/getById")
	public GetReturnPickupListByIdResponse getReturnPickupList(@Valid @RequestBody GetReturnPickupListByIdRequest request) {
		log.info("Request received to get return pickup list :: " + new Gson().toJson(request));
		return supplierReturnService.getReturnPickupById(request);
	}
	
	@PostMapping("/packets/search")
	public SearchReturnPacketsResponse returnPacket(@Valid @RequestBody @NotNull GetReturnPacketsRequest request, Pageable page){
		return supplierReturnService.getPacketReturns(request.getWarehouseId(), request.getSearchKey(), request.getStatus(), page);
	}
	
	@GetMapping("/packets/suppliers/{emsPacketId}")
	public GetSuppliersByPacketResponse returnProductDetailsByPacket(@PathVariable("emsPacketId") Integer emsPacketId){
		return supplierReturnService.getSuppliersByPacket(emsPacketId);
	}
	
	@GetMapping("/batch/details/{invoiceNumber}")
	public GetReturnDetailsResponse getReturnBatchDetailsByInvoiceNumber(@PathVariable("invoiceNumber") String invoiceNumber){
		return customerReturnService.getReturnBatchDetailsByInvoiceNumber(invoiceNumber);
	}
	
	@GetMapping("/returnpackets/suppliers/{emsReturnId}")
	public GetSuppliersByReturnPacketResponse returnProductDetailsByReturnPacket(@PathVariable("emsReturnId") Integer emsReturnId){
		return supplierReturnService.getSuppliersByReturnPacket(emsReturnId);
	}
	
	@GetMapping("/checkIfCancellable/{emsReturnId}")
	public DeleteBatchResponse checkIfBatchIsDeletable(@PathVariable("emsReturnId") Integer emsReturnId) {
		return supplierReturnService.checkIfBatchIsDeletable(emsReturnId);
	}
	
	@GetMapping("/cancel/{emsReturnId}")
	public DeleteBatchResponse cancelReturn(@PathVariable("emsReturnId") Integer emsReturnId) {
		return supplierReturnService.cancelReturn(emsReturnId);
	}
	
	@GetMapping("/cancelReturnPickupList/{emsreturnNoteId}")
	public BaseResponse cancelReturnPickupList(@PathVariable("emsreturnNoteId") Integer emsreturnNoteId) {
		return supplierReturnService.cancelReturnPickupList(emsreturnNoteId);
	}
	
	@PostMapping("/deductInventorisable")
	public BaseResponse deductInventorisable(@Valid @RequestBody DeductInventorisableRequest request) {
		log.info("Received Request to deduct Inventory for rate difference :: " + new Gson().toJson(request));
		return supplierReturnService.deductInventorisable(request);
	}
	
	@PostMapping("/getReturnFreeInventory")
	public GetFreeInventoryResponse getReturnFreeInventory(GetReturnFreeInventoryRequest request) {
		log.info("Received Request to return free Inventory for request :: " + new Gson().toJson(request));
		return customerReturnService.getReturnFreeInventory(request);
	}
	
	@PostMapping("/getLotInfo")
	public ReturnInvoiceLotResponse getLotNumbers(@Valid @RequestBody ReturnPacketLotInfoRequest request, Pageable page) {
		log.info("Request req :: " + new Gson().toJson(request));
		return customerReturnService.getLotInfo(request, page);
	}
	
	@GetMapping("/getFreeInventory/{supplierPoId}")
	public GetAvailableInventoryResponse getFreeInventory(@PathVariable("supplierPoId") Integer supplierPoId) {
		log.info("Received Request for free Inventory for po id :: [" + supplierPoId + "]");
		return customerReturnService.getAvailableInventory(supplierPoId);
	}
}

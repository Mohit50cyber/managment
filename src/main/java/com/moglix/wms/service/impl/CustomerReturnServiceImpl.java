package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.api.request.GetReturnFreeInventoryRequest;
import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.api.request.ReturnBatchRequest;
import com.moglix.wms.api.request.ReturnBatchRequest.PacketQuantityMapping;
import com.moglix.wms.api.request.ReturnBatchRequest.ReturnPacketCustom;
import com.moglix.wms.api.request.ReturnBatchRequest.ReturnPacketCustom.ReturnPacketItemCustom;
import com.moglix.wms.api.request.ReturnDetailsRequest;
import com.moglix.wms.api.request.SupplierDetailRequestIMS;
import com.moglix.wms.api.response.CreateBatchResponse;
import com.moglix.wms.api.response.GetAvailableInventoryResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse.Inventory;
import com.moglix.wms.api.response.GetReturnDetailsResponse;
import com.moglix.wms.api.response.PacketLotResponse;
import com.moglix.wms.api.response.ReturnBatchResponse;
import com.moglix.wms.api.response.ReturnDetailsResponse;
import com.moglix.wms.api.response.ReturnInvoiceLotResponse;
import com.moglix.wms.api.response.ReturnLotInfoDetailsMSNwise;
import com.moglix.wms.api.response.ReturnLotInfoDetailsMSNwise.ReturnLotInfo;
import com.moglix.wms.api.response.ReturnPacketLotInfoRequest;
import com.moglix.wms.api.response.SupplierDetailsForCustomerInvoiceResponse;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.constants.ProductType;
import com.moglix.wms.dto.LotInfo;
import com.moglix.wms.dto.FreeInventoryData;
import com.moglix.wms.dto.ProductPacketResponseDTOIMS;
import com.moglix.wms.dto.ProductPacketResponseDTOIMS.SupplierInfo;
import com.moglix.wms.dto.ReturnDetail;
import com.moglix.wms.dto.ReturnInventoryData;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.Packet;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.mapper.InboundMapper;
import com.moglix.wms.repository.BatchRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.PacketRespository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.SaleOrderAllocationRepository;
import com.moglix.wms.repository.SaleOrderRepository;
import com.moglix.wms.repository.StorageLocationRepository;
import com.moglix.wms.repository.WarehouseRepository;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.ICustomerReturnService;
import com.moglix.wms.service.IPacketService;

@Service(value = "customerReturnServiceImpl")
public class CustomerReturnServiceImpl implements ICustomerReturnService {

	@Autowired
	@Qualifier("packetServiceImpl")
	IPacketService packetService;
	
	@Autowired
	@Qualifier("batchService")
	private IBatchService biService;
	
	@Autowired
	ProductsRepository productRepo;
	
	@Autowired
	WarehouseRepository warehouseRepo;
	
	@Autowired
	SaleOrderRepository saleOrderRepo;
	
	@Autowired
	BatchRepository batchRepo;
	
	@Autowired
	InboundRepository inboundRepo;
	
	@Autowired
	InboundStorageRepository inboundStorageRepo;
	
	@Autowired
	SaleOrderAllocationRepository saleOrderAllocationRepo;
	
	@Autowired
	StorageLocationRepository storageLocationRepo;
	
	@Autowired
	private PacketRespository packetRepository;

	private Gson gson = new Gson();
	
	Logger log = LogManager.getLogger(CustomerReturnServiceImpl.class);
	@Override
	@Transactional
	public ReturnBatchResponse returnBatch(ReturnBatchRequest request) {

		log.info("Initiating return process of EMS Packet Id: " + request.getEmsPacketId());
		Packet packet = packetService.findByEmsPacketId(request.getEmsPacketId()).orElse(null);

		if (packet == null) {
			RestTemplate restTemplate = new RestTemplate();

			log.info("hiiting IMS API for supplierInfo ");

			List<PacketQuantityMapping> mappings = request.getPacketQuantityMapping();

			Set<ReturnPacketCustom> returnPackets = new HashSet<>();
			
			ReturnPacketCustom returnPacketCustom = new ReturnPacketCustom();
			
			returnPacketCustom.setTotalQuantity(mappings.stream().mapToDouble(PacketQuantityMapping :: getQuantity).sum());
			
			Set<ReturnPacketItemCustom> returnItems = new HashSet<>();
			
			for(PacketQuantityMapping mapping: mappings) {
				ReturnPacketItemCustom returnPacketItemCustom = new ReturnPacketItemCustom();
				returnPacketItemCustom.setProductMsn(mapping.getProductMsn());
				returnPacketItemCustom.setQuantity(mapping.getQuantity());
				returnItems.add(returnPacketItemCustom);
			}
			
			returnPacketCustom.setReturnPacketItems(returnItems);
			
			returnPackets.add(returnPacketCustom);
			
			SupplierDetailRequestIMS supplierDetailRequestIMS = new SupplierDetailRequestIMS();
			supplierDetailRequestIMS.setEmsPacketId(request.getEmsPacketId());
			supplierDetailRequestIMS.setEmsReturnId(request.getEmsReturnId());
			supplierDetailRequestIMS.setReturnPackets(returnPackets);
			
			HttpHeaders headers = new HttpHeaders();

			headers.add("Authorization",
					Constants.IMS_AUTH_TOKEN);
			
			log.info("request for IMS "+gson.toJson(supplierDetailRequestIMS));
			
			ResponseEntity<SupplierDetailsForCustomerInvoiceResponse> supplierDetailResponseIMS = restTemplate.postForEntity(Constants.IMS_SUPPLIER_INFO_API,
					new HttpEntity<SupplierDetailRequestIMS>(supplierDetailRequestIMS, headers), SupplierDetailsForCustomerInvoiceResponse.class);
			
			log.info("response from IMS :" + gson.toJson(supplierDetailResponseIMS));

			List<ProductPacketResponseDTOIMS> productPacketResponseDTOList = supplierDetailResponseIMS.getBody().getProductPacketResponses();
		
			Optional<Warehouse> warehouse = warehouseRepo.findById(request.getWarehouseId());
			Packet packetNull = new Packet();
			packetNull.setEmsPacketId(request.getEmsPacketId());
			packetNull.setWarehouse(warehouse.get());
			packetNull.setInvoiceNumber(request.getEmsInvoiceNumber());
			
			
			packetService.returnPacket(packetNull, request.getPacketQuantityMapping(), request.getCustomerName(), request.getEmsReturnId());
			CreateBatchRequest batchRequest = createBatchRequestBySupplierDeatils(productPacketResponseDTOList ,request);
			
			CreateBatchResponse createBatchResponse = biService.createBatch(batchRequest);
			return new ReturnBatchResponse("Batch Successfully created for EMS Packet ID: " + request.getEmsPacketId() +" Batch id : "+ createBatchResponse.getBatchId(),
					true, HttpStatus.OK.value());
		}else if(packet.getStatus().equals(PacketStatus.RETURNED)) {
			log.warn("No packet found for EMS Packet Id: " + request.getEmsPacketId() +" other than RETURNED status");
			return new ReturnBatchResponse("Cannot Delete Packet because no packet found with EMS Packet ID: " + request.getEmsPacketId()+" other than RETURNED status",
					false, HttpStatus.OK.value());
		}
		packetService.returnPacket(packet, request.getPacketQuantityMapping(), request.getCustomerName(), request.getEmsReturnId());
		log.info("Packet Successfully Returned for EMS Packet Id: " + request.getEmsPacketId());
		
		return new ReturnBatchResponse("Packet Successfully returned for EMS Packet ID: " + request.getEmsPacketId(),
				true, HttpStatus.OK.value());
	}
	private CreateBatchRequest createBatchRequestBySupplierDeatils(List<ProductPacketResponseDTOIMS> productPacketResponseDTOList , ReturnBatchRequest request) {
		CreateBatchRequest batchRequest = new CreateBatchRequest();
		batchRequest.setRefNo(request.getEmsReturnId()+"_"+productPacketResponseDTOList.get(0).getSupplierDetails().iterator().next().getSupplierId());
		batchRequest.setEmsPacketId(request.getEmsPacketId());
		batchRequest.setEmsReturnId(request.getEmsReturnId());
		batchRequest.setBatchType(BatchType.CUSTOMER_RETURN);
		batchRequest.setWarehouseId(request.getWarehouseId());
		batchRequest.setWarehouseName(request.getWarehouseName());
	//	batchRequest.setPurchaseDate(null);  doubt
    	batchRequest.setInboundedBy(request.getInboundedBy());  
		batchRequest.setSupplierId(productPacketResponseDTOList.get(0).getSupplierDetails().iterator().next().getSupplierId());
		batchRequest.setSupplierName(productPacketResponseDTOList.get(0).getSupplierDetails().iterator().next().getSupplierName());
		
		List<ProductInput> productInputs = new ArrayList<>();
		
		for(ProductPacketResponseDTOIMS productPacketResponseDTO : productPacketResponseDTOList) {
			for(SupplierInfo supplierInfo : productPacketResponseDTO.getSupplierDetails()  ) {
				ProductInput productInput = new ProductInput();
				productInput.setSupplierPoId(supplierInfo.getSupplierPoId());
				productInput.setSupplierPoItemId(supplierInfo.getSupplierPoItemId());
				productInput.setType(ProductType.DURABLE);
				productInput.setInboundType(InboundType.CUSTOMER_RETURN);
				productInput.setProductMsn(productPacketResponseDTO.getProductMsn());
				productInput.setProductName(productPacketResponseDTO.getProductName());
				productInput.setDangerType(productPacketResponseDTO.getDangerType());
				productInput.setStorageType(productPacketResponseDTO.getStorageType());
				productInput.setQuantity(supplierInfo.getQuantity());
				productInput.setPurchasePrice(supplierInfo.getPurchasePrice());
				productInput.setTax(supplierInfo.getTax());
				productInput.setUom(productPacketResponseDTO.getUom());
				productInput.setMfgDate(productPacketResponseDTO.getMfgDate());
				productInput.setExpDate(productPacketResponseDTO.getExpDate());
				productInput.setReturnedQuantity(productPacketResponseDTO.getReturnedQuantity());
				productInput.setIsSerializedProduct(productPacketResponseDTO.getIsSerializedProduct());
				productInput.setInventorize(false);
				productInput.setInventrisableQuantity(supplierInfo.getQuantity());	
				productInputs.add(productInput);

			}
		}

		batchRequest.setProducts(productInputs);
		return batchRequest;
	}
	
	@Override
	@Transactional
	public GetReturnDetailsResponse getReturnBatchDetailsByInvoiceNumber(String invoiceNumber) {

		GetReturnDetailsResponse response = new GetReturnDetailsResponse(
				"Return Details for invoice number: " + invoiceNumber, true, HttpStatus.OK.value());

		List<ReturnDetail> returnDetails = packetService.getReturnPacketDetails(invoiceNumber);
		
		if(CollectionUtils.isEmpty(returnDetails)) {
			response.setMessage("No Return Details Found");
			response.setStatus(false);
		}

		response.setReturnDetails(returnDetails);

		return response;
	}
	@Override
	@Transactional
	public GetFreeInventoryResponse getReturnFreeInventory(GetReturnFreeInventoryRequest request) {
		
		String refNumber = request.getEmsReturnId() + "_" + request.getSupplierId();
		
		GetFreeInventoryResponse response = new GetFreeInventoryResponse("Found Free Inventory", true, HttpStatus.OK.value());
		
		List<ReturnInventoryData> returnData = batchRepo.getReturnInventoryData(refNumber);
		
		if(CollectionUtils.isEmpty(returnData)) {
			return new GetFreeInventoryResponse("No free Inventory found", false, HttpStatus.OK.value());
		}
		
		for(ReturnInventoryData data: returnData) {
			Inventory inventory = new Inventory();
			inventory.setRefNo(data.getRefNumber());
			inventory.setSupplierPoId(data.getSupplierPoId());
			inventory.setSupplierPoItemId(data.getSupplierPoItemId());
			inventory.setFreeQuantity(data.getFreeQuantity());
			inventory.setAllocatedQuantity(data.getAllocatedQuantity());
			response.getInventory().add(inventory);
		}
		
		return response;
	}
	@Override
	@Transactional
	public ReturnDetailsResponse getReturnDetails(@Valid ReturnDetailsRequest request) {
		
		Batch returnBatch = batchRepo.findByRefNoAndBatchType(request.getEmsReturnId()+ "_" + request.getSupplierId(), BatchType.CUSTOMER_RETURN).orElse(null);
		
		if(returnBatch == null) {
			return new ReturnDetailsResponse("Did not find any return details for return id: " + request.getEmsReturnId() + " and supplier id: " + request.getSupplierId(), false, HttpStatus.OK.value());
		}else {
			List<Inbound> returnInbounds = returnBatch.getInbounds().stream().filter(e -> e.getInventorisableQuantity() > 0).collect(Collectors.toList());

			ReturnDetailsResponse response = new ReturnDetailsResponse("Found " + returnInbounds.size() + " details for " + request.getEmsReturnId() + " and supplier id: " + request.getSupplierId(), true, HttpStatus.OK.value());

			for (Inbound returnInbound : returnInbounds) {
				response.getReturnDetails().add(InboundMapper.createReturnDetailFromInbound(returnInbound));
			}
			return response;
		}
	}
	@Override
	public ReturnInvoiceLotResponse getLotInfo(@Valid ReturnPacketLotInfoRequest request, Pageable page) {
		List<String> invoiceNumbers = new ArrayList<>();
		invoiceNumbers.add(request.getInvoiceNumber());
		List<LotInfo> lotInformation = packetRepository.findLotInformatonByInvoiceNumbers(invoiceNumbers);
		
		if(lotInformation.isEmpty()) {
			ReturnInvoiceLotResponse response = new ReturnInvoiceLotResponse(
					"Not found any lot infomation against the invoice numbers", false, HttpStatus.OK.value());
			
			return response;
		}else {
			Map<String, List<ReturnLotInfo>> lotInfoMap = new HashMap<>();
			
			for(LotInfo lotInfo : lotInformation) {
				
				if(!lotInfoMap.containsKey(lotInfo.getProductMsn())) {
					lotInfoMap.put(lotInfo.getProductMsn(), new ArrayList<ReturnLotInfoDetailsMSNwise.ReturnLotInfo>());
				}
				ReturnLotInfo returnLotInfo = new ReturnLotInfo();
				returnLotInfo.setLotNumber(lotInfo.getLotNumber());
				returnLotInfo.setQuantity(lotInfo.getQuantity());
				lotInfoMap.get(lotInfo.getProductMsn()).add(returnLotInfo);
				
			}
			
			List<ReturnLotInfoDetailsMSNwise> lotInfoMsnwiseDetails = new ArrayList<>();
			
			for (Map.Entry<String,List<ReturnLotInfo>> entry : lotInfoMap.entrySet()){
				
				ReturnLotInfoDetailsMSNwise returnLotInfoDetailsMSNwise = new ReturnLotInfoDetailsMSNwise();
				returnLotInfoDetailsMSNwise.setProductMsn(entry.getKey());
				returnLotInfoDetailsMSNwise.setLotDetails(entry.getValue());
				
				lotInfoMsnwiseDetails.add(returnLotInfoDetailsMSNwise);
			}
	              
			
			ReturnInvoiceLotResponse response = new ReturnInvoiceLotResponse(
					"Found " + lotInfoMap.size() + "MSN records for invoice number", true, HttpStatus.OK.value());
			
			response.setMsnwiseLotDetails(lotInfoMsnwiseDetails);
			
			return response;
		}
	}
	@Override
	@Transactional
	public GetAvailableInventoryResponse getAvailableInventory(Integer supplierPoId) {
		List<FreeInventoryData> freeInventory = batchRepo.getFreeInventoryData(supplierPoId);
		
		if(CollectionUtils.isEmpty(freeInventory)) {
			return new GetAvailableInventoryResponse("No Inventory Found", false, HttpStatus.OK.value());
		}else {
			GetAvailableInventoryResponse response = new GetAvailableInventoryResponse("Found " + freeInventory.size() + " values for po id: " + supplierPoId, true, HttpStatus.OK.value());
			
			response.setInventory(freeInventory);
			
			return response;
		}
	}
}

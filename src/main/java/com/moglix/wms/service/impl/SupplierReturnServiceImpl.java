package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.moglix.wms.api.request.CreateSupplierCreditNoteByMRNRequest;
import com.moglix.wms.api.request.CreateSupplierCreditNoteByMRNRequest.CreditNoteItems;
import com.moglix.wms.api.request.CreateSupplierCreditNoteByReturnRequest;
import com.moglix.wms.api.request.CreateSupplierCreditNoteByReturnRequest.ReturnPickupListItemMapping;
import com.moglix.wms.api.request.CreateSupplierDebitNoteByFreeQuantityRequest;
import com.moglix.wms.api.request.DNUpdateStatusRequest;
import com.moglix.wms.api.request.DeductInventorisableRequest;
import com.moglix.wms.api.request.GetReturnPickupListByIdRequest;
import com.moglix.wms.api.request.ProductQuantity;
import com.moglix.wms.api.request.SearchReturnPickupListRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreateSupplierCreditNoteByMRNResponse;
import com.moglix.wms.api.response.CreateSupplierCreditNoteByReturnResponse;
import com.moglix.wms.api.response.CreateSupplierDebitNoteByFreeQuantityResponse;
import com.moglix.wms.api.response.DNStatusUpdateResponse;
import com.moglix.wms.api.response.DeleteBatchResponse;
import com.moglix.wms.api.response.GetReturnPickupListByIdResponse;
import com.moglix.wms.api.response.GetSuppliersByPacketResponse;
import com.moglix.wms.api.response.GetSuppliersByReturnPacketResponse;
import com.moglix.wms.api.response.GetSuppliersByReturnPacketResponse.SupplierInfo;
import com.moglix.wms.api.response.SearchReturnPacketsResponse;
import com.moglix.wms.api.response.SearchReturnPickupListResponse;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InventoriseAction;
import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.constants.ReturnPickupListStatus;
import com.moglix.wms.dto.ReturnPacketDTO;
import com.moglix.wms.dto.ReturnPickupListDto;
import com.moglix.wms.dto.ReturnPickupListItemDto;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Packet;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ReturnPacket;
import com.moglix.wms.entities.ReturnPickupList;
import com.moglix.wms.entities.ReturnPickupListItem;
import com.moglix.wms.exception.CancelReturnPickupListException;
import com.moglix.wms.repository.BatchRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.ReturnPacketRepository;
import com.moglix.wms.repository.ReturnPickupListRepository;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IPacketService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.ISupplierReturnService;
import com.moglix.wms.service.IWarehouseService;
import com.moglix.wms.util.DateUtil;
import com.moglix.wms.util.NumberUtil;
import com.moglix.wms.util.PaginationUtil;

@Service(value = "debitNoteService")
public class SupplierReturnServiceImpl implements ISupplierReturnService {

	Logger logger = LogManager.getLogger(SupplierReturnServiceImpl.class);
	
	@Autowired
	private ReturnPickupListRepository returnPickupListRepository;

	@Autowired
	private InboundRepository inboundrepo;
	
	@Autowired
	private ReturnPacketRepository returnPacketRepository;
	
	@Autowired
	@Qualifier("inventoryService")
	IInventoryService inventoryService;
	
	@Autowired
	@Qualifier("productService")
	IProductService productService;
	
	@Autowired
	IWarehouseService warehouseService;
	
	
	@Autowired
	@Qualifier("packetServiceImpl")
	IPacketService packetService;
	
		
	@Autowired
	IInboundStorageService inboundStorageService;
	
	@Autowired
	@Qualifier("batchService")
	IBatchService batchService;
	
	@Autowired
	private ProductsRepository prodRepo;
	
	@Autowired
	private ReturnPickupListRepository returnPickupListRepo;
	
	@Autowired
	private BatchRepository batchRepo;
	
	@Override
	public ReturnPickupList upsert(ReturnPickupList obj) {
		return returnPickupListRepository.save(obj);
	}

	@Override
	public ReturnPickupList getById(Integer id) {
		return returnPickupListRepository.findById(id).orElse(null);
	}
	
	@Override
	@Transactional
	public CreateSupplierCreditNoteByMRNResponse createSupplierCreditNoteByMRN(CreateSupplierCreditNoteByMRNRequest data) {
		
		logger.info("Inside createSupplierCreditNoteByMRN()");
		
		Batch batch = batchService.findByRefNoAndBatchType(data.getRefNo(), BatchType.INBOUND);
		logger.info("Batch found with MRN Id :: [" + data.getRefNo() + "]");
		
		List<Batch> childBatches = batchRepo.findByParentRefNo(data.getRefNo());
		logger.info("Child Batch :: ");
		
		if(batch == null) {
			return new CreateSupplierCreditNoteByMRNResponse("No Batch Found matching with Mrn Id :: [" + data.getRefNo() + "]", true, 200);
		}
		
		Set<Inbound> dnInbounds = batch.getInbounds();

		if(!CollectionUtils.isEmpty(childBatches)) {
			for(Batch childBatch: childBatches) {
				dnInbounds.addAll(childBatch.getInbounds());
			}
		}
		
		ReturnPickupList returnPickupList = new ReturnPickupList();
		
		Set<ReturnPickupListItem>returnPickupListItems = new HashSet<>();
		
		List<InboundStorage>storages = new ArrayList<>();
		returnPickupList.setSupplierName(batch.getSupplierName());
		returnPickupList.setSupplierId(batch.getSupplierId());
		returnPickupList.setTotalQuantity(data.getItems().stream().mapToDouble(CreditNoteItems :: getQuantity).sum());
		if(data.getStatus().equalsIgnoreCase("INITIATED")) {
			logger.info("State set to be INITIATED for Returnpickuplist supplier id :: " + returnPickupList.getSupplierId());
			returnPickupList.setStatus(ReturnPickupListStatus.INITIATED);
		}
		else {
			logger.info("State set to be CREATED for Returnpickuplist :: " + returnPickupList.getSupplierId());
			returnPickupList.setStatus(ReturnPickupListStatus.CREATED);
		}
		if(data.getCreditNoteNumber() != null) {
			returnPickupList.setCreditNoteNumber(data.getCreditNoteNumber());
		}
		returnPickupList.setEmsReturnNoteId(data.getEmsReturnNoteId());
		returnPickupList.setWarehouse(warehouseService.getById(data.getWarehouseId()));
		 
		logger.info("CreditNoteNumber :: " + data.getCreditNoteNumber());
		logger.info("Total Quantity :: " + data.getItems().stream().mapToDouble(CreditNoteItems :: getQuantity).sum());
		logger.info("Get Items :: " + data.getItems().size());
		
		for(CreditNoteItems items : data.getItems()) {
			
			logger.info("Get Item Details For CreditNoteNumber :: " + data.getCreditNoteNumber() +" :: ProductMSN = " + items.getProductMsn() + ", Quantity = " + items.getQuantity() + ", SupplierPoItemId = " + items.getSupplierPoItemId());
			
			Double deductQuantity = items.getQuantity();
			logger.info("Deduct Quantity :1: " + deductQuantity);

			List<Inbound>inbounds = dnInbounds.stream()
					.filter(e -> e.getProduct().getProductMsn().equals(items.getProductMsn()) && Integer.compare(items.getSupplierPoItemId(), e.getSupplierPoItemId()) == 0)
					.collect(Collectors.toList());
			logger.info("List of inbounds :: " + inbounds.size());
			
			for(Inbound inbound : inbounds) {
			
				logger.info("List of InboundsStorages :: WareHouse ID :: " + inbound.getWarehouseId() + ", WareHouseName :: " + inbound.getWarehouseName() + ", Supplier ID :: " +inbound.getSupplierId() + ", Supplier PO_ID :: " + inbound.getSupplierPoId() + ", Supplier PO_Item Id :: " +inbound.getSupplierPoItemId());
				logger.info("List of InboundsStorages :: " + inbound.getInboundStorages().size());
				
				for(InboundStorage storage: inbound.getInboundStorages()) {
					
					logger.info("InboundStorage Details :: Storage Location Id =" + storage.getStorageLocation().getId() + ", Storage Location Name =" 
					+ storage.getStorageLocation().getName() + " ,Bin Id : " + storage.getStorageLocation().getBin().getId()
					+ ",Bin Name : " + storage.getStorageLocation().getBin().getName() + " ,Rack Id : " + storage.getStorageLocation().getBin().getRack().getId()
					+ " ,Rack Name : " + storage.getStorageLocation().getBin().getRack().getName());
					
					double deductedQuantity = Math.min(storage.getAvailableQuantity(), deductQuantity);
					logger.info("Deducted Quantity :: " + deductedQuantity);

					logger.info("Before Deduction Available Quantity :: " + storage.getAvailableQuantity());
					logger.info("Before Deduction Total Quantity :: "     + storage.getQuantity());
					
					if(NumberUtil.round4(storage.getAvailableQuantity() - deductedQuantity) < 0) {
						storage.setAvailableQuantity(0.0);	
						logger.error("Available quantity should not be negative in Inbound Storage :: ProductMsn ::[" +  storage.getProduct().getProductMsn() +"] :: WarehouseId :: ["+ data.getWarehouseId() +"]");
					}
					else {
						storage.setAvailableQuantity(storage.getAvailableQuantity() - deductedQuantity);	
					}
					
					if(NumberUtil.round4(storage.getQuantity() - deductedQuantity) < 0) {
						storage.setQuantity(0.0);	
						logger.error("Quantity should not be negative in Inbound Storage :: ProductMsn ::[" +  storage.getProduct().getProductMsn() +"] :: WarehouseId :: ["+ data.getWarehouseId() +"]");
					}
					else {
						storage.setQuantity(storage.getQuantity() - deductedQuantity);	
					}
					
					logger.info("After Deduction Available Quantity :: " + storage.getAvailableQuantity());
					logger.info("After Deduction Total Quantity :: " + storage.getQuantity());
					
					ReturnPickupListItem item = new ReturnPickupListItem();
					item.setProduct(inbound.getProduct());
					item.setStorageLocation(storage.getStorageLocation());
					item.setReturnPickupList(returnPickupList);
					item.setQuantity(deductedQuantity);
					//item.setQuantity(items.getQuantity());
					returnPickupListItems.add(item);
					
					deductQuantity = deductQuantity - deductedQuantity;
					
					logger.info("Deduct Quantity :2: " + deductQuantity);
					
					item.setInboundStorage(storage);
					storages.add(storage);
					
					if(deductQuantity == 0) {
						break;
					}
				}
			}
			if(deductQuantity != 0) {
				return new CreateSupplierCreditNoteByMRNResponse("Partial Return Not Possible for PO_item_id: " + items.getSupplierPoItemId() + " Total Free items are: " + (items.getQuantity() - deductQuantity), false, 200);
			}
			else {
				Product product = prodRepo.getUniqueByProductMsn(items.getProductMsn());
				inventoryService.deductAvailableInventory(data.getWarehouseId(), product.getId(), items.getQuantity());
			}
		}
					
		inboundStorageService.saveAll(storages);			
		returnPickupList.setReturnPickupListItems(returnPickupListItems);
		logger.info("All storages saved.");
		returnPickupListRepo.save(returnPickupList);
		return new CreateSupplierCreditNoteByMRNResponse("Supplier Credit Note Successfully generated.", true, 200);		
	}
	

	@Override
	@Transactional
	public GetSuppliersByPacketResponse getSuppliersByPacket(Integer emsPacketId) {
		Packet packet = packetService.findByEmsPacketId(emsPacketId).orElse(null);
		if(packet == null) {
			return new GetSuppliersByPacketResponse("No Packets found for EMS Packet ID: " + emsPacketId, true, HttpStatus.OK.value());
		}
		GetSuppliersByPacketResponse response = new GetSuppliersByPacketResponse("Successfully found products for EMS Packet ID: " + emsPacketId, true, HttpStatus.OK.value());

		Map<Integer, String> supplierMap = packet.getPacketItems().stream().collect(Collectors.toMap(e -> e.getInboundStorage().getInbound().getSupplierId(), e -> e.getInboundStorage().getInbound().getSupplierName(), (e1, e2) -> e1));
		
		for (Map.Entry<Integer, String> entry : supplierMap.entrySet()) {

			com.moglix.wms.dto.ProductPacketResponseDTO.SupplierInfo sInfo = new com.moglix.wms.dto.ProductPacketResponseDTO.SupplierInfo();

			sInfo.setSupplierId(entry.getKey());
			sInfo.setSupplierName(entry.getValue());
			response.getSuppliers().add(sInfo);
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public GetSuppliersByReturnPacketResponse getSuppliersByReturnPacket(Integer emsReturnId) {
		List<Batch> batches = batchService.getReturnedBatches(emsReturnId);
		if(batches.isEmpty()) {
			return new GetSuppliersByReturnPacketResponse("No Batches found for EMS Return ID: " + emsReturnId, true, HttpStatus.OK.value());
		}
		
		GetSuppliersByReturnPacketResponse response = new GetSuppliersByReturnPacketResponse("Successfully found Suppliers for EMS Return ID: " + emsReturnId, true, HttpStatus.OK.value());

		
		for (Batch batch : batches) {
			for(Inbound inbound: batch.getInbounds()) {
				SupplierInfo sInfo = new SupplierInfo();
				sInfo.setSupplierId(batch.getSupplierId());
				sInfo.setQuantity(inbound.getQuantity());
				sInfo.setSupplierName(batch.getSupplierName());
				sInfo.setSupplierPoId(inbound.getSupplierPoId());
				sInfo.setSupplierPoItemId(inbound.getSupplierPoItemId());
				response.getSuppliers().add(sInfo);
			}
		}
		
		return response;
	}

	@Override
	@Transactional
	public SearchReturnPickupListResponse searchReturnPickup(SearchReturnPickupListRequest request, Pageable page) {
		logger.info("Serch Return pickup list Service Started");
		SearchReturnPickupListResponse response;
		Page<ReturnPickupList> returnPickupLists;
		if(request.getWarehouseId() != null && request.getSearchKey() != null) {
			returnPickupLists = returnPickupListRepository.findByWarehouseIdAndCreditNoteNumberAndStatusNotOrderByCreated(request.getWarehouseId(), request.getSearchKey(), ReturnPickupListStatus.CANCELLED, page);

		}
		else if(request.getWarehouseId() != null && request.getSearchKey() == null) {
			returnPickupLists = returnPickupListRepository.findByWarehouseIdAndStatusNotOrderByCreated(request.getWarehouseId(), ReturnPickupListStatus.CANCELLED, page);
		} 
		else if(request.getWarehouseId() == null && request.getSearchKey() != null) {
			returnPickupLists = returnPickupListRepository.findByCreditNoteNumberAndStatusNotOrderByCreated(request.getSearchKey(), ReturnPickupListStatus.CANCELLED, page);

		}
		else {
			returnPickupLists = returnPickupListRepository.findByStatusNotOrderByCreated(ReturnPickupListStatus.CANCELLED, page);
		}
		
		List<ReturnPickupListDto> returnPickupListDtos = new ArrayList<>();
		if(!returnPickupLists.getContent().isEmpty()) {
			for(ReturnPickupList returnPickupList : returnPickupLists) {
				returnPickupListDtos.add(new ReturnPickupListDto(returnPickupList));
			}
			response = (SearchReturnPickupListResponse) PaginationUtil.setPaginationParams(returnPickupLists, new SearchReturnPickupListResponse("Return pick up list found : " + returnPickupLists.getTotalElements(), true, HttpStatus.OK.value()));
			response.setReturnPickupList(returnPickupListDtos);
		} else {
			response = new SearchReturnPickupListResponse("No Return pickup list found", true, HttpStatus.OK.value());
		}
		logger.info("Search Return pickup list Service Ended");
		return response;
	}


	@Override
	@Transactional
	public GetReturnPickupListByIdResponse getReturnPickupById(GetReturnPickupListByIdRequest request) {
		logger.info("Get Return pickup list Service Started");
		GetReturnPickupListByIdResponse response = new GetReturnPickupListByIdResponse();
		ReturnPickupList returnPickupList = getById(request.getId());
		if(returnPickupList == null) {
			response.setMessage("Return pickup list details not found for id: " + request.getId());
		} else {
			ReturnPickupListDto returnPickupListDto = new ReturnPickupListDto(returnPickupList);
			for(ReturnPickupListItem item : returnPickupList.getReturnPickupListItems()) {
				returnPickupListDto.getReturnPickupListItems().add(new ReturnPickupListItemDto(item));
			}
			response.setMessage("Return pickup list found");
			response.setStatus(true);
			response.setReturnPickupList(returnPickupListDto);

		}
		logger.info("Get Return pickup list Service Ended");
		return response;
	}

	@Override
	@Transactional
	public SearchReturnPacketsResponse getPacketReturns(Integer warehouseId, String searchKey, PacketStatus status,
			Pageable page) {

		logger.info("Get Return packets Service Started");
		SearchReturnPacketsResponse response;
		Page<ReturnPacket> returnPackets;

		if (warehouseId == null && searchKey == null) {
			logger.info("Query with no warehouse ID and searchkey");
			returnPackets = returnPacketRepository.findByStatusOrderByCreated(page, status);
		} else if (warehouseId != null && searchKey != null) {
			logger.info("Query with warehouse id: " + warehouseId + " and searchkey: " + searchKey);
			returnPackets = returnPacketRepository.findByWarehouseIdAndInvoiceNumberAndStatusOrderByCreated(warehouseId,
					searchKey, page, status);
			if (!returnPackets.hasContent()) {
				try {
					Integer emsPacketId = Integer.parseInt(searchKey);
					returnPackets = returnPacketRepository.findByWarehouseIdAndEmsPacketIdAndStatusOrderByCreated(warehouseId,
							emsPacketId, page, status);
				} catch (NumberFormatException e) {
					logger.warn("Number to big to be parsed as EMS Packet ID. Possibly invoice number: " + searchKey);
				}
			}
			if (!returnPackets.hasContent()) {
				try {
					Date date = DateUtil.convertStringToDate(searchKey, "yyyy-MM-dd", true);
					if(date != null) {
						Date start = DateUtil.trimDate(date);
						Date end  = DateUtil.endDate(date);
						returnPackets = returnPacketRepository.findByWarehouseIdAndCreatedBetweenAndStatusOrderByCreated(warehouseId,
								start, end, page, status);
					}
					
				} catch (NumberFormatException e) {
					logger.warn("Number to big to be parsed as EMS Packet ID or Date. Possibly invoice number: " + searchKey);
				}
			}

		}else if(warehouseId != null) {
			//NUll searchkey
			logger.info("Query with warehouse id: " + warehouseId + " and no searchkey");
			returnPackets = returnPacketRepository.findByWarehouseIdAndStatusOrderByCreated(warehouseId, page, status);
		}else {
			//Null warehouse
			logger.info("Query with no warehouse id and searchkey: " + searchKey);
			returnPackets = returnPacketRepository.findByInvoiceNumberAndStatusOrderByCreated(searchKey, page, status);
			if (!returnPackets.hasContent()) {
				try {
					Integer emsPacketId = Integer.parseInt(searchKey);
					returnPackets = returnPacketRepository.findByEmsPacketIdAndStatusOrderByCreated(emsPacketId, page, status);
				} catch (NumberFormatException e) {
					logger.warn("Number to big to be parsed as EMS Packet ID. Possibly invoice number: " + searchKey);
				}
			}
			if (!returnPackets.hasContent()) {
				try {
					Date date = DateUtil.convertStringToDate(searchKey, "yyyy-MM-dd",true);
					if(date != null) {
						Date start = DateUtil.trimDate(date);
						Date end  = DateUtil.endDate(date);
						returnPackets = returnPacketRepository.findByCreatedBetweenAndStatusOrderByCreated(start, end, page, status);
					}
					
				} catch (NumberFormatException e) {
					logger.warn("Number to big to be parsed as EMS Packet ID or Date. Possibly invoice number: " + searchKey);
				}
			}
			
		}
		List<ReturnPacketDTO> returnPacketDtos = new ArrayList<>();
		if (!returnPackets.getContent().isEmpty()) {

			logger.info(returnPackets.getTotalElements() + "Returned Packets found");
			for (ReturnPacket returnPacket : returnPackets.getContent()) {
				returnPacketDtos.add(new ReturnPacketDTO(returnPacket));
			}
			response = (SearchReturnPacketsResponse) PaginationUtil.setPaginationParams(returnPackets,
					new SearchReturnPacketsResponse("Return pick up list found : " + returnPackets.getTotalElements(),
							true, HttpStatus.OK.value()));

			response.setReturnPackets(returnPacketDtos);
		} else {
			logger.info("No Return Packets Found");
			response = new SearchReturnPacketsResponse("No Return Packets found", true, HttpStatus.OK.value());
		}

		return response;
	}
	
	@Override
	@Transactional
	public BaseResponse checkDebiNoteEligiblity(Integer emsReturnId, Integer supplierId) {
		Batch batch = batchService.findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(emsReturnId, supplierId).orElse(null);
		
		if(batch == null) {
			return new BaseResponse("No return found for return Id: " + emsReturnId + " and supplier Id: +" + supplierId, false, 200);
		}
		
		
		Set<Inbound> inbounds = batch.getInbounds();
		
		for(Inbound inbound : inbounds) {
			if(inbound.getStatus().name().equals(InboundStatusType.STARTED.name())){
				return new BaseResponse("Cannot create Debit Note because returned Items are not Assigned to bin for returnId: " + emsReturnId + " and supplierId: " + supplierId, false, 200);
			}
		}
		
		return new BaseResponse("DebitNote validation passed", true, 200);
	}	
	
	@Override
	@Transactional
	public CreateSupplierDebitNoteByFreeQuantityResponse createSupplierCreditNoteByFreeQuantity(CreateSupplierDebitNoteByFreeQuantityRequest data) {
		
		logger.info("Inside createSupplierCreditNoteByFreeQuantity()");
		Set<ReturnPickupListItem>returnPickupListItems = new HashSet<>();
		
		ReturnPickupList returnPickUpList = new ReturnPickupList();
		returnPickUpList.setSupplierId(data.getSupplierId());
		returnPickUpList.setSupplierPoId(data.getSupplierPoId());
		if(data.getDebitNoteNumber()==null || data.getStatus().equalsIgnoreCase("INITIATED")) {
			logger.info("State set to be INITIATED for Returnpickuplist supplier id ::"+returnPickUpList.getSupplierId());
			returnPickUpList.setStatus(ReturnPickupListStatus.INITIATED);
		}else{
			logger.info("State set to be CREATED for Returnpickuplist ::"+returnPickUpList.getSupplierId());
			returnPickUpList.setStatus(ReturnPickupListStatus.CREATED);	
		}
		if(data.getDebitNoteNumber()!=null) {
			returnPickUpList.setCreditNoteNumber(data.getDebitNoteNumber());
		}
		returnPickUpList.setEmsReturnNoteId(data.getEmsReturnNoteId());
		returnPickUpList.setSupplierName(data.getSupplierName());
		returnPickUpList.setWarehouse(warehouseService.getById(data.getWarehouseId()));
		returnPickUpList.setTotalQuantity(data.getItems().stream().mapToDouble(ReturnPickupListItemMapping :: getQuantity).sum());
		logger.info("Get ReturnPickupListItems :: " + data.getItems());
		for(ReturnPickupListItemMapping item : data.getItems()) {
			double itemQuantity = item.getQuantity();
			
			Set<InboundStorage> inboundStorages = inboundrepo.findById(item.getInboundId()).orElse(new Inbound()).getInboundStorages();
			for(InboundStorage storage: inboundStorages) {
				if(storage.getAvailableQuantity() > 0) {
					if(itemQuantity <= 0) {
						break;
					}
					double pickupListItemQuantity = Math.min(itemQuantity, storage.getAvailableQuantity());
					ReturnPickupListItem returnPickupListItem = new ReturnPickupListItem();
					returnPickupListItem.setStorageLocation(storage.getStorageLocation());
					returnPickupListItem.setProduct(prodRepo.getUniqueByProductMsn(item.getProductMsn()));
					returnPickupListItem.setReturnPickupList(returnPickUpList);
					returnPickupListItem.setQuantity(pickupListItemQuantity);
					returnPickupListItem.setInboundStorage(storage);
					returnPickupListItems.add(returnPickupListItem);
					itemQuantity = itemQuantity - pickupListItemQuantity;
				}
			}
		}
		
		returnPickUpList.setReturnPickupListItems(returnPickupListItems);
		//logger.info("Get ReturnPickupList :: " + returnPickupListItems);

		Map<Integer, Double> productMsns = data.getItems().stream().collect(Collectors.toMap(ReturnPickupListItemMapping::getInboundId, ReturnPickupListItemMapping::getQuantity));
		
		List<Integer>inboundIds = data.getItems().stream().map(e -> e.getInboundId()).collect(Collectors.toList());
		
		List<Inbound> inbounds = new ArrayList<>();
		
		for (Integer inboundId : inboundIds) {
			
			Inbound inbound = inboundrepo.findById(inboundId).orElse(null);
			
			inbound.setInventorisableQuantity(Math.max(0, NumberUtil.round4(
					inbound.getInventorisableQuantity() - productMsns.get(inbound.getId()))));
			
			deductQuantityFromInboundStoragesforInbound(inbound, productMsns.get(inbound.getId()));
			
			inbound.setCreditDoneQuantity(NumberUtil.round4(
					inbound.getCreditDoneQuantity() + productMsns.get(inbound.getId())));
			
			inventoryService.deductAvailableInventory(inbound.getWarehouseId(), inbound.getProduct().getId(), productMsns.get(inbound.getId()));
			inbounds.add(inbound);
		}
		logger.info("Save all Inbounds :: ");
		inboundrepo.saveAll(inbounds);
		returnPickupListRepo.save(returnPickUpList);
		return new CreateSupplierDebitNoteByFreeQuantityResponse("Return Pickup List created", true, 200);
	}
	
	@Override
	@Transactional
	public CreateSupplierCreditNoteByReturnResponse createSupplierCreditNoteByReturn(CreateSupplierCreditNoteByReturnRequest data) {	

		Set<ReturnPickupListItem>returnPickupListItems = new HashSet<>();
		
		ReturnPickupList returnPickUpList = new ReturnPickupList();
		returnPickUpList.setSupplierId(data.getSupplierId());
		returnPickUpList.setSupplierPoId(data.getSupplierPoId());
		if(data.getStatus().equalsIgnoreCase("INITIATED")) {
			logger.info("State set to be INITIATED for Returnpickuplist supplier id ::"+returnPickUpList.getSupplierId());
			returnPickUpList.setStatus(ReturnPickupListStatus.INITIATED);
		}else{
			logger.info("State set to be CREATED for Returnpickuplist ::"+returnPickUpList.getSupplierId());
			returnPickUpList.setStatus(ReturnPickupListStatus.CREATED);
		}
		if(data.getCreditNoteNumber()!=null) {
		returnPickUpList.setCreditNoteNumber(data.getCreditNoteNumber());
		}
		returnPickUpList.setEmsReturnNoteId(data.getEmsReturnNoteId());
		returnPickUpList.setSupplierName(data.getSupplierName());
		returnPickUpList.setWarehouse(warehouseService.getById(data.getWarehouseId()));
		returnPickUpList.setReturnPacket(returnPacketRepository.findByEmsReturnId(data.getEmsReturnId()).orElse(null));
		returnPickUpList.setTotalQuantity(data.getReturnPickupListItemMapping().stream().mapToDouble(ReturnPickupListItemMapping :: getQuantity).sum());
		for(ReturnPickupListItemMapping item : data.getReturnPickupListItemMapping()) {
			Set<InboundStorage> inboundStorages = inboundrepo.findById(item.getInboundId()).orElse(new Inbound()).getInboundStorages();
			inboundStorages = inboundStorages.stream().filter(is -> is.getAvailableQuantity() > 0).collect(Collectors.toSet());
			for(InboundStorage storage: inboundStorages) {
				ReturnPickupListItem returnPickupListItem = new ReturnPickupListItem();
				returnPickupListItem.setStorageLocation(storage.getStorageLocation());
				returnPickupListItem.setProduct(prodRepo.getUniqueByProductMsn(item.getProductMsn()));
				returnPickupListItem.setReturnPickupList(returnPickUpList);
				returnPickupListItem.setQuantity(item.getQuantity());
				returnPickupListItem.setInboundStorage(storage);
				returnPickupListItems.add(returnPickupListItem);
			}
		}
		
		returnPickUpList.setReturnPickupListItems(returnPickupListItems);
		//Deduct Inventorisable quantity
		Batch batch = batchService.findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(data.getEmsReturnId(), data.getSupplierId()).orElse(null);
		
		if(batch == null) {
			return new CreateSupplierCreditNoteByReturnResponse("Cannot Create return Pickup List", false, 200);
		}
		Map<String, Double> productMsns = data.getReturnPickupListItemMapping().stream().collect(Collectors.toMap(ReturnPickupListItemMapping::getProductMsn, ReturnPickupListItemMapping::getQuantity));
		List<Inbound>inbounds = new ArrayList<>();
		
		List<Integer>inboundIds = data.getReturnPickupListItemMapping().stream().map(e -> e.getInboundId()).collect(Collectors.toList());
		
		for (Inbound inbound : batch.getInbounds()) {
			Product prod = inbound.getProduct();
			if (productMsns.get(prod.getProductMsn()) != null && Integer.compare(inbound.getSupplierPoId(), data.getSupplierPoId()) == 0 && inboundIds.contains(inbound.getId())) {
				inbound.setInventorisableQuantity(NumberUtil.round4(
						inbound.getInventorisableQuantity() - productMsns.get(prod.getProductMsn())));
				
				deductQuantityFromInboundStoragesforInbound(inbound, productMsns.get(prod.getProductMsn()));
				
				inbound.setCreditDoneQuantity(NumberUtil.round4(
						inbound.getCreditDoneQuantity() + productMsns.get(prod.getProductMsn())));
				inbounds.add(inbound);
			}
		}		
		inboundrepo.saveAll(inbounds);
		returnPickupListRepo.save(returnPickUpList);
		return new CreateSupplierCreditNoteByReturnResponse("Return Pickup List created", true, 200);
	}
	

	private void deductQuantityFromInboundStoragesforInbound(Inbound inbound, Double deductQuantity) {
		logger.info("Deducting available quantities from inbound storage for inbound: " + inbound.getId());
		Set<InboundStorage> inboundStorages = inbound.getInboundStorages();
		logger.trace("Found " + inboundStorages.size() + " for inbound: " + inbound.getId());
		
		for(InboundStorage inboundStorage: inboundStorages) {
			logger.trace("Inside inboundStorages loop for deducting available quantities");
			logger.debug("Inbound Storage Available Quantity: " + inboundStorage.getAvailableQuantity());
			logger.debug("Inbound Storage Current Quantity: " + inboundStorage.getQuantity());
			logger.debug("Debit Note Deduct Quantity: " + deductQuantity);
			
			double deductibleQuantity = Math.min(inboundStorage.getAvailableQuantity(), deductQuantity);
			
			logger.debug("Deducting " + deductibleQuantity + " quantity from inbound storage: " + inboundStorage.getId());
			
			inboundStorage.setAvailableQuantity(NumberUtil.round4(inboundStorage.getAvailableQuantity() - deductibleQuantity));
			
			inboundStorage.setQuantity(NumberUtil.round4(inboundStorage.getQuantity() - deductibleQuantity));
			
			logger.debug("New Inbound Storage Available Quantity: " + inboundStorage.getAvailableQuantity());
			
			logger.debug("New Inbound Storage Current Quantity: " + inboundStorage.getQuantity());

			deductQuantity = NumberUtil.round4(deductQuantity - deductibleQuantity);
			
			logger.debug("LeftOver Deduct Quantity: " + deductQuantity);
			
			if(deductQuantity == 0) {
				logger.trace("No quantity left to be deducted. Breaking the loop");
				break;
			}
		}
	}
	
	@Override
	@Transactional
	public DeleteBatchResponse checkIfBatchIsDeletable(Integer emsReturnId) {
		List<Batch> batches = batchService.findByEmsReturnId(emsReturnId);
		DeleteBatchResponse response = new DeleteBatchResponse();
		response.setMessage("Return can be cancelled");
		boolean isDeletable = false;
		if(CollectionUtils.isEmpty(batches)) {
			response.setMessage("No batches found for Ems Return Id: " + emsReturnId);
			return response;
		}
		for (Batch batch: batches) {
			isDeletable = batchService.checkIfBatchIsDeletable(batch.getRefNo(), batch.getBatchType()).getStatus();
			if(isDeletable == false) {
				response.setMessage("Cannot cancel return since some of the Items are inventorised");
				break;
			}
		}
		response.setStatus(isDeletable);
		return response;
	}

	@Override
	@Transactional
	public DeleteBatchResponse cancelReturn(Integer emsReturnId) {
		DeleteBatchResponse response = new DeleteBatchResponse();
		ReturnPacket returnPacket = returnPacketRepository.findByEmsReturnId(emsReturnId).orElse(null);
		
		if(returnPacket != null) {
			returnPacket.setStatus(PacketStatus.CANCELLED);
			returnPacketRepository.save(returnPacket);
		}
		
		List<Batch> batches = batchService.findByEmsReturnIdAndBatchType(emsReturnId, BatchType.CUSTOMER_RETURN);
		if(!batches.isEmpty()) {
			Set<Integer> inbounds = batches.stream().flatMap(e -> e.getInbounds().stream()).map(e -> e.getId()).collect(Collectors.toSet()); 
			
			inboundrepo.deleteInbounds(inbounds);
			
			Set<Integer> batchesId = batches.stream().map(e -> e.getId()).collect(Collectors.toSet());
			batchRepo.deleteBatches(batchesId);
			
			response.setMessage("Return Deleted successfully");
			response.setStatus(true);
		}else {
			response.setMessage("No Batches Found");
			response.setStatus(false);
		}
		return response;
	}

	@Override
	public BaseResponse deductInventorisable(@Valid DeductInventorisableRequest request) {

		Batch returnBatch = batchService.findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(request.getEmsReturnId(), request.getSupplierId()).orElse(null);

		if(returnBatch != null) {
			Map<String, Double> productMsnMap = request.getProductQuantity().stream().collect(Collectors.toMap(ProductQuantity::getProductMsn, ProductQuantity::getQuantity));

			List<Inbound>returnInbounds = new ArrayList<>();
			for (Inbound inbound : returnBatch.getInbounds()) {
				Product prod = inbound.getProduct();
				if (productMsnMap.get(prod.getProductMsn()) != null) {
					// if action add then add inventorisable and subtract debit done
					if (request.getAction().name().equals(InventoriseAction.SUBTRACT.toString())) {
						logger.info("Action: SUBTRACT");
						logger.info("Setting inbound inventorisable quantity as: " + NumberUtil
								.round4(inbound.getInventorisableQuantity() + productMsnMap.get(prod.getProductMsn())));
						inbound.setInventorisableQuantity(NumberUtil
								.round4(inbound.getInventorisableQuantity() + productMsnMap.get(prod.getProductMsn())));

						logger.info("Setting inbound Credit done quantity as: " + NumberUtil
								.round4(inbound.getCreditDoneQuantity() - productMsnMap.get(prod.getProductMsn())));
						inbound.setCreditDoneQuantity(NumberUtil
								.round4(inbound.getCreditDoneQuantity() - productMsnMap.get(prod.getProductMsn())));
					} else if (request.getAction().name().equals(InventoriseAction.ADD.toString())) {
						logger.info("Action: ADD");
						logger.info("Setting inbound inventorisable quantity as: " + NumberUtil
								.round4(inbound.getInventorisableQuantity() - productMsnMap.get(prod.getProductMsn())));
						inbound.setInventorisableQuantity(NumberUtil
								.round4(inbound.getInventorisableQuantity() - productMsnMap.get(prod.getProductMsn())));
						logger.info("Setting inbound Credit done quantity as: " + NumberUtil
								.round4(inbound.getCreditDoneQuantity() + productMsnMap.get(prod.getProductMsn())));
						inbound.setCreditDoneQuantity(NumberUtil
								.round4(inbound.getCreditDoneQuantity() + productMsnMap.get(prod.getProductMsn())));

					}
					returnInbounds.add(inbound);
				}
			}
			
			batchRepo.save(returnBatch);
		}
		
		
		return new BaseResponse("Quantity Deducted Successfully", true, HttpStatus.OK.value());
	}

	@Override
	@Transactional
	public BaseResponse cancelReturnPickupList(Integer emsreturnNoteId) {

		List<InboundStorage>storages = new ArrayList<>();

		logger.info("Cancelling return pickupLst for EMS_Return_NoteID :: [" + emsreturnNoteId + "]");
		
		ReturnPickupList returnPickupList = returnPickupListRepository.findByreturnnoteid(emsreturnNoteId);
		
		if (returnPickupList != null) {
			
			logger.info("Size return pickupList ::  " + returnPickupList.getReturnPickupListItems().size() );
			
			for(ReturnPickupListItem returnPickupListItem : returnPickupList.getReturnPickupListItems()) {
				
				InboundStorage storage =  returnPickupListItem.getInboundStorage();

				if(storage == null) {
					throw new CancelReturnPickupListException("Cannot Cancel return pickup list since no inbound storage found for ReturnNoteID: " + emsreturnNoteId);
				}
				
				logger.info("Get AvailableQuantity from storage :: " + storage.getAvailableQuantity());
				logger.info("Get Quantity from ReturnPickupListItem :: " + returnPickupListItem.getQuantity());
				
				storage.setAvailableQuantity(storage.getAvailableQuantity() + returnPickupListItem.getQuantity());
				logger.info("Get Storage :: " + storage.getQuantity());
				
				storage.setQuantity(storage.getQuantity() + returnPickupListItem.getQuantity());
				inventoryService.addAvailableInventory(returnPickupList.getWarehouse().getId(), returnPickupListItem.getProduct().getId(), returnPickupListItem.getQuantity());
				storages.add(storage);
			}
			
			inboundStorageService.saveAll(storages);			
			returnPickupList.setStatus(ReturnPickupListStatus.CANCELLED);
			returnPickupListRepository.save(returnPickupList);
		}else {
			logger.info("No Returnpickuplist Found for EmsReturnNoteId : "+emsreturnNoteId);
		}
		
		return new BaseResponse("ReturnPickupList: " + emsreturnNoteId + " Cancelled Successfully", true, HttpStatus.OK.value());
	}

	@Override
	public DNStatusUpdateResponse dnStatusUpdate(DNUpdateStatusRequest request) {
		
		logger.info("Dn status Update for request gor Return note id ::"+request.getReturnNoteId()+" With status "+request.getStatus());
		ReturnPickupList returnPickUpList=returnPickupListRepository.findByreturnnoteid(request.getReturnNoteId());
		
		if(returnPickUpList==null) {
		logger.info("Dn Not Found for Return note id ::"+request.getReturnNoteId());		
		return new DNStatusUpdateResponse("Returnnoteid Not Found for id :: "+request.getReturnNoteId(),false,200);
		}
		
		returnPickUpList.setCreditNoteNumber(request.getDebitNoteNumber());
		if(request.getStatus().equalsIgnoreCase("SHIPPED")) {
			logger.info("Setting State to SHIPPED for returnonteid "+request.getReturnNoteId());
			returnPickUpList.setStatus(ReturnPickupListStatus.SHIPPED);
		}else if(request.getStatus().equalsIgnoreCase("DELIVERED")) {
			logger.info("Setting State to DELIVERED for returnonteid "+request.getReturnNoteId());
			returnPickUpList.setStatus(ReturnPickupListStatus.DELIVERED);
		}else if(request.getStatus().equalsIgnoreCase("DELIVERED_POD")) {
			logger.info("Setting State to DELIVERED_POD for returnonteid "+request.getReturnNoteId());
			returnPickUpList.setStatus(ReturnPickupListStatus.DELIVERED_POD);
		}else if(request.getStatus().equalsIgnoreCase("CREATED")) {
			logger.info("Setting State to CREATED for returnonteid "+request.getReturnNoteId());
			returnPickUpList.setStatus(ReturnPickupListStatus.CREATED);
		}else if(request.getStatus().equalsIgnoreCase("QC_PASS")) {
			logger.info("Setting State to QC_PASS for returnonteid "+request.getReturnNoteId());
			returnPickUpList.setStatus(ReturnPickupListStatus.QC_PASS);
		}else if(request.getStatus().equalsIgnoreCase("QC_FAIL")) {
			logger.info("Setting State to QC_FAIL for returnonteid "+request.getReturnNoteId());
			returnPickUpList.setStatus(ReturnPickupListStatus.QC_FAIL);
		}else if(request.getStatus().equalsIgnoreCase("REJECTED")) {
			logger.info("Setting State to REJECTED for returnonteid "+request.getReturnNoteId());
			returnPickUpList.setStatus(ReturnPickupListStatus.REJECTED);
		}
		
		returnPickupListRepository.save(returnPickUpList);
		
		return new DNStatusUpdateResponse("Returnnoteid "+request.getReturnNoteId(),true,200);
		
		
	}

}

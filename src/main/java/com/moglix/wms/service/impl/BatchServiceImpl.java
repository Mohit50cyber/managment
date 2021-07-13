package com.moglix.wms.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.api.request.DeleteBatchRequest;
import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.api.request.ProductInput.ItemRefDetail;
import com.moglix.wms.api.request.ProductInput.LotInfo;
import com.moglix.wms.api.request.RollbackBatchRequest;
import com.moglix.wms.api.request.SupplierCNCancelRequest;
import com.moglix.wms.api.response.CancelSupplierCNResponse;
import com.moglix.wms.api.response.CreateBatchResponse;
import com.moglix.wms.api.response.DeleteBatchResponse;
import com.moglix.wms.api.response.FileUploadResponse;
import com.moglix.wms.api.response.RollbackBatchResponse;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.constants.ReturnPickupListStatus;
import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundLot;
import com.moglix.wms.entities.InventoryUploadHistory;
import com.moglix.wms.entities.Lot;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ReturnPacket;
import com.moglix.wms.entities.ReturnPickupList;
import com.moglix.wms.entities.ReturnPickupListItem;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMapping;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMappingItem;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.exception.WMSExpiredInventoryException;
import com.moglix.wms.mapper.BatchMapper;
import com.moglix.wms.mapper.InboundMapper;
import com.moglix.wms.mapper.ProductMapper;
import com.moglix.wms.repository.BatchRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.InventoryUploadHistoryRepository;
import com.moglix.wms.repository.LotRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.ReturnPacketRepository;
import com.moglix.wms.repository.ReturnPickupListRepository;
import com.moglix.wms.repository.SaleOrderSupplierPurchaseOrderMappingItemRepository;
import com.moglix.wms.repository.SaleOrderSupplierPurchaseOrderMappingRepository;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IPacketService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.util.BarcodeGenerator;

@Service(value = "batchService")
public class BatchServiceImpl implements IBatchService {

	private Logger log = LogManager.getLogger(BatchServiceImpl.class);

	@Autowired
	private BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("productService")
	private IProductService prodService;
	
	@Autowired
	private ProductsRepository prodRepo;

	@Autowired
    @Qualifier("inboundService")
	private IInboundService inboundService;
	
	@Autowired
	@Qualifier("inventoryService")
	private IInventoryService  inventoryService;
	
	@Autowired
	private InboundRepository inboundsRepo;

	@Autowired
	private InventoryUploadHistoryRepository inventoryUploadHistoryRepo;
	
	@Autowired
	private SaleOrderSupplierPurchaseOrderMappingRepository saleOrderSupplierPurchaseOrderMappingRepository;
	
	@Autowired
	private SaleOrderSupplierPurchaseOrderMappingItemRepository saleOrderSupplierPurchaseOrderMappingItemRepository;
	
	@Autowired
	@Qualifier("packetServiceImpl")
	private IPacketService packetService;
	
	@Autowired
	private ReturnPacketRepository returnPacketRepository;
	
	@Autowired
	private ReturnPickupListRepository returnPickupListRepository;
	
	@Autowired
	private LotRepository lotRepository;

	@Override
	@Transactional
	public CreateBatchResponse createBatch(CreateBatchRequest data) {
		
		log.info("Batch Creation started for refNo: " + data.getRefNo());
		log.trace("Batch Creation request: " + data.toString());
		
		Batch batch = BatchMapper.createEntityFromInput(data);
		batch = batchRepo.save(batch);
		
		Set<Inbound> inbounds       = new HashSet<>();
		Set<InboundLot> inboundLots = new HashSet<>();
		
		for (ProductInput prod : data.getProducts()) {
		
			Inbound inbound = InboundMapper.createEntityFromInput(prod);
			Product product = prodRepo.getUniqueByProductMsn(prod.getProductMsn());
		
			if (product == null) {
				product = prodService.add(ProductMapper.createEntityFromInput(prod));
				
			}
			if (batch.getBatchType().equals(BatchType.INBOUND) && product.getExpiryDateManagementEnabled() != null && product.getExpiryDateManagementEnabled()
					&& (prod.getExpDate() == null || prod.getExpDate().before(Date.from(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
							.plusDays(product.getShelfLife()).toInstant(ZoneId.of("Asia/Kolkata").getRules().getOffset(LocalDateTime.now(ZoneId.of("Asia/Kolkata")))))))) {
				throw new WMSExpiredInventoryException("Cannot Inbound Inventory as product: " + prod.getProductMsn()
						+ " has expired. Expiry date: " + prod.getExpDate() + " Shelf Life: " + product.getShelfLife());
			}
			
			inbound.setProduct(product);
			inbound.setSupplierName(data.getSupplierName());
			inbound.setSupplierId(data.getSupplierId());
			inbound.setWarehouseId(data.getWarehouseId());
			inbound.setWarehouseName(data.getWarehouseName());
			inbound.setPurchaseDate(data.getPurchaseDate());
			inbound.setStatus(InboundStatusType.STARTED);
			inbound.setBatch(batch);
			inbounds.add(inbound);
			
			List<LotInfo> lotInfos = prod.getLotInfo();
			
			if(batch.getBatchType().equals(BatchType.INBOUND) && product.getLotManagementEnabled() != null && product.getLotManagementEnabled()) {
				
				if(CollectionUtils.isEmpty(lotInfos)) {
					throw new WMSException("Batch not Created as LotManagement is Enabled and LotInfo is Empty");
				}
				else {
					List<String> lotMsnSupplierList =  new ArrayList<>();
					Set<Lot> lotMsnSupplierListLocal = new HashSet<>();
					
					for(LotInfo lotInfo : lotInfos) {
						String lotMsnSupplier = lotInfo.getLotNumber() + "_" + prod.getProductMsn() + "_" + data.getSupplierId();
						lotMsnSupplierList.add(lotMsnSupplier);
						Lot lotLocal = new Lot();
						lotLocal.setLotNumber(lotInfo.getLotNumber());
						lotLocal.setProductMsn(prod.getProductMsn());
						lotLocal.setSupplierId(data.getSupplierId());
						lotLocal.setLotMsnSupplierId(lotMsnSupplier);
						lotMsnSupplierListLocal.add(lotLocal);
					}
					
					Set<Lot> lotMsnSupplierDB = lotRepository.findAllByLotMsnSupplierId(lotMsnSupplierList);
					lotMsnSupplierListLocal.removeAll(lotMsnSupplierDB);
					
					if(!CollectionUtils.isEmpty(lotMsnSupplierListLocal)) {
						lotMsnSupplierListLocal = Sets.newHashSet(lotRepository.saveAll(lotMsnSupplierListLocal));
						lotMsnSupplierDB.addAll(lotMsnSupplierListLocal);
					}
					
					Map<String, Lot> lotMsnSupplierIdMap = new HashMap<>();
					for(Lot lotDB : lotMsnSupplierDB) {
						lotMsnSupplierIdMap.putIfAbsent(lotDB.getLotMsnSupplierId(), lotDB);
					}
					
					for(LotInfo lotInfo : lotInfos) {
						String lotMsnSupplier = lotInfo.getLotNumber() + "_" + prod.getProductMsn() + "_" + data.getSupplierId();
						InboundLot inboundLot = new InboundLot();
						inboundLot.setInbound(inbound);
						inboundLot.setQuantity(lotInfo.getQuantity());
						inboundLot.setLot(lotMsnSupplierIdMap.get(lotMsnSupplier));
						inboundLots.add(inboundLot);
					}
				}
				inbound.setInboundLotAssoc(inboundLots);
			}
			
			// Mapping Logic here 
			
			List<ItemRefDetail> itemRefDetails = prod.getItemRefDetails();
			
			if(batch.getBatchType().equals(BatchType.INBOUND) && !itemRefDetails.isEmpty()) {
				for(ItemRefDetail itemRefDetail : itemRefDetails) {
					
					log.info("Checking SaleOrder against ItemRef :: " + itemRefDetail.getItemRef());
					
					List<SaleOrderSupplierPurchaseOrderMapping> saleOrderSupplierPurchaseOrderMappingList = saleOrderSupplierPurchaseOrderMappingRepository.findAllByItemRefAndSupplierPoId(itemRefDetail.getItemRef(),prod.getSupplierPoId());
					
					if (saleOrderSupplierPurchaseOrderMappingList.isEmpty()) {
						log.info("SaleOrder not found for inventory inbounding for itemRef :: " + itemRefDetail.getItemRef());
					}
					else {
						SaleOrderSupplierPurchaseOrderMapping sospomObj = saleOrderSupplierPurchaseOrderMappingList.get(0);
						SaleOrderSupplierPurchaseOrderMappingItem saleOrderSupplierPurchaseOrderMappingItemObj = new SaleOrderSupplierPurchaseOrderMappingItem();
						
						saleOrderSupplierPurchaseOrderMappingItemObj.setBatchId(batch.getId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setRefNo(data.getRefNo());
						saleOrderSupplierPurchaseOrderMappingItemObj.setSaleOrderId(sospomObj.getSaleOrderId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setProductID(sospomObj.getProductID());
						saleOrderSupplierPurchaseOrderMappingItemObj.setProductMSN(prod.getProductMsn());
						saleOrderSupplierPurchaseOrderMappingItemObj.setProductName(prod.getProductName());
						saleOrderSupplierPurchaseOrderMappingItemObj.setOrderRef(sospomObj.getOrderRef());
						saleOrderSupplierPurchaseOrderMappingItemObj.setItemRef(itemRefDetail.getItemRef());
						saleOrderSupplierPurchaseOrderMappingItemObj.setQuantity(itemRefDetail.getQuantity());
						saleOrderSupplierPurchaseOrderMappingItemObj.setWarehouseId(sospomObj.getWarehouseId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setWarehouseName(sospomObj.getWarehouseName());
						saleOrderSupplierPurchaseOrderMappingItemObj.setPickUpWarehouseId(data.getWarehouseId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setPickUpWarehouseName(data.getWarehouseName());
						saleOrderSupplierPurchaseOrderMappingItemObj.setSupplierId(data.getSupplierId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setSupplierName(data.getSupplierName());
						saleOrderSupplierPurchaseOrderMappingItemObj.setSupplierPoId(prod.getSupplierPoId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setSupplierPoItemId(prod.getSupplierPoItemId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setSaleOrderSupplierPurchaseOrderMappingId(sospomObj.getId());
						saleOrderSupplierPurchaseOrderMappingItemObj.setIsActive(true);
						saleOrderSupplierPurchaseOrderMappingItemObj.setStatus(SaleOrderSupplierPurchaseOrderMappingStatus.INBOUNDED);
						saleOrderSupplierPurchaseOrderMappingItemRepository.save(saleOrderSupplierPurchaseOrderMappingItemObj);
						log.info("SaleOrderSupplierPurchaseOrderMappingItem saved against SaleOrder :: [" + sospomObj.getSaleOrderId() +"]");
					}
				}
			}
		}
		inboundService.saveAll(inbounds);
		
		//If Batch created is of type Customer Return then set status of all returned packets to BatchCreated
		if (batch.getBatchType().equals(BatchType.CUSTOMER_RETURN) && data.getEmsPacketId() != null) {
			ReturnPacket returnPacket = returnPacketRepository
					.findByEmsReturnIdAndStatusOrderByCreated(data.getEmsReturnId(), PacketStatus.RETURNED);

			if(returnPacket != null) {
				returnPacket.setStatus(PacketStatus.BATCH_CREATED);
				returnPacketRepository.save(returnPacket);
			}
		}
		
		if(batch.getBatchType().equals(BatchType.SUPPLIER_RETURN) && data.getRefNo() != null) {
			log.info("cancelling return pickupLst for creditNote: " + data.getRefNo());
			ReturnPickupList returnPickupList = returnPickupListRepository.findByCreditNoteNumber(data.getRefNo()).orElse(null);
			
			if (returnPickupList != null) {
				
				if (returnPickupList.getReturnPacket() != null){
					Integer emsReturnId = returnPickupList.getReturnPacket().getEmsReturnId();
					Set<ReturnPickupListItem> returnPickupListItems = returnPickupList.getReturnPickupListItems();

					Map<String, Double> productMsnMap = returnPickupListItems.stream()
							.collect(Collectors.toMap(e -> e.getProduct().getProductMsn() + "-" + e.getReturnPickupList().getSupplierPoId(), e -> e.getQuantity()));
					Batch returnBatch = findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(emsReturnId,
							data.getSupplierId()).orElse(null);

					List<Inbound> returnInbounds = new ArrayList<>();
					if (returnBatch != null) {
						for (Inbound inbound : returnBatch.getInbounds()) {
							Product prod = inbound.getProduct();
							if (productMsnMap.get(prod.getProductMsn() + "-" + inbound.getSupplierPoId()) != null && inbound.getCreditDoneQuantity() > 0) {
								inbound.setInventorisableQuantity(
										inbound.getInventorisableQuantity() + productMsnMap.get(prod.getProductMsn()+ "-" + inbound.getSupplierPoId()));
								inbound.setCreditDoneQuantity(
										inbound.getCreditDoneQuantity() - productMsnMap.get(prod.getProductMsn()+ "-" + inbound.getSupplierPoId()));
								returnInbounds.add(inbound);
							}
						}

						batchRepo.save(returnBatch);
					}
				}
				returnPickupList.setStatus(ReturnPickupListStatus.CANCELLED);
				returnPickupListRepository.save(returnPickupList);
			}
		}
		log.info("Batch created successfully for " + data.getBatchType() + ": " + data.getRefNo());

		CreateBatchResponse response  = new CreateBatchResponse("Batch Created", true, HttpStatus.OK.value());
		
		response.setBatchId(batch.getId());
		return response;
	}


    @Override
    @Transactional
    public RollbackBatchResponse rollbackBatch(RollbackBatchRequest request) {
		log.info("Rollback Batch Service Started");
		RollbackBatchResponse response = new RollbackBatchResponse();
        Batch batch = batchRepo.findById(request.getBatchId()).orElse(null);
        if(batch == null) {
            response.setMessage("Batch not found for id: " + request.getBatchId());
            response.setCode(HttpStatus.NOT_FOUND.value());
        } else {
            batchRepo.delete(batch);

            response.setMessage("Batch removed successfully with " + batch.getInbounds().size() + " inbounds");
            response.setStatus(true);
            response.setCode(HttpStatus.OK.value());
        }
		log.info("Rollback Batch Service Ended");
        return response;
    }

    @Override
	@Transactional
	public List<Batch> getReturnedBatches(Integer emsReturnId) {
		return batchRepo.findByEmsReturnId(emsReturnId).orElse(Collections.emptyList());		
	}  
    
    @Override
    @Transactional
    public Batch findByRefNoAndBatchType(String refNo, BatchType type) {
    	return batchRepo.findByRefNoAndBatchType(refNo, type).orElse(null);
    }
    
	@Override
	@Transactional
	public DeleteBatchResponse deleteBatch(DeleteBatchRequest request) {
		log.info("Delete Batch Service Started");
		DeleteBatchResponse response = new DeleteBatchResponse();
		Batch batch = batchRepo.findByRefNoAndBatchType(request.getRefNo(), request.getBatchType()).orElse(null);
		if(batch == null) {
			response.setMessage("Batch not found");
		} else {
			boolean remove = true;
			for(Inbound inbound : batch.getInbounds()) {
				if(!inbound.getStatus().equals(InboundStatusType.STARTED)) {
					remove = false;
					break;
				}
			}

			if(remove) {				
				Set<Integer> inbounds = batch.getInbounds().stream().map(e -> e.getId()).collect(Collectors.toSet());
				inboundsRepo.deleteInbounds(inbounds);
				batchRepo.deleteBatches(Collections.singleton(batch.getId()));
				response.setMessage("Batch deleted successfully with " + batch.getInbounds().size() + " inbounds");
			} else {
				response.setMessage("Batch cannot be deleted as some/all items has been assigned to bin");
			}
			response.setStatus(remove);
			response.setCode(HttpStatus.OK.value());
		}
		log.info("Delete Batch Service Ended");
		return response;
	}


	@Override
	@Transactional
	public Optional<Batch> findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(Integer emsReturnId,
			Integer supplierId) {
		return batchRepo.findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(emsReturnId, supplierId);
	}


	@Override
	@Transactional
	public DeleteBatchResponse checkIfBatchIsDeletable(String refNo, BatchType type) {
		DeleteBatchResponse response = new DeleteBatchResponse();

		Batch batch = batchRepo.findByRefNoAndBatchType(refNo, type).orElse(null);

		if (batch == null) {
			response.setMessage("Batch not found");
		} else {
			boolean remove = true;
			for (Inbound inbound : batch.getInbounds()) {
				if (!inbound.getStatus().equals(InboundStatusType.STARTED)) {
					remove = false;
					break;
				}
			}
			response.setStatus(remove);
		}
		return response;
	}


	@Override
	public List<Batch> findByEmsReturnId(Integer emsReturnId) {
		return batchRepo.findByEmsReturnId(emsReturnId).orElse(Collections.emptyList());
	}


	@Override
	public List<Batch> findByEmsReturnIdAndBatchType(Integer emsReturnId, BatchType customerReturn) {
		return batchRepo.findByEmsReturnIdAndBatchType(emsReturnId, BatchType.CUSTOMER_RETURN).orElse(Collections.emptyList());
	}


	@Override
	public FileUploadResponse fileUpload(MultipartFile file) throws IOException {
		
		Map<String, Object>contentMap = new HashMap<>();
		List<Map<String,String>>recordList = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		if (file.isEmpty()) {
            return new FileUploadResponse("File is empty", false, HttpStatus.OK.value(), "N/A");
		} else {
			String filename = new BarcodeGenerator().toHexString() + ".csv";
			byte[] bytes = file.getBytes();
			
			InventoryUploadHistory history = new InventoryUploadHistory();
			
			Path path = Paths.get(Constants.UPLOADED_FOLDER + filename);
			Files.write(path, bytes);
			
			BufferedReader reader = Files.newBufferedReader(path);
			
			CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
			
			for(CSVRecord record: parser.getRecords()) {
				recordList.add(record.toMap());
			}
			
			contentMap.put("content", recordList);
			
			history.setFileContent(mapper.writeValueAsString(contentMap));
			
			history.setOriginalFileName(file.getOriginalFilename());
			
			history.setModifiedName(filename);
			
			inventoryUploadHistoryRepo.save(history);
			
			reader.close();
			
			return new FileUploadResponse("File Uploaded successfully", true, HttpStatus.OK.value(), filename);
		}
	}


	@Override
	public CancelSupplierCNResponse cancelsupplierCN(SupplierCNCancelRequest request) {

		log.info("cancelsupplierCN  Service Started");
		CancelSupplierCNResponse response = new CancelSupplierCNResponse();
		Batch batch = batchRepo.findByRefNoAndBatchType(request.getRefNo(), request.getBatchType()).orElse(null);
		if(batch == null) {
			response.setMessage("Batch not found");
		} else {
			boolean remove = true;
			for(Inbound inbound : batch.getInbounds()) {
				if(!inbound.getStatus().equals(InboundStatusType.STARTED)) {
					remove = false;
					break;
				}
			}

			if(remove) {				
				Set<Integer> inbounds = batch.getInbounds().stream().map(e -> e.getId()).collect(Collectors.toSet());
				inboundsRepo.deleteInbounds(inbounds);
				batchRepo.deleteBatches(Collections.singleton(batch.getId()));
				response.setMessage("SupplierCN cancelled successfully with " + batch.getInbounds().size() + " inbounds");
			} else {
				response.setMessage("SupplierCN cannot be cancelled as some/all items has been assigned to bin");
			}
			response.setStatus(remove);
			response.setCode(HttpStatus.OK.value());
		}
		log.info("cancelsupplierCN Service Ended");
		return response;
	
	}
}

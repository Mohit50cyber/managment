package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.moglix.wms.api.request.GetInboundByIdRequest;
import com.moglix.wms.api.request.GetInboundRequest;
import com.moglix.wms.api.request.InventoriseInboundRequest;
import com.moglix.wms.api.request.UpdateInboundTaxRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse.Inventory;
import com.moglix.wms.api.response.GetInboundByIdResponse;
import com.moglix.wms.api.response.GetInboundByPoItemIdResponse;
import com.moglix.wms.api.response.GetInboundResponse;
import com.moglix.wms.api.response.InventoriseInboundResponse;
import com.moglix.wms.api.response.LotInfoResponse;
import com.moglix.wms.api.response.LotInfoResponse.InboundLotInfo;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.constants.StorageLocationType;
import com.moglix.wms.dto.InboundDTO;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundLot;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.producer.FifoProducer;
import com.moglix.wms.queueModel.InventoryAllocationRequest;
import com.moglix.wms.repository.BatchRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.WarehouseRepository;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.specifications.InboundSpecifications;
import com.moglix.wms.util.JsonUtil;
import com.moglix.wms.util.PaginationUtil;

/**
 * @author pankaj on 30/4/19
 */
@Service("inboundService")
public class InboundServiceImpl implements IInboundService {

	Logger log = LogManager.getLogger(InboundServiceImpl.class);
    @Autowired
    private InboundRepository repository;
    
    @Autowired
    private BatchRepository batchRepo;

    @Autowired
	@Qualifier("inventoryService")
	private IInventoryService inventoryService;

    @Autowired 
    private WarehouseRepository warehouseRepo;
    
    @Autowired
    private IProductInventoryService productInventoryService;
    
    @Autowired
    private ProductsRepository prodRepo;

	@Autowired
	private FifoProducer producer;

    @Value("${queue.allocation.new}")
    private String NEW_ALLOCATION_QUEUE;
    

    @Override
    public Inbound upsert(Inbound inbound) {
        repository.save(inbound);
        return inbound;
    }

    @Override
    public Inbound getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(Inbound inbound) {
        repository.delete(inbound);
    }

    @Override
    public void deleteMultiple(List<Inbound> inbounds) {
        repository.deleteAll(inbounds);
    }

    @Override
    public List<Inbound> getByBatchId(Integer batchId) {
        return repository.findByBatchId(batchId);
    }

	@Override
	public Page<Inbound> findAllByOrderByModifiedDesc(Pageable page) {
		return repository.findAllByOrderByModifiedDesc(page);
	}

	@Override
	public Page<Inbound> findByWarehouseId(Integer warehouseId, Pageable page) {
		return repository.findByWarehouseId(warehouseId, page);
	}

	@Override
	@Transactional
	public GetInboundResponse getInboundList(GetInboundRequest request, Pageable page) {
		if (request.getWarehouseId() != null) {
			log.info("Gettin inbound List for IDs for warehouseId: " + request.getWarehouseId());

			Page<Inbound> inbounds = findByWarehouseId(request.getWarehouseId(), page);
			if(inbounds.getContent().isEmpty()) {	
				log.info("No inbounds found for warehouseId: " + request.getWarehouseId());
				return new GetInboundResponse("No Inbounds found for warehouse id: " + request.getWarehouseId(), true, HttpStatus.OK.value());
			}else {
				GetInboundResponse response = (GetInboundResponse) PaginationUtil.setPaginationParams(inbounds, new GetInboundResponse("Successfully retreived inbounds", true, HttpStatus.OK.value()));
				for(Inbound inbound : inbounds.getContent()) {
					response.getInbounds().add(new InboundDTO(inbound));
				}
				return response;
			} 
		} else {
			log.info("Gettin inbound List for IDs for all warehouses");
			Page<Inbound> inbounds = findAllByOrderByModifiedDesc(page);
			GetInboundResponse response = (GetInboundResponse) PaginationUtil.setPaginationParams(inbounds, new GetInboundResponse("Successfully retreived inbounds", true, HttpStatus.OK.value()));
			for(Inbound inbound : inbounds.getContent()) {
				response.getInbounds().add(new InboundDTO(inbound));
			}
			return response;
		}
	}
	
	@Override
	@Transactional
	public GetInboundResponse searchInbounds(GetInboundRequest request, Pageable page) {

		log.info("Gettin inbound List for IDs for warehouseId: " + request.getWarehouseId());

		Specification<Inbound> inboundSpec = InboundSpecifications.hasWarehouse(request.getWarehouseId());

		if(request.getStatus() != null) {
			inboundSpec = inboundSpec.and(InboundSpecifications.hasStatus(request.getStatus()));
		}
		
		if(request.getAction() != null) {
			inboundSpec = inboundSpec.and(InboundSpecifications.hasAction(request.getAction()));
		}
				
		if(request.getSearchKey() != null) {
			inboundSpec = inboundSpec.and(InboundSpecifications.containsTextInName(String.valueOf(request.getSearchKey())));
		}
		
		Page<Inbound> inbounds = repository.findAll(inboundSpec, page);
		
		if (inbounds.getContent().isEmpty()) {
			log.info("No inbounds found for warehouseId: " + request.getWarehouseId());
			return new GetInboundResponse("No Inbounds found for warehouse id: " + request.getWarehouseId(), true,
					HttpStatus.OK.value());
		} else {
			GetInboundResponse response = (GetInboundResponse) PaginationUtil.setPaginationParams(inbounds,
					new GetInboundResponse("Successfully retreived inbounds", true, HttpStatus.OK.value()));
			for (Inbound inbound : inbounds.getContent()) {
				response.getInbounds().add(new InboundDTO(inbound));
			}
			return response;
		}
	}

	@Override
	public void saveAll(Iterable<Inbound> inbounds) {
		repository.saveAll(inbounds);		
	}

	@Override
	@Transactional
	public GetInboundByIdResponse getInboundById(GetInboundByIdRequest request) {
		log.info("Getting detail for inbound Id: " + request.getId());
		InboundDTO iDTO = null;
		GetInboundByIdResponse response = null;
		Inbound inbound = repository.findById(request.getId()).orElse(null);
		if (inbound != null) {
			log.info("Inbound found for id: " + request.getId());
			iDTO = new InboundDTO(inbound);
			response = new GetInboundByIdResponse("Inbound Found for id: " + request.getId(), true,
					HttpStatus.OK.value());
		}else {
			log.info("Inbound not found for id: " + request.getId());
			response =  new GetInboundByIdResponse("Inbound Not Found for id: " + request.getId(), true, HttpStatus.OK.value());
		}
		response.setInbound(iDTO);		
		return response;
	}

	@Override
	@Transactional
	public InventoriseInboundResponse inventoriseInbound(InventoriseInboundRequest request) {
		log.info("Add inventory for inbound service started");
		InventoriseInboundResponse response = new InventoriseInboundResponse();
		Inbound inbound = getById(request.getInboundId());
				
		if (inbound != null) {
			Warehouse warehouse = warehouseRepo.findById(inbound.getWarehouseId()).orElse(null);

			log.info("Inbound found for id");
			inbound.setInventorize(true);
			
			for(InboundStorage storage : inbound.getInboundStorages()) {
				storage.setConfirmed(true);
			}
			
			//update inventory
			Double prevQuantity = 0.0d;
			Double currQuantity;
			ProductInventory prevInventory = productInventoryService.getByWarehouseIdAndProductId(inbound.getWarehouseId(), inbound.getProduct().getId());
			
			if(prevInventory != null) {
				prevQuantity = prevInventory.getCurrentQuantity();
			}
			
			inventoryService.addInventory(inbound.getWarehouseId(), inbound.getProduct().getId(),inbound.getPurchasePrice(), inbound.getInventorisableQuantity());

			ProductInventory currInventory = productInventoryService.getByWarehouseIdAndProductId(inbound.getWarehouseId(), inbound.getProduct().getId());
			
			currQuantity = currInventory.getCurrentQuantity();			
			
			inventoryService.saveInventoryHistory(warehouse ,inbound.getProduct().getProductMsn(), InventoryTransactionType.WAREHOUSE_RETURN, InventoryMovementType.INVENTORY_IN, inbound.getBatch().getRefNo() , prevQuantity, currQuantity);
			//set inventorisable quantity to 0			
			inbound.setInventorisableQuantity(0.0d);
			//push product to inventory allocation queue
			InventoryAllocationRequest input = new InventoryAllocationRequest(null, inbound.getProduct().getId());
			producer.sendMessage(NEW_ALLOCATION_QUEUE, JsonUtil.toJson(input));

			repository.save(inbound);
			response.setMessage("Inbound inventory added");
			response.setStatus(true);
		} else {
			log.info("Inbound not found for id: " + request.getInboundId());
			response.setMessage("Inbound not found for id: " + request.getInboundId());
		}
		log.info("Add inventory for inbound service ended");
		return response;
	}

	@Override
	@Transactional
	public GetFreeInventoryResponse getFreeInventory(String refNo, Integer supplierPoId) {
		
		Batch batch = batchRepo.findByRefNoAndBatchType(refNo, BatchType.INBOUND).orElse(null);
		
		List<Batch> childBatches = batchRepo.findByParentRefNo(refNo);
		
		if(batch == null){
			return new GetFreeInventoryResponse("Batch Not Found", true, 200); 
		}
		

		Set<Inbound>dnInbounds = batch.getInbounds();
		
		if(!CollectionUtils.isEmpty(childBatches)) {
			for(Batch childBatch: childBatches) {
				dnInbounds.addAll(childBatch.getInbounds());
			}
		}
		
		GetFreeInventoryResponse response = new GetFreeInventoryResponse("Successfully found free inventory for refNo" + refNo + " and poId " + supplierPoId, true, 200);
		
		Map<Integer, Double> availableQuantity = dnInbounds.stream()
				.filter(e -> Integer.compare(supplierPoId, e.getSupplierPoId()) == 0)
				.flatMap(e -> e.getInboundStorages().stream())
				.filter(e -> e.getStorageLocation().getType().equals(StorageLocationType.GOOD) && e.getStorageLocation().isActive())
				.collect(Collectors.groupingBy(e -> e.getInbound().getSupplierPoItemId(),
						Collectors.summingDouble(e -> e.getAvailableQuantity())));
		
		for(Map.Entry<Integer, Double>entry : availableQuantity.entrySet()) {
			Inventory inventory = new Inventory();
			inventory.setRefNo(refNo);
			inventory.setSupplierPoId(supplierPoId);
			inventory.setSupplierPoItemId(entry.getKey());
			inventory.setFreeQuantity(entry.getValue());
			response.getInventory().add(inventory);
		}
		return response;
	}

	@Override
	public GetInboundResponse getInboundByProducMsn(GetInboundRequest request, Pageable page) {
		Product product = prodRepo.getUniqueByProductMsn(request.getSearchKey());
		if(product == null) {
			return new GetInboundResponse("No product found for MSN: " + request.getSearchKey(), false, HttpStatus.OK.value());
		}
		else {
			Page<Inbound> inbounds = repository.findByWarehouseIdAndProduct(request.getWarehouseId(), product, page);
			if (inbounds.getContent().isEmpty()) {
				log.info("No inbounds found for warehouseId: " + request.getWarehouseId());
				return new GetInboundResponse("No Inbounds found for warehouse id: " + request.getWarehouseId(), true,
						HttpStatus.OK.value());
			} else {
				GetInboundResponse response = (GetInboundResponse) PaginationUtil.setPaginationParams(inbounds,
						new GetInboundResponse("Successfully retreived inbounds", true, HttpStatus.OK.value()));
				for (Inbound inbound : inbounds.getContent()) {
					response.getInbounds().add(new InboundDTO(inbound));
				}
				return response;
			}
		}		
	}
	
	@Override
	@Transactional
	public BaseResponse updateInboundTransferPrice(UpdateInboundRequest updateRequest) {
		
		Inbound updateInbound = repository.findById(updateRequest.getInboundId()).orElse(null);
		
		if(updateInbound == null) {
			log.info("No Inbound found for id: " + updateRequest.getInboundId());
			return new BaseResponse("Cannot update because no inbound found", false, HttpStatus.OK.value());
		}
		
		if(updateRequest.getPurchasePrice() != null) {
			updateInbound.setPurchasePrice(updateRequest.getPurchasePrice());
		}
		
		if(updateRequest.getTax() != null) {
			updateInbound.setTax(updateRequest.getTax());
		}
		
		log.info("Saving inbound to database");
		repository.save(updateInbound);
		
		log.info("Inbound updated Successfully: " + updateInbound.getId());
		
		return new BaseResponse("Inbound updated successfully: " + updateInbound.getId(), true, HttpStatus.OK.value());
		
	}

	@Override
	@Transactional
	public BaseResponse updateInboundTax(UpdateInboundTaxRequest request) {
		List<Inbound> updateInbounds = repository.findByIdIn(request.getInboundIds());
		if(updateInbounds.isEmpty()) {
			log.info("No Inbound found for given ids: " +request.getInboundIds());

		}
		for(Inbound updateInbound : updateInbounds) {
			log.info("Saving inbound to database for inbound :{}",updateInbound);
			updateInbound.setTax(request.getTax());
			repository.save(updateInbound);
			log.info("Inbound updated Successfully: " + updateInbound.getId());
		}

		return new BaseResponse("Inbound updated successfully: " + request.getInboundIds(), true, HttpStatus.OK.value());
	}

	@Override
	public GetInboundByPoItemIdResponse getInBoundByPoItemId(Integer poItemId) {
		log.info("fetching Inbound  started for poItemId : " + poItemId);
		GetInboundByPoItemIdResponse getInboundByPoItemIdResponse = new GetInboundByPoItemIdResponse();
		List<Inbound> inboundList=repository.findBySupplierPoItemId(poItemId);
		List<Integer> inboundIdList = new ArrayList<>();
		if(!inboundList.isEmpty()) {
			for(Inbound inbound : inboundList) {
				inboundIdList.add(inbound.getId());
			}
			getInboundByPoItemIdResponse.setInboundIds(inboundIdList);
			getInboundByPoItemIdResponse.setCode(200);
			getInboundByPoItemIdResponse.setStatus(true);
			getInboundByPoItemIdResponse.setMessage("Inbound List by poItemId fetched successfully");
			
		}else {
			getInboundByPoItemIdResponse.setMessage("No Inbound found for this poItemId");
			getInboundByPoItemIdResponse.setCode(500);
			getInboundByPoItemIdResponse.setStatus(false);
			
		}
		log.info("fetching Inbound  ended for poItemId : " + poItemId);

		return getInboundByPoItemIdResponse;
	}

	@Override
	public LotInfoResponse getLotInfoById(GetInboundByIdRequest request) {
		log.info("fetching lot info started for inbound id :{}" + request.getId());
		Inbound inbound = repository.findById(request.getId()).orElse(null);
		LotInfoResponse lotInfoResponse = new LotInfoResponse();
		if(inbound != null) {
			List<InboundLotInfo> lotInfoList = new ArrayList<>();
			for(InboundLot inboundLot : inbound.getInboundLotAssoc()) {
				InboundLotInfo lotInfo = new InboundLotInfo();
				lotInfo.setLotNumber(inboundLot.getLot().getLotNumber());
				lotInfo.setLotId(inboundLot.getLot().getId());
				lotInfo.setQuantity(inboundLot.getQuantity());
				lotInfoList.add(lotInfo);
			}
			lotInfoResponse.setLotInfo(lotInfoList);
		}
		log.info("fetched lot info successfully for inbound id :{}" + request.getId());
		lotInfoResponse.setMessage("lot info fetched successfully");
		lotInfoResponse.setStatus(true);
		lotInfoResponse.setCode(200);
		return lotInfoResponse;
	}

	@Override
	public GetInboundResponse searchAppInbounds(@Valid GetInboundRequest request, Pageable page) {
		log.info("Gettin inbound List for IDs for warehouseId: " + request.getWarehouseId());

		Page<Inbound> inbounds=null;
				
		if(request.getSearchKey() != null) {
			inbounds = repository.findBySupplierPOIdandWarehouseId(request.getSearchKey()+'%',request.getWarehouseId(), page);
		}else {
			 inbounds = repository.findByWarehouseIdandStatus(request.getWarehouseId(), page);
			
		}
			
		if (inbounds.getContent().isEmpty()) {
			log.info("No inbounds found for warehouseId: " + request.getWarehouseId());
			return new GetInboundResponse("No Inbounds found for warehouse id: " + request.getWarehouseId(), true,
					HttpStatus.OK.value());
		} else {
			GetInboundResponse response = (GetInboundResponse) PaginationUtil.setPaginationParams(inbounds,
					new GetInboundResponse("Successfully retreived inbounds", true, HttpStatus.OK.value()));
			for (Inbound inbound : inbounds.getContent()) {
				response.getInbounds().add(new InboundDTO(inbound));
			}
			return response;
		}
	}

}

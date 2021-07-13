package com.moglix.wms.service.impl;


import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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

import com.moglix.wms.api.request.BlockInventoryRequest;
import com.moglix.wms.api.request.DeleteInventoryRequest;
import com.moglix.wms.api.request.DeleteWarehouseInventoryRequest;
import com.moglix.wms.api.request.GetDnDetailItemsRequest;
import com.moglix.wms.api.request.GetInventoryAvailabilityRequest;
import com.moglix.wms.api.request.GetInventoryStatsRequest;
import com.moglix.wms.api.request.SearchProductInventoryRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GetDnItemsResponse;
import com.moglix.wms.api.response.GetFreshAvailableQuantityResponse;
import com.moglix.wms.api.response.GetInventoryAvailabilityResponse;
import com.moglix.wms.api.response.GetInventoryStatsResponse;
import com.moglix.wms.api.response.SearchProductInventoryResponse;
import com.moglix.wms.constants.BlockInventoryAction;
import com.moglix.wms.constants.BlockedProductInventoryStatus;
import com.moglix.wms.dto.DnDetailItemDTO;
import com.moglix.wms.dto.FreshAvailableQuantityDetail;
import com.moglix.wms.dto.ProductInventoryData;
import com.moglix.wms.dto.ProductInventoryDetailsDTO;
import com.moglix.wms.dto.ProductInventoryDto;
import com.moglix.wms.dto.QuantityDetail;
import com.moglix.wms.entities.BlockedProductInventory;
import com.moglix.wms.entities.BlockedProductInventoryHistory;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.ProductInventoryConfig;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.projectionObjects.AverageAgeAndActiveMsn;
import com.moglix.wms.projectionObjects.InventoryStats;
import com.moglix.wms.repository.BlockedProductInventoryHistoryRepository;
import com.moglix.wms.repository.BlockedProductInventoryRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.ProductInventoryConfigRepository;
import com.moglix.wms.repository.ProductInventoryRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.ReturnPickupListRepository;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.task.ProductInventoryConfigImportTask;
import com.moglix.wms.util.NumberUtil;
import com.moglix.wms.util.PaginationUtil;

/**
 * @author pankaj on 6/5/19
 */
@Service("productInventoryService")
public class ProductInventoryServiceImpl implements IProductInventoryService {

    Logger logger = LogManager.getLogger(ProductInventoryServiceImpl.class);

    @Autowired
    private ProductInventoryRepository repository;
    
    @Autowired 
	@Qualifier("inventoryService")
    private IInventoryService inventoryService;
    
    @Autowired
    private InboundRepository inboundRepo;
    
    @Autowired
    private InboundStorageRepository isRepo;
    
    @Autowired
    private BlockedProductInventoryRepository blockedInventoryRepo;
    
    @Autowired
    private BlockedProductInventoryHistoryRepository blockedInventoryHistoryRepo;
    
    @Autowired
    private ProductsRepository productRepo;
    
    @Autowired
    private ProductInventoryConfigImportTask productInventoryConfigImportTask;

    @Autowired
	private ProductInventoryConfigRepository productInventoryConfigRepository;
    
    @Autowired
    private ReturnPickupListRepository returnPickupListRepo;

    @Override
    public void upsertAll(List<ProductInventory> list) {
        repository.saveAll(list);
    }

    @Override
    public ProductInventory upsert(ProductInventory obj) {
        return repository.save(obj);
    }

    @Override
    public ProductInventory getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ProductInventory> getAll() {
        return repository.findAll();
    }

    @Override
    public ProductInventory getByWarehouseIdAndProductId(Integer warehouseId, Integer productId) {
        return repository.findByWarehouseIdAndProductId(warehouseId, productId);
    }

    @Override
    public ProductInventory getByWarehouseIdAndProductProductMsn(Integer warehouseId, String productMsn) {
        return repository.findByWarehouseIdAndProductProductMsn(warehouseId, productMsn);
    }

    @Override
    public List<ProductInventory> getByWarehouseIdInAndProductProductMsnIn(List<Integer> warehouseId, List<String> productMsn) {
        return repository.findAllByWarehouseIdInAndProductProductMsnIn(warehouseId, productMsn);
    }

    @Override
    @Transactional
    public SearchProductInventoryResponse searchInventory(SearchProductInventoryRequest request, Integer countryId, Pageable page) {
       
    	logger.info("Search Inventory Service Started");
        SearchProductInventoryResponse response;
        Page<ProductInventory> productInventoryList = null;
        Page<ProductInventoryDetailsDTO> productInventoryPage = null;
        List<ProductInventoryDetailsDTO> productInventoryDtoList = new ArrayList<>();

        if(StringUtils.isNotBlank(request.getProductMsn()) && request.getWarehouseId() != null) {
        	if(request.getZoneId() != null) {
        		if(request.getBinId() != null) {
        			productInventoryPage = isRepo.getInventoryDetailsByProductWarehouseAndZoneAndBin(request.getProductMsn(), request.getWarehouseId(), request.getZoneId(), request.getBinId(),page);
        		}
        		else {
        			productInventoryPage  = isRepo.getInventoryDetailsByProductWarehouseAndZone(request.getProductMsn(), request.getWarehouseId(), request.getZoneId(), page);
        		}
        	}
        	else {
                productInventoryList = repository.findByWarehouseIdAndProductProductMsn(request.getWarehouseId(), request.getProductMsn(), page);
        	}
        }
        else if(StringUtils.isBlank(request.getProductMsn()) && request.getWarehouseId() != null) {
        	if(request.getZoneId() != null) {
        		if(request.getBinId() != null) {
        			productInventoryPage = isRepo.getInventoryDetailsByWarehouseAndZoneAndBin(request.getWarehouseId(), request.getZoneId(), request.getBinId(), page); 
        		}
        		else {
        			productInventoryPage = isRepo.getInventoryDetailsByWarehouseAndZone(request.getWarehouseId(), request.getZoneId(), page);
        		}
        	}
        	else {
                productInventoryList = repository.findByWarehouseIdOrderByModifiedDesc(request.getWarehouseId(), page);
        	}
        }
        else if(StringUtils.isNotBlank(request.getProductMsn()) && request.getWarehouseId() == null) {
            productInventoryList = repository.findByProductProductMsnAndWarehouseIsoNumberOrderByModifiedDesc(request.getProductMsn(), countryId, page);
        }
        else {
        	productInventoryList = repository.findAllByWarehouseIsoNumberOrderByModifiedDesc(countryId,page);
        }
       
        
		if (productInventoryList != null && !productInventoryList.getContent().isEmpty()) {
			for (ProductInventory obj : productInventoryList) {
				
				Double expiredQty = isRepo.getTotalExpiredInventoryByWarehouseIdAndProductId(obj.getWarehouse().getId(), obj.getProduct().getId());
				
				ProductInventoryDto productInventoryDTO = new ProductInventoryDto(obj);
                List<BlockedProductInventory> blockedInventory = blockedInventoryRepo.findByWarehouseIdAndProductMsn(obj.getWarehouse().getId(), obj.getProduct().getProductMsn());
                
                if(blockedInventory != null && blockedInventory.size()!=0) {
                	productInventoryDTO.setBlockedQuantity(blockedInventoryRepo.findtotalblockedquantity(obj.getWarehouse().getId(), obj.getProduct().getProductMsn()));
                }
                else {
                	productInventoryDTO.setBlockedQuantity(0.0d);
                }
                
                Double packedQuantity = 0d;
				if(request.getZoneId() != null) {
	        		if(request.getBinId() != null) {
	        					packedQuantity = isRepo.totalPackedQuantityByZoneAndBin(request.getProductMsn(), obj.getWarehouse().getId(),request.getZoneId(),request.getBinId());
	        				}else {
	        					packedQuantity = isRepo.totalPackedQuantityByZone(request.getProductMsn(), obj.getWarehouse().getId(),request.getZoneId());
	        			}
	        	}else {
	        		packedQuantity = isRepo.totalPackedQuantity(request.getProductMsn(), obj.getWarehouse().getId());
	        		}
				DecimalFormat df = new DecimalFormat("#.000"); 
				
				Double allocateQty=Math.max(productInventoryDTO.getAllocatedQuantity()-packedQuantity,0);
				
				allocateQty = Double.valueOf(df.format(allocateQty));
				
                productInventoryDTO.setAllocatedQuantity(allocateQty);
                
                productInventoryDTO.setPackedQuantity(packedQuantity);
                
                productInventoryDTO.setAvailableQuantity(NumberUtil.round4(expiredQty == null ? productInventoryDTO.getAvailableQuantity() : productInventoryDTO.getAvailableQuantity() - expiredQty));
                
                productInventoryDTO.setCurrentQuantity(NumberUtil.round4(expiredQty == null ? productInventoryDTO.getCurrentQuantity() : productInventoryDTO.getCurrentQuantity() - expiredQty));
                
                productInventoryDTO.setDNInitiatedQuantity(returnPickupListRepo.findDNInitiatedQuantity(request.getProductMsn(), obj.getWarehouse().getId() ));
                
                productInventoryDTO.setDNCreatedQuantity(returnPickupListRepo.findDNCreatedQuantity(request.getProductMsn(), obj.getWarehouse().getId() ));
                
				productInventoryDtoList.add(productInventoryDTO);
			}
			response = (SearchProductInventoryResponse) PaginationUtil.setPaginationParams(productInventoryList,
					new SearchProductInventoryResponse("Product Inventory found : " + productInventoryList.getTotalElements(), true,HttpStatus.OK.value()));
			response.setInventoryList(productInventoryDtoList);
		} 
		else if (productInventoryPage != null && !productInventoryPage.getContent().isEmpty()) {
			
			for(ProductInventoryDetailsDTO productInventoryDetailsDTO:productInventoryPage.getContent()) {
                
				Double expiredQty = isRepo.getTotalExpiredInventoryByWarehouseIdAndProductId(productInventoryDetailsDTO.getWarehouseId(), productInventoryDetailsDTO.getProductId());

				ProductInventoryDetailsDTO returnDTO = new ProductInventoryDto(productInventoryDetailsDTO);
				
				List<BlockedProductInventory> blockedInventory = blockedInventoryRepo.findByWarehouseIdAndProductMsn(productInventoryDetailsDTO.getWarehouseId(), productInventoryDetailsDTO.getProductMsn());
                
				if(blockedInventory != null && blockedInventory.size()!=0) {
                	returnDTO.setBlockedQuantity(blockedInventoryRepo.findtotalblockedquantity(request.getWarehouseId(), request.getProductMsn()));;
                }
				else {
                	returnDTO.setBlockedQuantity(0.0d);
                }
				
				Double packedQuantity = 0d;
				if(request.getZoneId() != null) {
	        		if(request.getBinId() != null) {
	        					packedQuantity = isRepo.totalPackedQuantityByZoneAndBin(request.getProductMsn(), productInventoryDetailsDTO.getWarehouseId(),request.getZoneId(),request.getBinId());
	        				}else {
	        					packedQuantity = isRepo.totalPackedQuantityByZone(request.getProductMsn(), productInventoryDetailsDTO.getWarehouseId(),request.getZoneId());
	        			}
	        	}else {
	        		packedQuantity = isRepo.totalPackedQuantity(request.getProductMsn(), productInventoryDetailsDTO.getWarehouseId());
	        		}
				
				returnDTO.setAllocatedQuantity(returnDTO.getAllocatedQuantity()-packedQuantity);
				
				returnDTO.setPackedQuantity(packedQuantity);
                
                returnDTO.setAvailableQuantity(NumberUtil.round4(expiredQty == null ? returnDTO.getAvailableQuantity() : returnDTO.getAvailableQuantity() - expiredQty));
                
                returnDTO.setCurrentQuantity(NumberUtil.round4(expiredQty == null ? returnDTO.getCurrentQuantity() : returnDTO.getCurrentQuantity() - expiredQty));
                
                returnDTO.setDNInitiatedQuantity(returnPickupListRepo.findDNInitiatedQuantity(request.getProductMsn(), productInventoryDetailsDTO.getWarehouseId() ));
                
                returnDTO.setDNCreatedQuantity(returnPickupListRepo.findDNCreatedQuantity(request.getProductMsn(), productInventoryDetailsDTO.getWarehouseId()));
                
                productInventoryDtoList.add(returnDTO);
			}
			response = (SearchProductInventoryResponse) PaginationUtil.setPaginationParams(productInventoryPage,
					new SearchProductInventoryResponse("Product Inventory found : " + productInventoryPage.getTotalElements(), true, HttpStatus.OK.value()));
			response.setInventoryList(productInventoryDtoList);
		}
		else {
			response = new SearchProductInventoryResponse("No Product Inventory found", true, HttpStatus.OK.value());
		}
        logger.info("Search Inventory Service Ended");
        return response;
    }

    @Override
    @Transactional
    public GetInventoryStatsResponse getInventoryStats(GetInventoryStatsRequest request) {
        logger.info("Get Inventory Stats Service Started");
        GetInventoryStatsResponse response = new GetInventoryStatsResponse();

        InventoryStats inventoryStats;
        AverageAgeAndActiveMsn averageAgeAndActiveMsn;
        if(request.getWarehouseId() != null) {
            inventoryStats = repository.getAggregatedDataByWarehouse(request.getWarehouseId());
            averageAgeAndActiveMsn = repository.getAverageAgeAndActiveMsnByWarehouse(request.getWarehouseId());
        } else {
            inventoryStats = repository.getAggregatedData();
            averageAgeAndActiveMsn = repository.getAverageAgeAndActiveMsn();
        }

        if(inventoryStats != null) {
            response.setTotalInventory(inventoryStats.getTotalInventory());
            response.setAvailableInventory(inventoryStats.getAvailableInventory());
            response.setAllocatedInventory(inventoryStats.getAllocatedInventory());
            response.setTotalPrice(inventoryStats.getTotalPrice());
            response.setMsnInInventory(inventoryStats.getMsnInInventory());
        }
        if(averageAgeAndActiveMsn != null) {
            response.setAverageAge((int)averageAgeAndActiveMsn.getAvgAge());
            response.setMsnActive(averageAgeAndActiveMsn.getMsnActive());
        }

        response.setStatus(true);
        response.setMessage("Inventory stats fetched");

        logger.info("Get Inventory Stats Service Ended");
        return response;
    }

	@Override
	@Transactional
	public BaseResponse deleteInventory(DeleteInventoryRequest request) {
		
		Product product = productRepo.getUniqueByProductMsn(request.getProductMsn());
		
		ProductInventory productInventory = repository.findByWarehouseIdAndProductId(request.getWarehouseId(), product.getId());
		if(productInventory == null || productInventory.getAvailableQuantity() == 0 || productInventory.getAvailableQuantity() < request.getQuantity()) {
			return new BaseResponse("Inventory Deletion is not possible since there is no free inventory. Use Inventory Transfer Module to free inventory", false, HttpStatus.OK.value());
		}
		
		List<InboundStorage> inboundStorages = isRepo.findByProductAndWarehouseForAvailable(request.getWarehouseId(), product.getId());
		
		double totalDeductQuantity = request.getQuantity();
		
		inboundStorages.sort(Comparator.comparing(InboundStorage::getCreated).reversed());
		for(InboundStorage storage: inboundStorages) {
			double deductQuantity = Math.min(storage.getAvailableQuantity(), totalDeductQuantity);
			
			storage.setAvailableQuantity(storage.getAvailableQuantity() - deductQuantity);
			
			storage.setQuantity(storage.getQuantity() - deductQuantity);
			
			totalDeductQuantity = totalDeductQuantity - deductQuantity;
			
			if(totalDeductQuantity == 0) {
				break;
			}
		}
		
		
		if(totalDeductQuantity == 0) {
			isRepo.saveAll(inboundStorages);
			inventoryService.deductAvailableInventory(request.getWarehouseId(), product.getId(), request.getQuantity());
		}else {
			throw new WMSException("Partial Deletion Not Possible for product: " + request.getProductMsn());
		}
		
		return new BaseResponse("Inventory Deleted for product: " + request.getProductMsn(), true, 200);
	}

    @Override
	@Transactional
	public BaseResponse deleteInventoryByWarehouse(@Valid DeleteWarehouseInventoryRequest request) {
		logger.info("deleting inventory of warehouseId :{}  started", request.getWarehouseId());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime nowStart = LocalDateTime.now();
		logger.info("starting time of deleteInventoryByWarehouse method  :{}", dtf.format(nowStart));
		List<ProductInventory> productInventoryList = repository.findByWarehouseIdAndAvailableQuantityGreaterThan(request.getWarehouseId(), 0.0d);
		if (productInventoryList.isEmpty()) {
			return new BaseResponse("Product Inventory  Not found for warehouseId :" + request.getWarehouseId(), false,
					HttpStatus.OK.value());
		}
		
		List<InboundStorage> inboundStorages = isRepo.findByWarehouseForAvailable(request.getWarehouseId());

		Map<Inbound, List<InboundStorage>> inboundInboundStorageMap = inboundStorages.stream().collect(Collectors
				.groupingBy(InboundStorage::getInbound, Collectors.mapping((InboundStorage p) -> p, Collectors.toList())));
		
		List<Inbound> inboundListForChange = new ArrayList<>();
		
		for(Entry<Inbound, List<InboundStorage>> entryInboundInboundStorage: inboundInboundStorageMap.entrySet()) {
			List<InboundStorage> inboundStorageList= entryInboundInboundStorage.getValue();
			Double changeQuantity =0.0;
			for(InboundStorage inboundStorageEntry : inboundStorageList) {
				changeQuantity += inboundStorageEntry.getAvailableQuantity();
			}
			Double inboundQuantity=entryInboundInboundStorage.getKey().getQuantity();
			Double inventorizeQuantity = entryInboundInboundStorage.getKey().getInventorisableQuantity();
			Double setInboundQuantity = inboundQuantity - changeQuantity;
			Double setinventorizeQuantity = inventorizeQuantity - changeQuantity;
			if(setInboundQuantity < 0.0) {
				setInboundQuantity = 0.0;
			}
			if(setinventorizeQuantity < 0.0) {
				setinventorizeQuantity =0.0;
			}
			entryInboundInboundStorage.getKey().setQuantity(setInboundQuantity);
			entryInboundInboundStorage.getKey().setInventorisableQuantity(setinventorizeQuantity);
			
			inboundListForChange.add(entryInboundInboundStorage.getKey());
		
		}
		inboundRepo.saveAll(inboundListForChange);

		if (!inboundStorages.isEmpty()) {
			for (InboundStorage storage : inboundStorages) {
				storage.setAvailableQuantity(0.0d);
			}
			isRepo.saveAll(inboundStorages);
		}
		for (ProductInventory productInventory : productInventoryList) {
			
				isRepo.saveAll(inboundStorages);
				inventoryService.deductAvailableInventoryWarehouseWise(request.getWarehouseId(),
						productInventory,productInventory.getProduct());

		}

		LocalDateTime nowEnd = LocalDateTime.now();
		long diff = ChronoUnit.NANOS.between(nowStart, nowEnd);

		logger.info("ending time of deleteInventoryByWarehouse method  :{}", dtf.format(nowEnd));
		logger.info("total response time of deleteInventoryByWarehouse method :{},milliseconds", diff);

		return new BaseResponse("Inventory Deleted for warehouseId  : " + request.getWarehouseId(), true, 200);
	}

	@Override
	public List<ProductInventory> findByAllocatedQuantityGreaterThan(double value) {
		// TODO Auto-generated method stub
		return repository.findByAllocatedQuantityGreaterThan(value);
	}

	@Override
	@Transactional
	public GetInventoryAvailabilityResponse getInventoryAvailability(@Valid GetInventoryAvailabilityRequest request) {
		
		List<ProductInventoryData> inventoryData = repository.getProductInventoryData(request.getWarehouseId(), request.getStorageLocationtype().toString(), request.getProductMSNList());
		
		GetInventoryAvailabilityResponse response = new GetInventoryAvailabilityResponse();
		
		if(CollectionUtils.isEmpty(inventoryData)) {
			response.setMessage("Didn't find any inventory for the requested MSNs at the warehouse");
			response.setStatus(false);
			response.setCode(HttpStatus.OK.value());
		}else {
			response.setMessage("Found " + inventoryData.size() + " inventory for the requested MSNs at the warehouse");
			response.setStatus(true);
			response.setCode(HttpStatus.OK.value());
			response.setData(inventoryData);
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public BaseResponse blockInventory(@Valid BlockInventoryRequest request) {	
		
		logger.info("Block Inventory service starts!!!" );
		
		BlockedProductInventory blockedInventory = blockedInventoryRepo.findByWarehouseIdAndProductMsnAndUniqueID(request.getWarehouseId(), request.getProductMsn(), request.getBulkInvoiceId());

		ProductInventory productInventory = repository.findByWarehouseIdAndProductProductMsn(request.getWarehouseId(), request.getProductMsn());
		
		BlockedProductInventoryHistory blockedInventoryHistory = new BlockedProductInventoryHistory();

		blockedInventoryHistory.setBlockedQuantity(request.getQuantity());
		blockedInventoryHistory.setBulkInvoiceId(request.getBulkInvoiceId());
		blockedInventoryHistory.setProductMsn(request.getProductMsn());
		blockedInventoryHistory.setWarehouseId(request.getWarehouseId());
		blockedInventoryHistory.setUniqueblockid(request.getBulkInvoiceId());
		
		if(request.getAction().equals(BlockInventoryAction.BLOCK)) {
			
			Double inboundStorageQty = 0.0d;
			inboundStorageQty = isRepo.totalAvailableQTY(request.getWarehouseId(), request.getProductMsn());
			
			if(productInventory == null){
				logger.info("Inventory for productMSN :: ["+ request.getProductMsn() + "]  is not present in warehouse :: [" + request.getWarehouseId() + "]");
				return new BaseResponse("Inventory for productMSN :: ["+ request.getProductMsn() + "]  is not present in warehouse :: [" + request.getWarehouseId() + "]", false, HttpStatus.BAD_REQUEST.value());
			}
			else if(!(productInventory.getAvailableQuantity().doubleValue()>= request.getQuantity().doubleValue())){
				logger.info("Inventory you are trying to block for productMSN :: ["+ request.getProductMsn() + "]  is greater then the inventory actual present in warehouse :: [" + request.getWarehouseId() + "] :: ProductInventoryAvailableQTY :: [" + productInventory.getAvailableQuantity() +"], OrderedQTY :: [" +request.getQuantity()+"].");
				return new BaseResponse("Inventory you are trying to block for productMSN :: ["+ request.getProductMsn() + "]  is greater then the inventory actual present in warehouse :: [" + request.getWarehouseId() + "].", false, HttpStatus.BAD_REQUEST.value());
			}
			else if(!productInventory.getAvailableQuantity().equals(inboundStorageQty)){
				logger.error("Inventory mismatch found for productMSN :: ["+ request.getProductMsn() + "] in warehouse :: [" + request.getWarehouseId() + "] :: ProductInventoryAvailableQTY :: [" + productInventory.getAvailableQuantity() +"], InboundStorageQTY :: [" +inboundStorageQty+"].");
				return new BaseResponse("Inventory mismatch found for productMSN :: ["+ request.getProductMsn() + "] in warehouse :: [" + request.getWarehouseId() + "].", false, HttpStatus.BAD_REQUEST.value());
			}
			else {
				
				if(blockedInventory == null) {
					//Create new blocked inventory quantity
					logger.info("Blocking Inventory starts for productMSN :: ["+ request.getProductMsn() + "] against BulkInvoiceId :: [" + request.getBulkInvoiceId() + "] in warehouse :: [" + request.getWarehouseId() + "].");
					blockedInventory = new BlockedProductInventory();
					blockedInventory.setBlockedQuantity(request.getQuantity());
					blockedInventory.setProductMsn(request.getProductMsn());
					blockedInventory.setWarehouseId(request.getWarehouseId());
					blockedInventory.setStatus(BlockedProductInventoryStatus.BLOCKED);
					blockedInventory.setUniqueblockid(request.getBulkInvoiceId());
						
					blockedInventoryHistory.setStatus(BlockedProductInventoryStatus.BLOCKED);
				}
			}
		}
		else if(request.getAction().equals(BlockInventoryAction.UNBLOCK)) {
		
			logger.info("Unblocking Updating blocked inventory for msn :: [" + blockedInventory.getProductMsn() + "] and quantity :: [" + blockedInventory.getBlockedQuantity() + "]");
			blockedInventory.setBlockedQuantity(blockedInventory.getBlockedQuantity() - request.getQuantity());
			blockedInventoryHistory.setStatus(BlockedProductInventoryStatus.UNBLOCKED);
			if(blockedInventory.getBlockedQuantity() == 0) {
				blockedInventory.setStatus(BlockedProductInventoryStatus.UNBLOCKED);
			}
		}
	
		blockedInventoryRepo.save(blockedInventory);
		blockedInventoryHistoryRepo.save(blockedInventoryHistory);
		//}

		logger.info("BlockInventory service end for request BulkInvoiceId :: [" + request.getBulkInvoiceId() + "]");
		
		return new BaseResponse("Inventory " + request.getAction().toString() + " Successfully", true, HttpStatus.OK.value());
	}
	
	private boolean isValidRequest(BlockInventoryRequest request, BlockedProductInventory blockedInventory, ProductInventory inventory) {
		
		logger.info("Checking request is valid for BulkInvoiceId :: [" + request.getBulkInvoiceId() + "]");
		
		double maximumQuantity      = 0.0d;
		double totalblockedquantity = 0.0d;
		
		ProductInventoryConfig inventoryConfig = productInventoryConfigRepository.findByProductMsnAndWarehouseId(inventory.getProduct().getProductMsn(), inventory.getWarehouse().getId());
        
		List<BlockedProductInventory> blockedinventorylist = blockedInventoryRepo.findByWarehouseIdAndProductMsn(request.getWarehouseId(), request.getProductMsn());
		
        if(blockedinventorylist == null || blockedinventorylist.isEmpty()) {
			logger.info("BlockedInventoryList found to be null or 0 for ProductMSN :: [" + request.getProductMsn() + "]");
			totalblockedquantity = 0;
		}
        else {
			if(blockedInventoryRepo.findtotalblockedquantity(request.getWarehouseId(), request.getProductMsn()) == null) {
				totalblockedquantity = 0;
			}
			else {
				logger.info("Total blocked quantity for productMSN :: [" + request.getProductMsn() + "] with total :: [" + blockedInventoryRepo.findtotalblockedquantity(request.getWarehouseId(), request.getProductMsn()) +"]");
				totalblockedquantity = blockedInventoryRepo.findtotalblockedquantity(request.getWarehouseId(), request.getProductMsn());	
			}
		}
        
        if(inventoryConfig != null) {
			maximumQuantity = inventoryConfig.getMaximumQuantity();
		}
		if ((blockedInventory == null && request.getAction().equals(BlockInventoryAction.UNBLOCK)) 
				|| inventory == null
				|| (request.getAction().equals(BlockInventoryAction.UNBLOCK)&& blockedInventory.getBlockedQuantity() < request.getQuantity())
				|| ((blockedInventory != null && request.getAction().equals(BlockInventoryAction.BLOCK)) && request.getQuantity() > (inventory.getAvailableQuantity() - totalblockedquantity - maximumQuantity))
				|| ((blockedInventory == null && request.getAction().equals(BlockInventoryAction.BLOCK)) && request.getQuantity() > inventory.getAvailableQuantity() - maximumQuantity)) {
			logger.info("Invalid request found for product msn :: ["+ request.getProductMsn() + "]");
			return false;
		}
		logger.info("Valid request for product msn :: [" + request.getProductMsn() + "]");
		return true;
	}

	@Override
	@Transactional
	public GetInventoryAvailabilityResponse getRealtimeInventoryAvailability(
			@Valid GetInventoryAvailabilityRequest request) {

		List<ProductInventoryData> inventoryData = repository.getRealtimeProductInventoryData(request.getWarehouseId(),
				request.getProductMSNList());

		GetInventoryAvailabilityResponse response = new GetInventoryAvailabilityResponse();

		if (CollectionUtils.isEmpty(inventoryData)) {
			response.setMessage("Didn't find any inventory for the requested MSNs at the warehouse");
			response.setStatus(false);
			response.setCode(HttpStatus.OK.value());
		} else {
			response.setMessage("Found " + inventoryData.size() + " inventory for the requested MSNs at the warehouse");
			response.setStatus(true);
			response.setCode(HttpStatus.OK.value());
			response.setData(inventoryData);
		}

		return response;
	}

	@Override
	public BaseResponse uploadProductInventoryConfig(String filename) {
		boolean flag = false;
		flag = productInventoryConfigImportTask.uploadProductInventoryConfigs(filename);
		
		if(flag) {
			return new BaseResponse("Product Inventory Configs imported Successfully", flag, HttpStatus.OK.value());
		}else {
			return new BaseResponse("Product Inventory Configs encountered an error", flag, HttpStatus.OK.value());
		}
	}

	@Override
	@Transactional
	public GetFreshAvailableQuantityResponse getFreshAvailableQuantity() {
		
		List<FreshAvailableQuantityDetail> quantityDetails = isRepo.getFreshAvailableQuantity();
		
		if(quantityDetails.isEmpty()) {
			return new GetFreshAvailableQuantityResponse("No Details Found", false, HttpStatus.OK.value());
		}
		
		Map<String, List<FreshAvailableQuantityDetail>> detailsMap = quantityDetails.stream().collect(Collectors.groupingBy(FreshAvailableQuantityDetail::getProductMsn));
		
		List<QuantityDetail> details = new ArrayList<>();
		
		
		for(Map.Entry<String, List<FreshAvailableQuantityDetail>> entry : detailsMap.entrySet()) {
			QuantityDetail detail = new QuantityDetail();
			detail.setProductMsn(entry.getKey());
			detail.setDetails(entry.getValue());
			details.add(detail);
		}
		
		GetFreshAvailableQuantityResponse response = new GetFreshAvailableQuantityResponse(
				"Found " + detailsMap.size() + " ProductMsns", true, HttpStatus.OK.value());

		response.setData(details);
		
		return response;
	}

	@Override
	public GetDnItemsResponse getDnInititatedItems(@Valid GetDnDetailItemsRequest request) {
		
		String productmsn=productRepo.getProductMsn(request.getProductId());
		
		List<DnDetailItemDTO> returnPickupList = returnPickupListRepo.findDNInitiatedItems(productmsn, request.getWarehouseId());
		
		if(returnPickupList.isEmpty()) {
			return new GetDnItemsResponse("No Return PickupList Found",false,200);
		}
		
		logger.info("Found ReturnPickupList with size[ "+returnPickupList.size()+" ]");
		
		GetDnItemsResponse response=new GetDnItemsResponse();
		
		response.setTotalQuantity(returnPickupListRepo.findDNInitiatedQuantity(productmsn, request.getWarehouseId()));
		
		response.setProductMSN(productmsn);
		
		response.setDnItemDetails(returnPickupList);
		
		Product product=productRepo.getUniqueByProductMsn(productmsn);
		
		response.setProductDesc(product.getProductName());
		
		response.setMessage("DN Details Generated");
		
		response.setStatus(true);
		
		logger.info("Returning DN Initiated List with Total Quantity [ "+response.getTotalQuantity()+" ]");
		
		return response;
	}

	@Override
	public GetDnItemsResponse getDnCreatedItems(@Valid GetDnDetailItemsRequest request) {
		
		String productmsn=productRepo.getProductMsn(request.getProductId());
		
		List<DnDetailItemDTO> returnPickupList = returnPickupListRepo.findDNCreatedItems(productmsn, request.getWarehouseId());
		
		if(returnPickupList.isEmpty()) {
			return new GetDnItemsResponse("No Return PickupList Found",false,200);
		}
		
		logger.info("Found ReturnPickupList with size[ "+returnPickupList.size()+" ]");
		
		GetDnItemsResponse response=new GetDnItemsResponse();
		
		response.setTotalQuantity(returnPickupListRepo.findDNCreatedQuantity(productmsn, request.getWarehouseId()));
		
		response.setProductMSN(productmsn);
		
		response.setDnItemDetails(returnPickupList);
		
		Product product=productRepo.getUniqueByProductMsn(productmsn);
		
		response.setProductDesc(product.getProductName());
		
		response.setMessage("DN Details Generated");
		
		response.setStatus(true);
		
		logger.info("Returning DN Initiated List with Total Quantity [ "+response.getTotalQuantity()+" ]");
		
		return response;
	}
	
}

package com.moglix.wms.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.moglix.wms.api.request.*;
import com.moglix.wms.constants.*;
import com.moglix.wms.entities.*;
import com.moglix.wms.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.moglix.ems.repository.EnterpriseRolePermRepository;
import com.moglix.wms.api.request.CreateInboundStorageRequest;
import com.moglix.wms.api.request.GetInventoryLocationsForAvailableQtyRequest;
import com.moglix.wms.api.request.GetInventoryLocationsForTotalQtyRequest;
import com.moglix.wms.api.request.StockTransferRequest;
import com.moglix.wms.api.request.StockTransferRequest.DestLocation;
import com.moglix.wms.api.request.StockTransferRequest.Location;
import com.moglix.wms.api.response.CreateInboundStorageResponse;
import com.moglix.wms.api.response.GetInventoryLocationsForAvailableQtyResponse;
import com.moglix.wms.api.response.GetInventoryLocationsForTotalQtyResponse;
import com.moglix.wms.api.response.StockTransferResponse;
import com.moglix.wms.api.response.StockTransferResponse.SourceDestinationDetail;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.BinTransferStatus;
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.constants.StorageLocationType;
import com.moglix.wms.dto.InventoryLocationDto;
import com.moglix.wms.entities.BinTransferDao;
import com.moglix.wms.entities.BinTransferHistory;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.PacketItem;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMappingItem;
import com.moglix.wms.entities.StorageLocation;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.exception.WMSSecurtyException;
import com.moglix.wms.producer.FifoProducer;
import com.moglix.wms.queueModel.InventoryAllocationRequest;
import com.moglix.wms.repository.BinTransferHistoryRepository;
import com.moglix.wms.repository.BinTransferRepository;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.ProductInventoryRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.SaleOrderAllocationRepository;
import com.moglix.wms.repository.SaleOrderSupplierPurchaseOrderMappingItemRepository;
import com.moglix.wms.repository.StorageLocationRepository;
import com.moglix.wms.repository.WarehouseRepository;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.service.IStorageLocationService;
import com.moglix.wms.util.JsonUtil;
import com.moglix.wms.util.NumberUtil;

@Service(value = "inboundStorageServiceImpl")
public class InboundStorageServiceImpl implements IInboundStorageService {

    private Logger logger = LogManager.getLogger(InboundStorageServiceImpl.class);

    private List<String> usersList = new ArrayList<>();

    @Autowired
    private BinTransferRepository binTransferRepository;

    @Autowired
    private StorageLocationRepository storageLocationRepository;

    @Autowired
    private BinTransferHistoryRepository binTransferHistoryRepository;

    @Autowired
    private InboundStorageRepository repository;

    @Autowired
    private IInboundService inboundService;

    @Autowired
    private IWarehouseService iWarehouseService;

    @Autowired
    private ProductsRepository prodRepo;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    SaleOrderAllocationRepository saleOrderAllocationRepo;

    @Autowired
    private StockTransferInboundService stockTransferInboundService;

    @Autowired
    private IStorageLocationService storageLocationService;

    @Autowired
    @Qualifier("inventoryService")
    private IInventoryService inventoryService;

    @Autowired
    private FifoProducer producer;

    @Autowired
    private IProductInventoryService productInventoryService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Value("${queue.allocation.new}")
    private String NEW_ALLOCATION_QUEUE;

    @Autowired
    private ISaleOrderAllocationService saleOrderAllocationService;

    @Autowired
    private EmsService emsService;

    @Autowired
    private EnterpriseRolePermRepository rolePermRepository;

    @Autowired
    private SaleOrderSupplierPurchaseOrderMappingItemRepository saleOrderSupplierPurchaseOrderMappingItemRepository;

    @Override
    public InboundStorage upsert(InboundStorage obj) {
        return repository.save(obj);
    }

    @Override
    public InboundStorage getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<InboundStorage> findAllByInboundIdIn(List<Integer> ids) {
        return repository.findAllByInboundIdIn(ids);
    }

    @Override
    public List<InboundStorage> findAllByProductId(Integer productId) {
        return repository.findByProductId(productId);
    }

    @Autowired
    private IProductService productService;



    @Override
    public List<InboundStorage> findAvailableByProduct(Integer productId) {
        return repository.findAvailableByProduct(productId);
    }

    @Autowired
    private ISaleOrderService saleOrderService;

    @Override
    @Transactional
    public CreateInboundStorageResponse create(CreateInboundStorageRequest request) {
        logger.info("Assign bin service started");
        CreateInboundStorageResponse response = new CreateInboundStorageResponse();

		Inbound inbound = inboundService.getById(request.getInboundId());
		if(inbound == null) {
			response.setMessage("Invalid Inbound Id: " + request.getInboundId());
		} else if(!inbound.getStatus().equals(InboundStatusType.STARTED)) {
			response.setMessage("Inbound status in invalid: " + inbound.getStatus().name());
		} else {
			Double totalQty = 0d;
			String errorMessage = "";
			boolean error = false;
			StorageLocation storageLocation;
			InboundStorage inboundStorage;
			List<InboundStorage> inboundStorages = new ArrayList<>();
			for(CreateInboundStorageRequest.LocationToQuantity item : request.getItems()) {
				storageLocation = storageLocationService.getById(item.getStorageLocationId());
				if(storageLocation == null || !storageLocation.getWarehouse().getId().equals(inbound.getWarehouseId()) || (!storageLocation.isActive())) {
					errorMessage = "Storage Location is invalid for id: " + item.getStorageLocationId();
					error = true;
					break;
				} else {
					inboundStorage = new InboundStorage();
					inboundStorage.setQuantity(NumberUtil.round4(item.getQuantity()));
					inboundStorage.setAvailableQuantity(NumberUtil.round4(item.getQuantity()));
					inboundStorage.setConfirmed(inbound.getInventorize());
					inboundStorage.setInbound(inbound);
					inboundStorage.setStorageLocation(storageLocation);
					inboundStorage.setProduct(inbound.getProduct());
					inboundStorage.setLotId(item.getLotId());
					inboundStorage.setLotNumber(item.getLotNumber());
					inboundStorage.setExpiryDate(item.getExpDate());
					inboundStorages.add(inboundStorage);

					totalQty += item.getQuantity();
				}
			}

			if(error) {
				response.setMessage(errorMessage);
			} else if(NumberUtil.round4(totalQty) != NumberUtil.round4(inbound.getQuantity())) {
				response.setMessage("All items are not assigned to bin");
			} else {
				repository.saveAll(inboundStorages);

				inbound.setStatus(InboundStatusType.BIN_ASSIGNED);
				inbound.setBinAssignedBy(request.getBinAssignedBy());
				
				inboundService.upsert(inbound);

				if(inbound.getInventorize() == true) {
					
					Double prevQuantity = 0.0d;
					Double currQuantity;
					ProductInventory prevInventory = productInventoryService.getByWarehouseIdAndProductId(inbound.getWarehouseId(), inbound.getProduct().getId());
					
					if(prevInventory != null) {
						prevQuantity = prevInventory.getCurrentQuantity();
					}
					
					//update inventory
					inventoryService.addInventory(inbound.getWarehouseId(), inbound.getProduct().getId(), inbound.getPurchasePrice(), inbound.getQuantity());
					
					ProductInventory currInventory = productInventoryService.getByWarehouseIdAndProductId(inbound.getWarehouseId(), inbound.getProduct().getId());
					
					currQuantity = currInventory.getCurrentQuantity();
					
					Warehouse warehouse = warehouseRepository.findById(inbound.getWarehouseId()).orElse(null);
					 //update inventory history table
					if(inbound.getBatch().getBatchType().name().equals(BatchType.INBOUND.name())) {
				        inventoryService.saveInventoryHistory(warehouse, inbound.getProduct().getProductMsn(), InventoryTransactionType.MRN_DONE, InventoryMovementType.INVENTORY_IN, inbound.getBatch().getRefNo() , prevQuantity, currQuantity);
					}
			      
					// new change start
					if(inbound.getType().name().equals(InboundType.NEW.name())) {
						
						logger.info("InboundType is New");
						
						List <SaleOrderSupplierPurchaseOrderMappingItem> saleOrderSupplierPurchaseOrderMappingItemList = saleOrderSupplierPurchaseOrderMappingItemRepository.findByBatchIdAndProductIDAndSupplierPoIdAndSupplierPoItemIdAndPickUpWarehouseIdAndStatus(inbound.getBatch().getId(), inbound.getProduct().getId(), inbound.getSupplierPoId(), inbound.getSupplierPoItemId(), inbound.getWarehouseId(), SaleOrderSupplierPurchaseOrderMappingStatus.INBOUNDED);
						
						if(saleOrderSupplierPurchaseOrderMappingItemList != null && !saleOrderSupplierPurchaseOrderMappingItemList.isEmpty()) {
							
							logger.info("SaleOrderSupplierPurchaseOrderMappingItemList size :: " + saleOrderSupplierPurchaseOrderMappingItemList.size());
							
							for(SaleOrderSupplierPurchaseOrderMappingItem dataObj : saleOrderSupplierPurchaseOrderMappingItemList) {
								
								logger.trace("Inside SaleOrderSupplierPurchaseOrderMappingItem Loop");
								
								dataObj.setInboundId(request.getInboundId());

								if(!dataObj.getStatus().equals(SaleOrderSupplierPurchaseOrderMappingStatus.CANCELLED)) {

									dataObj.setStatus(SaleOrderSupplierPurchaseOrderMappingStatus.BIN_ASSIGNED);
								}
								saleOrderSupplierPurchaseOrderMappingItemRepository.save(dataObj);
								logger.info("Bin Assigned against SaleOrder :: [" + dataObj.getSaleOrderId() +"]");
							}
						}
					}
					// end

                    // Run allocation logic only if its a normal inbound or belongs to Warehouse-to-warehouse type.
                    StockTransferInbound stiByInbound = stockTransferInboundService.findByInboundId(inbound);
                    if (stiByInbound == null || stiByInbound.getStockTransferNote().getStnType() == StockTransferNoteType.WAREHOUSE_TO_WAREHOUSE) {
                        InventoryAllocationRequest input = new InventoryAllocationRequest(null, inbound.getProduct().getId());
                        producer.sendMessage(NEW_ALLOCATION_QUEUE, JsonUtil.toJson(input));
                    } else if (stiByInbound.getStockTransferNote().getStnType() == StockTransferNoteType.CUSTOMER_ORDER) {
                        // run logic by allocation for STN
                        allocationOfProductForSTNSaleOrder(stiByInbound, inboundStorages);
                    }
				}

				response.setMessage("Bin Assigned");
				response.setStatus(true);
			}
		}
		logger.info("Assign bin service ended");
		return response;
	}

    private Double allocateAndUpdateInboundStorage(SaleOrder saleOrder, InboundStorage inboundStorage) {
        SaleOrderAllocation allocation;
        SaleOrderAllocationHistory allocationHistory;
        Double inboundStorageAvailableQty;
//        Double currentAllocation=soCurrentAllocation;
//	    logger.trace("Inside inbound storages Loop" + " : " + uuid);
        logger.trace("Inside inbound storages Loop" + " : ");
        logger.info("InboundStorage id for saleorder [ " + saleOrder.getId() + "]" + " :: InboundStorage id :: " + inboundStorage.getId());

        inboundStorageAvailableQty = inboundStorage.getAvailableQuantity();
        logger.debug("Quantity available in inboundStorage: " + inboundStorage.getId() + " : [" + inboundStorageAvailableQty + "] : ");
//        logger.debug("Quantity available in inboundStorage: " + inboundStorage.getId() + " : [" + inboundStorageAvailableQty + "] : " + uuid);

        allocation = new SaleOrderAllocation();
        allocation.setInboundStorage(inboundStorage);
        allocation.setSaleOrder(saleOrder);

        // check inbounded quantity for this saleorder

//        logger.debug("setting Available and allocated quantity to: " + (inboundStorageAvailableQty >= currentAllocation ? currentAllocation : inboundStorageAvailableQty) + " : " + uuid);
        allocation.setAllocatedQuantity(inboundStorageAvailableQty);
        allocation.setAvailableQuantity(allocation.getAllocatedQuantity());
        saleOrderService.upsertAllocation(allocation);
        logger.error("allocation {}",allocation.getId());
        allocationHistory = new SaleOrderAllocationHistory();
        allocationHistory.setAction("Allocated");
        allocationHistory.setQuantity(allocation.getAllocatedQuantity());
        allocationHistory.setSaleOrder(saleOrder);
        saleOrderService.upsertAllocationHistory(allocationHistory);

//        logger.debug("Setting new storage available quantity: " + NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);

//        if(NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) < 0) {
//            inboundStorage.setAvailableQuantity(0.0);
//            logger.error("Available quantity should not be negative in Inbound Storage :: ProductMsn ::[" +  inboundStorage.getProduct().getProductMsn() +"] :: WarehouseId :: ["+ saleOrder.getWarehouse().getId() +"]");
//        }
//        else {
//            inboundStorage.setAvailableQuantity(NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()));
//        }
        inboundStorage.setAvailableQuantity(0.0);
//        logger.debug("Setting new storage allocated quantity: " + NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);
        logger.debug("Setting new storage allocated quantity: " + NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()));
        inboundStorage.setAllocatedQuantity(NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()));

//        logger.trace("Updating inbound storage: " + inboundStorage.getId() + " : " + uuid);
        repository.save(inboundStorage);
        logger.error("inbound inboundStorage id:{}",inboundStorage.getId());
        return allocation.getAllocatedQuantity();
//        currentAllocation = NumberUtil.round4(currentAllocation - allocation.getAllocatedQuantity());
//        if (currentAllocation == 0) {
//            logger.trace("Allocation Completed for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
//            break;
//        }


    }

    private void allocationOfProductForSTNSaleOrder(StockTransferInbound stInbound, List<InboundStorage> inboundStorages) {
        StockTransferNote stnbyInboundId = stInbound.getStockTransferNote();
        if (stnbyInboundId != null && stnbyInboundId.getStnType() == StockTransferNoteType.CUSTOMER_ORDER) {
            SaleOrder so = saleOrderService.getByItemRef(stnbyInboundId.getItemRef());
            Inbound inbound = stInbound.getInbound();
            // TODO: Logic for allocation
            ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(inbound.getWarehouseId(), so.getProduct().getId());
            Double currentSoAllocation = 0.0;
            for (InboundStorage storage :
                    inboundStorages) {
                currentSoAllocation += allocateAndUpdateInboundStorage(so, storage);
            }
            so.setAllocatedQuantity(so.getAllocatedQuantity() + currentSoAllocation);

            if (so.getOrderedQuantity().equals(so.getAllocatedQuantity())) {
                so.setStnAssoication(false);
                so.setStatus(SaleOrderStatus.OPEN);
            }

            saleOrderService.upsert(so);
            // notify it to ems
            EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(so.getEmsOrderItemId(), so.getAllocatedQuantity(), "WMS");
            emsService.updateEmsPortal(emsRequest);

            productInventory.setAllocatedQuantity(NumberUtil.round4(productInventory.getAllocatedQuantity() + currentSoAllocation));

            logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Available Quantity: " + NumberUtil.round4(productInventory.getAvailableQuantity() - currentSoAllocation));
//            logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Available Quantity: " + NumberUtil.round4(productInventory.getAvailableQuantity() - currentSoAllocation) + " : " + uuid);
            if (NumberUtil.round4(productInventory.getAvailableQuantity() - currentSoAllocation) < 0) {
                productInventory.setAvailableQuantity(0.0);
                logger.error("Available quantity should not be negative in ProductInventory :: ProductMsn ::[" + productInventory.getProduct().getProductMsn() + "] :: WarehouseId :: [" + productInventory.getWarehouse().getId() + "]");
            } else {
                productInventory.setAvailableQuantity(NumberUtil.round4(productInventory.getAvailableQuantity() - currentSoAllocation));
            }

//            logger.trace("Updating product inventory with inventoryId: " + productInventory.getId() + " : " + uuid);
            logger.trace("Updating product inventory with inventoryId: " + productInventory.getId());
            productInventoryService.upsert(productInventory);

            Product product = productInventory.getProduct();
//            logger.debug("Product Id: " + product.getId() + " Updated Allocated Quantity: " + NumberUtil.round4(product.getAllocatedQuantity() + currentSoAllocation) + " : " + uuid);
            logger.debug("Product Id: " + product.getId() + " Updated Allocated Quantity: " + NumberUtil.round4(product.getAllocatedQuantity() + currentSoAllocation));
            product.setAllocatedQuantity(NumberUtil.round4(product.getAllocatedQuantity() + currentSoAllocation));
            logger.debug("Product Id: " + product.getId() + " Updated Available Quantity: " + NumberUtil.round4(product.getAvailableQuantity() - currentSoAllocation));
//            logger.debug("Product Id: " + product.getId() + " Updated Available Quantity: " + NumberUtil.round4(product.getAvailableQuantity() - currentSoAllocation) + " : " + uuid);
            if (NumberUtil.round4(product.getAvailableQuantity() - currentSoAllocation) < 0) {
                product.setAvailableQuantity(0.0);
                logger.error("Available quantity should not be negative in Product Table :: ProductMsn ::[" + product.getProductMsn() + "] :: WarehouseId :: [" + inbound.getWarehouseId() + "]");
            } else {
                product.setAvailableQuantity(NumberUtil.round4(product.getAvailableQuantity() - currentSoAllocation));
            }

            logger.trace("Updating Product Entry for product: " + product.getId());
//            logger.trace("Updating Product Entry for product: " + product.getId() + " : " + uuid);
            productService.upsert(product);

        }
    }
    @Transactional
    @Override
    public void deductInboundStorage(StockTransferNote stockTransferNote) {

        Warehouse sourceWarehouse =
                iWarehouseService.getById(stockTransferNote.getWarehouseFrom());
        Warehouse targetWarehouse =
                iWarehouseService.getById(stockTransferNote.getWarehouseTo());
        List<StockTransfer> stItems = stockTransferNote.getStockTransferList();
        List<InboundStorage> inboundStorages = new ArrayList<>();
        Map<String, Double> prevProductQuantityMap = new HashMap<>();
        SaleOrder so = saleOrderService.getByItemRef(stockTransferNote.getItemRef());
        if(!so.getWarehouse().getId().equals(targetWarehouse.getId())){
            throw new WMSException("Please change the fullfillment warehouse to [" + targetWarehouse.getName() +"]");
        } else if(so.getPackedQuantity() != 0) {
            throw new WMSException("Item_ref is not eligible for Stock " +
                    "transfer  allocated Quantity [" + so.getAllocatedQuantity() + "] , Packed Quantity [" + so.getPackedQuantity() + "] ,Ordered Quantity ["
                    + so.getOrderedQuantity() + "]");
        }

        Map<String, Double> currentProductQuantityMap = new HashMap<>();
        for (StockTransfer stItem : stItems) {
            InboundStorage storage = stItem.getInboundStorage();
            List<SaleOrderAllocation> saleOrderAllocations =
                    saleOrderAllocationService.getSaleOrderAllocationByInboundStorageAndSaleOrder(
                            storage,
                            so
                    );

            if(saleOrderAllocations.isEmpty() || saleOrderAllocations.stream().anyMatch(soa-> soa.getStatus().equals(SaleOrderAllocationStatus.CANCELLED))) {
                throw new WMSException("Allocation already cancelled  for inbound storage id["+storage.getId()+"]");
            }
            storage.setQuantity(storage.getQuantity() - stItem.getQuantity());
            storage.setAllocatedQuantity(storage.getAllocatedQuantity() - stItem.getQuantity());
            inboundStorages.add(storage);
            String productMsn = stItem.getProduct().getProductMsn();
            //TODO: NEED to check
            prevProductQuantityMap.compute(productMsn,
                    (k, v) -> v == null ? productInventoryService
                            .getByWarehouseIdAndProductId(sourceWarehouse.getId(), stItem.getProduct().getId())
                            .getCurrentQuantity() : v + productInventoryService
                            .getByWarehouseIdAndProductId(sourceWarehouse.getId(), stItem.getProduct().getId())
                            .getCurrentQuantity());
            currentProductQuantityMap.compute(productMsn,
                    (k, v) -> v == null ? stItem.getQuantity() : NumberUtil.round4(v + stItem.getQuantity()));
        }
        for (Map.Entry<String, Double> entry : prevProductQuantityMap.entrySet()) {
            inventoryService.saveInventoryHistory(sourceWarehouse, entry.getKey(), InventoryTransactionType.STN_PACKED,
                    InventoryMovementType.INVENTORY_OUT, String.valueOf(stockTransferNote.getStockTransferNoteId()), entry.getValue(),
                    (NumberUtil.round4(entry.getValue() - currentProductQuantityMap.get(entry.getKey()))));
        }


        saveAll(inboundStorages);
        Map<Integer, Double> stnQuantity = stItems.stream().collect(Collectors.groupingBy(stockTransfer -> stockTransfer.getProduct().getId(), Collectors.summingDouble(StockTransfer::getQuantity)));
        stnQuantity.forEach((key, value) -> inventoryService.deductAllocatedInventory(sourceWarehouse.getId(),
                key, value));
        //TODO: entries in SaleOrderAllocation and SaleOrderAllocationHistory

        saleOrderAllocationService.updateStatus(so.getId(), SaleOrderAllocationStatus.STN_TRANSFERRED);
        so.setStatus(SaleOrderStatus.ORDER_TRANSFERED);
        so.setAllocatedQuantity(0d);
        saleOrderService.upsert(so);
        EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(so.getEmsOrderItemId(), so.getAllocatedQuantity(), "WMS");
        emsService.updateEmsPortal(emsRequest);
    }

    @Override
    @Transactional
    public GetInventoryLocationsForTotalQtyResponse getLocationsForTotalQty(GetInventoryLocationsForTotalQtyRequest request) {
        logger.info("Get Inventory locations for total quantity service started");
        GetInventoryLocationsForTotalQtyResponse response = new GetInventoryLocationsForTotalQtyResponse();
        List<InboundStorage> inboundStorages = new ArrayList<>();
        if (request.getZoneId() != null) {
            if (request.getBinId() != null) {
                inboundStorages = repository.findByProductAndWarehouseAndZoneAndBinForTotal(request.getWarehouseId(), request.getProductId(), request.getZoneId(), request.getBinId());
            } else {
                inboundStorages = repository.findByProductAndWarehouseAndZoneForTotal(request.getWarehouseId(), request.getProductId(), request.getZoneId());
            }
        } else {
            inboundStorages = repository.findByProductAndWarehouseForTotal(request.getWarehouseId(), request.getProductId());
        }
        ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(request.getWarehouseId(), request.getProductId());
        InventoryLocationDto inventoryLocationDto = new InventoryLocationDto();
        List<InventoryLocationDto.LocationDto> locations;
        Map<Integer, InventoryLocationDto.LocationDto> map = new HashMap<>();
        InventoryLocationDto.LocationDto location;
        if (!CollectionUtils.isEmpty(inboundStorages)) {

            inventoryLocationDto.setAge(productInventory.getAverageAge());
            inventoryLocationDto.setProductId(productInventory.getProduct().getId());
            inventoryLocationDto.setProductMsn(productInventory.getProduct().getProductMsn());
            inventoryLocationDto.setProductName(productInventory.getProduct().getProductName());
            inventoryLocationDto.setQuantity(productInventory.getCurrentQuantity());
            inventoryLocationDto.setWarehouseId(productInventory.getWarehouse().getId());
            inventoryLocationDto.setWarehouseName(productInventory.getWarehouse().getName());
            inventoryLocationDto.setUom(productInventory.getProduct().getUom());

            for (InboundStorage inboundStorage : inboundStorages) {
                location = map.get(inboundStorage.getStorageLocation().getId());
                if (location == null) {
                    location = new InventoryLocationDto.LocationDto();
                    location.setQuantity(NumberUtil.round4(inboundStorage.getQuantity()));
                    location.setStorageLocationId(inboundStorage.getStorageLocation().getId());
                    location.setStorageLocationName(inboundStorage.getStorageLocation().getName());
                    location.setZoneId(inboundStorage.getStorageLocation().getZone().getId());
                    location.setZoneName(inboundStorage.getStorageLocation().getZone().getName());
                } else {
                    location.setQuantity(NumberUtil.round4(location.getQuantity() + inboundStorage.getQuantity()));
                }
                map.put(inboundStorage.getStorageLocation().getId(), location);
            }

            locations = new ArrayList<>(map.values());
            locations.sort(Comparator.comparing(InventoryLocationDto.LocationDto::getZoneId).thenComparing(InventoryLocationDto.LocationDto::getStorageLocationId));

            inventoryLocationDto.setLocations(locations);

            response.setMessage("Inventory locations found: " + locations.size());
            response.setStatus(true);
            response.setInventoryLocation(inventoryLocationDto);
        } else {
            response.setMessage("No Inventory locations found");
        }
        logger.info("Get Inventory locations for total quantity service ended");
        return response;
    }

    @Override
    @Transactional
    public GetInventoryLocationsForAvailableQtyResponse getLocationsForAvailableQty(GetInventoryLocationsForAvailableQtyRequest request) {
        logger.info("Get Inventory locations for available quantity service started");
        GetInventoryLocationsForAvailableQtyResponse response = new GetInventoryLocationsForAvailableQtyResponse();
        List<InboundStorage> inboundStorages = new ArrayList<>();
        if (request.getZoneId() != null) {
            if (request.getBinId() != null) {
                inboundStorages = repository.findByProductAndWarehouseAndZoneAndBinForAvailable(request.getWarehouseId(), request.getProductId(), request.getZoneId(), request.getBinId());
            } else {
                inboundStorages = repository.findByProductAndWarehouseAndZoneForAvailable(request.getWarehouseId(), request.getProductId(), request.getZoneId());
            }
        } else {
            inboundStorages = repository.findByProductAndWarehouseForAvailableBasedOnExpiry(request.getWarehouseId(), request.getProductId());
        }
        ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(request.getWarehouseId(), request.getProductId());
        InventoryLocationDto inventoryLocationDto = new InventoryLocationDto();
        List<InventoryLocationDto.LocationDto> locations;
        Map<Integer, InventoryLocationDto.LocationDto> map = new HashMap<>();
        InventoryLocationDto.LocationDto location;
        if (!CollectionUtils.isEmpty(inboundStorages)) {

            inventoryLocationDto.setAge(productInventory.getAverageAge());
            inventoryLocationDto.setProductId(productInventory.getProduct().getId());
            inventoryLocationDto.setProductMsn(productInventory.getProduct().getProductMsn());
            inventoryLocationDto.setProductName(productInventory.getProduct().getProductName());
            inventoryLocationDto.setQuantity(productInventory.getAvailableQuantity());
            inventoryLocationDto.setWarehouseId(productInventory.getWarehouse().getId());
            inventoryLocationDto.setWarehouseName(productInventory.getWarehouse().getName());
            inventoryLocationDto.setUom(productInventory.getProduct().getUom());

            for (InboundStorage inboundStorage : inboundStorages) {
                location = map.get(inboundStorage.getStorageLocation().getId());
                if (location == null) {
                    location = new InventoryLocationDto.LocationDto();
                    location.setQuantity(NumberUtil.round4(inboundStorage.getAvailableQuantity()));
                    location.setStorageLocationId(inboundStorage.getStorageLocation().getId());
                    location.setStorageLocationName(inboundStorage.getStorageLocation().getName());
                    location.setZoneId(inboundStorage.getStorageLocation().getZone().getId());
                    location.setZoneName(inboundStorage.getStorageLocation().getZone().getName());
                } else {
                    location.setQuantity(NumberUtil.round4(location.getQuantity() + inboundStorage.getQuantity()));
                }
                map.put(inboundStorage.getStorageLocation().getId(), location);
            }

            locations = new ArrayList<>(map.values());
            locations.sort(Comparator.comparing(InventoryLocationDto.LocationDto::getZoneId).thenComparing(InventoryLocationDto.LocationDto::getStorageLocationId));

            inventoryLocationDto.setLocations(locations);

            response.setMessage("Inventory locations found: " + locations.size());
            response.setStatus(true);
            response.setInventoryLocation(inventoryLocationDto);
        } else {
            response.setMessage("No Inventory locations found");
        }
        logger.info("Get Inventory locations for available quantity service ended");
        return response;
    }

    @Override
    public void saveAll(Iterable<InboundStorage> storages) {
        repository.saveAll(storages);
    }


    @Transactional
    public StockTransferResponse transferStock(Location sourceLocation, List<DestLocation> destLocations,
                                               String productMsn, Double srcQuantity, String transferredBy) {

        StockTransferResponse response = new StockTransferResponse();
        StorageLocation source = storageLocationService.findByWarehouseIdAndZoneIdAndRackIdAndBinId(
                sourceLocation.getWarehouseId(), sourceLocation.getZoneId(), sourceLocation.getRackId(),
                sourceLocation.getBinId());

        boolean shouldTransferAllocationInventory = true;

        if (source == null) {
            throw new WMSException("Not  storage location found for  :" + sourceLocation.toString());
        }

        if (source.getType().equals(StorageLocationType.QUARANTINE) && !this.authQuarantine(transferredBy)) {
            throw new WMSSecurtyException(
                    transferredBy + ": Not allowed to transfer stock from Quarantine Source!!!");
        }

        Product product = prodRepo.getUniqueByProductMsn(productMsn);

        if (product == null) {
            // no product found
            throw new WMSException("No Product Found for msn: " + productMsn);
        }
        response.setProductMsn(productMsn);
        response.setProductDesc(product.getProductName());

        List<SourceDestinationDetail> destinationDetails = new ArrayList<>();
        Double sourceQuantity = 0.0;
        for (DestLocation destLocation : destLocations) {
            Integer locationId = destLocation.getStorageLocationId();
            StorageLocation destination = storageLocationRepository.findById(locationId).get();
            if (destination == null) {
                throw new WMSException("No storage location found for :" + locationId);

            }
            List<InboundStorage> inboundStorages = repository.findByProductIdAndStorageLocationIdAndQuantityGreaterThan(product.getId(),
                    source.getId(), 0.0d);
            List<InboundStorage> destInboundStorage = repository.findByProductIdAndStorageLocationId(product.getId(),
                    destination.getId());

            inboundStorages.stream().forEach(storage -> {
                if (destination.getType().equals(StorageLocationType.QUARANTINE) && !storage.isConfirmed()) {
                    throw new WMSException(storage.getAvailableQuantity()
                            + " qty is non-inventorized customer return at storage Location" + source.getName()
                            + ". Please inventorise first.");
                }
            });

            if (product.getExpiryDateManagementEnabled() != null && product.getExpiryDateManagementEnabled()
                    && (!CollectionUtils.isEmpty(destInboundStorage))
                    && (!inboundStorages.get(0).getExpiryDate().equals(destInboundStorage.get(0).getExpiryDate()))) {
                throw new WMSException("destination location  have different expiry date for  this MSN");
            }

            Set<String> packetSet = new HashSet<>();

            for (InboundStorage storage : inboundStorages) {
                if (!CollectionUtils.isEmpty(storage.getPacketItem())) {
                    for (PacketItem item : storage.getPacketItem()) {
                        if (item.getPacket().getStatus().isPacked() && !packetSet.contains(item.getPacket().getInvoiceNumber())) {
                            packetSet.add(item.getPacket().getInvoiceNumber());
                        }
                    }
                }
            }

            double totalMovableAvailableQuantity = inboundStorages.stream()
                    .mapToDouble(InboundStorage::getAvailableQuantity).sum();

            double totalMovableAllocatedQuantity = inboundStorages.stream()
                    .mapToDouble(InboundStorage::getAllocatedQuantity).sum();

            if (!CollectionUtils.isEmpty(packetSet) && totalMovableAvailableQuantity <= 0) {
                throw new WMSException("Invoices found" + packetSet + " found at storage Location" + source.getName()
                        + ". Please Cancel these invoices first.");
            } else if (!CollectionUtils.isEmpty(packetSet) && totalMovableAvailableQuantity > 0) {
                shouldTransferAllocationInventory = false;
            }

            Double transferQuantity = destLocation.getQuantity();
            if (transferQuantity <= 0.0) {
                throw new WMSException("Stock Cannot be transferred because transfer quantity is: " + transferQuantity);

            }
            sourceQuantity += transferQuantity;
            if (shouldTransferAllocationInventory) {
                if (transferQuantity > (totalMovableAvailableQuantity + totalMovableAllocatedQuantity)) {
                    // throw error here
                    throw new WMSException("Stock Cannot be transferred because total movable quantity is: "
                            + (totalMovableAvailableQuantity + totalMovableAllocatedQuantity));
                }
            } else {
                if (transferQuantity > (totalMovableAvailableQuantity)) {
                    // throw error here
                    throw new WMSException("Stock Cannot be transferred because total movable quantity is: "
                            + (totalMovableAvailableQuantity));
                }
            }


            // Deduct free inventory
            double transferredQuantity = 0.0d;

            for (InboundStorage storage : inboundStorages) {

                double transferableQuantity = Math.min(storage.getAvailableQuantity(), transferQuantity);

                double transferableAllocatedQuantity = shouldTransferAllocationInventory ? Math.min(storage.getAllocatedQuantity(), transferQuantity - transferableQuantity) : 0.0d;


                if (transferableQuantity != 0 || transferableAllocatedQuantity != 0) {

                    if (destination.getType().equals(StorageLocationType.QUARANTINE) && !storage.isConfirmed()) {
                        continue;
                    }

                    if (destination.getType().equals(StorageLocationType.QUARANTINE) && transferableAllocatedQuantity > 0) {
                        throw new WMSException("Cannot transfer Allocated inventory to quarantine bin. Move allocated inventory to available and try again");
                    }

                    transferredQuantity += (transferableQuantity + transferableAllocatedQuantity);
                    storage.setAvailableQuantity(NumberUtil.round4(storage.getAvailableQuantity() - transferableQuantity));
                    storage.setAllocatedQuantity(NumberUtil.round4(storage.getAllocatedQuantity() - transferableAllocatedQuantity));
                    storage.setQuantity(NumberUtil.round4(storage.getAvailableQuantity() + storage.getAllocatedQuantity()));


					transferQuantity = NumberUtil.round4(transferQuantity - transferableQuantity - transferableAllocatedQuantity);
					InboundStorage destStorage = new InboundStorage();
					destStorage.setStorageLocation(destination);
					destStorage.setAvailableQuantity(NumberUtil.round4(transferableQuantity));
					destStorage.setAllocatedQuantity(transferableAllocatedQuantity);
					destStorage.setQuantity(NumberUtil.round4(destStorage.getAvailableQuantity() + destStorage.getAllocatedQuantity()));
					destStorage.setInbound(storage.getInbound());
					destStorage.setProduct(product);
					destStorage.setConfirmed(storage.isConfirmed());
					List<SaleOrderAllocation> saleOrderAllocations = new ArrayList<>();
					if (transferableAllocatedQuantity != 0) {
						double tempTransferableAllocatedQuantity = transferableAllocatedQuantity;
						List<SaleOrderAllocation> allocations = saleOrderAllocationRepo
								.findAllByInboundStorageAndStatusAndAvailableQuantityGreaterThan(storage, SaleOrderAllocationStatus.ALLOCATED, 0.0d);
						for (SaleOrderAllocation allocation : allocations) {
							if (tempTransferableAllocatedQuantity <= 0) {
								break;
							}
							if (tempTransferableAllocatedQuantity >= allocation.getAvailableQuantity()) {
								allocation.setInboundStorage(destStorage);
								tempTransferableAllocatedQuantity = tempTransferableAllocatedQuantity
										- allocation.getAvailableQuantity();
							} else {
								allocation.setAvailableQuantity(
										allocation.getAvailableQuantity() - tempTransferableAllocatedQuantity);
								SaleOrderAllocation newAllocation = new SaleOrderAllocation();
								newAllocation.setAllocatedQuantity(tempTransferableAllocatedQuantity);
								newAllocation.setAvailableQuantity(tempTransferableAllocatedQuantity);
								newAllocation.setSaleOrder(allocation.getSaleOrder());
								newAllocation.setInboundStorage(destStorage);
								saleOrderAllocations.add(newAllocation);
								tempTransferableAllocatedQuantity = tempTransferableAllocatedQuantity
										- newAllocation.getAvailableQuantity();
							}
							saleOrderAllocations.add(allocation);
						}
					}
					
					repository.save(storage);
					repository.save(destStorage);
					
					saleOrderAllocationRepo.saveAll(saleOrderAllocations);
					
					StorageLocationType destType = destination.getType();
					StorageLocationType sourceType = source.getType();
					
					if(sourceType.name().equals(StorageLocationType.QUARANTINE.name())) {
						//Check if destination is Quarantine
						if(!destType.name().equals(StorageLocationType.QUARANTINE.name())) {
							logger.info("Destination Location is  not Quarantine Id: " + destination.getId());
							//update inventory table
							logger.info("Destination Location is not Quarantine  Id: " +destination.getId() );
							product.setAvailableQuantity(NumberUtil.round4(product.getAvailableQuantity() + transferableQuantity));
							product.setAllocatedQuantity(NumberUtil.round4(product.getAllocatedQuantity() + transferableAllocatedQuantity));
							product.setCurrentQuantity(NumberUtil.round4(product.getAllocatedQuantity() + product.getAvailableQuantity()));
							prodRepo.save(product);
							logger.info("available quantity for productId: " + product.getId() + " has changed from: " + (product.getAvailableQuantity() - transferableQuantity) + " to: " + product.getAvailableQuantity());
							logger.info("current quantity for productId: " + product.getId() + " has changed from: " + (product.getCurrentQuantity() - transferableQuantity) + " to: " + product.getCurrentQuantity());


                            ProductInventory productInventory = productInventoryRepository.findByWarehouseIdAndProductId(destination.getWarehouse().getId(), product.getId());
                            productInventory.setAvailableQuantity(NumberUtil.round4(productInventory.getAvailableQuantity() + transferableQuantity));
                            productInventory.setAllocatedQuantity(NumberUtil.round4(productInventory.getAllocatedQuantity() + transferableAllocatedQuantity));
                            productInventory.setCurrentQuantity(NumberUtil.round4(productInventory.getAllocatedQuantity() + productInventory.getAvailableQuantity()));
                            productInventoryRepository.save(productInventory);
                            logger.info("available quantity in product Inventory for productId: " + product.getId() + " and warehouseId: " + destination.getWarehouse().getId() + " has changed from: " + (productInventory.getAvailableQuantity() - transferableQuantity) + " to: " + productInventory.getAvailableQuantity());
                            logger.info("current quantity in product Inventory for productId: " + product.getId() + " and warehouseId: " + destination.getWarehouse().getId() + " has changed from: " + (productInventory.getCurrentQuantity() - transferableQuantity) + " to: " + productInventory.getCurrentQuantity());
                        } else {
                            logger.trace("Destination bin is Quarantine bin. Performing no operation on inventory table");
                        }

                    } else {
                        if (destType.name().equals(StorageLocationType.QUARANTINE.name())) {
                            logger.info("Destination Location is Quarantine Id: " + destination.getId());
                            product.setAvailableQuantity(NumberUtil.round4(product.getAvailableQuantity() - transferableQuantity));
                            product.setAllocatedQuantity(NumberUtil.round4(product.getAllocatedQuantity() - transferableAllocatedQuantity));
                            product.setCurrentQuantity(NumberUtil.round4(product.getAllocatedQuantity() + product.getAvailableQuantity()));
                            prodRepo.save(product);
                            logger.info("available quantity for productId: " + product.getId() + " has changed from: "
                                    + (product.getAvailableQuantity() + transferableQuantity) + " to: "
                                    + product.getAvailableQuantity());
                            logger.info("current quantity for productId: " + product.getId() + " has changed from: "
                                    + (product.getCurrentQuantity() + transferableQuantity) + " to: "
                                    + product.getCurrentQuantity());
                            ProductInventory productInventory = productInventoryRepository
                                    .findByWarehouseIdAndProductId(sourceLocation.getWarehouseId(), product.getId());
                            productInventory.setAvailableQuantity(
                                    NumberUtil.round4(productInventory.getAvailableQuantity() - transferableQuantity));
                            productInventory.setAllocatedQuantity(NumberUtil
                                    .round4(productInventory.getAllocatedQuantity() - transferableAllocatedQuantity));
                            productInventory
                                    .setCurrentQuantity(NumberUtil.round4(productInventory.getAllocatedQuantity() + productInventory.getAvailableQuantity()));

                            productInventoryRepository.save(productInventory);
                            logger.info("available quantity in product Inventory for productId: " + product.getId()
                                    + " and warehouseId: " + sourceLocation.getWarehouseId() + " has changed from: "
                                    + (productInventory.getAvailableQuantity() + transferableQuantity) + " to: "
                                    + productInventory.getAvailableQuantity());
                            logger.info("current quantity in product Inventory for productId: " + product.getId()
                                    + " and warehouseId: " + sourceLocation.getWarehouseId() + " has changed from: "
                                    + (productInventory.getCurrentQuantity() + transferableQuantity) + " to: "
                                    + productInventory.getCurrentQuantity());
                        } else {
                            logger.info("Destination Location is  not Quarantine Id: " + destination.getId() + " No Changes made to productInventory");
                        }

                    }

                    if (transferQuantity == 0) {
                        break;
                    }
                }
            }
            SourceDestinationDetail destinationDetail = new SourceDestinationDetail();
            destinationDetail.setBinId(destination.getBin().getId());
            destinationDetail.setZoneId(destination.getZone().getId());
            destinationDetail.setQuantity(transferredQuantity);
            destinationDetail.setZoneName(destination.getZone().getName());
            destinationDetail.setStorageLocationName(destination.getName());

            destinationDetails.add(destinationDetail);

        }

        SourceDestinationDetail sourceDetail = new SourceDestinationDetail();
        sourceDetail.setBinId(source.getBin().getId());
        sourceDetail.setZoneId(source.getZone().getId());
        sourceDetail.setQuantity(sourceQuantity);
        sourceDetail.setStorageLocationName(source.getName());
        sourceDetail.setZoneName(source.getZone().getName());
        response.setSourceDetail(sourceDetail);

        response.setDestinationDetails(destinationDetails);
        response.setCode(200);
        response.setStatus(true);
        response.setMessage("Bin Transferred successfully");

        return response;
    }

    private boolean authQuarantine(String transferredBy) {
        return usersList.contains(transferredBy) ? true : false;
    }

    @Override
    @Transactional
    public List<StockTransferResponse> binTransfer(List<StockTransferRequest> request) {
        List<StockTransferResponse> responseList = new ArrayList<>();

        for (StockTransferRequest binTransferRequest : request) {
            String transferredBy = binTransferRequest.getEmail();
            BinTransferDao binTransfer = new BinTransferDao();
            binTransfer.setTransferredBy(transferredBy);
            binTransfer = binTransferRepository.save(binTransfer);
            StockTransferResponse response = new StockTransferResponse();
            Boolean binHistoryFlag = true;
            Product product = prodRepo.getUniqueByProductMsn(binTransferRequest.getProductMsn());

            try {
                response = this.transferStock(binTransferRequest.getSourceLocation(),
                        binTransferRequest.getDestinationtLocations(), binTransferRequest.getProductMsn(),
                        binTransferRequest.getQuantity(), transferredBy);
            } catch (Exception e) {
                response.setMessage(e.getMessage());
                response.setCode(200);
                response.setStatus(false);
                response.setProductMsn(binTransferRequest.getProductMsn());
                if (product != null) {
                    response.setProductDesc(product.getProductName());
                }

                binHistoryFlag = false;
            }

            StorageLocation source = storageLocationService.findByWarehouseIdAndZoneIdAndRackIdAndBinId(
                    binTransferRequest.getSourceLocation().getWarehouseId(),
                    binTransferRequest.getSourceLocation().getZoneId(),
                    binTransferRequest.getSourceLocation().getRackId(),
                    binTransferRequest.getSourceLocation().getBinId());
            String productMsn = binTransferRequest.getProductMsn();

            if (binHistoryFlag) {
                for (DestLocation destLocation : binTransferRequest.getDestinationtLocations()) {

                    Integer locationId = destLocation.getStorageLocationId();
                    Optional<StorageLocation> destination = storageLocationRepository.findById(locationId);
                    if (destination.isPresent()) {
                        this.saveBinTransferHistory(binTransfer, source, destination.get(), destLocation.getQuantity(),
                                productMsn, BinTransferStatus.SUCCESSFULL);

                    }
                }

            } else {
                for (DestLocation destLocation : binTransferRequest.getDestinationtLocations()) {

                    Integer locationId = destLocation.getStorageLocationId();
                    Optional<StorageLocation> destination = storageLocationRepository.findById(locationId);
                    if (destination.isPresent()) {
                        this.saveBinTransferHistory(binTransfer, source, destination.get(), destLocation.getQuantity(),
                                productMsn, BinTransferStatus.UNSUCCESSFULL);

                    }

                }

            }
            responseList.add(response);
        }
        return responseList;

    }

    public void saveBinTransferHistory(BinTransferDao binTransfer, StorageLocation source, StorageLocation destination, Double transferQuantity, String productMsn, BinTransferStatus status) {
        BinTransferHistory binTransferHistory = new BinTransferHistory();
        binTransferHistory.setBinTransfers(binTransfer);
        binTransferHistory.setFromStorageLocation(source);
        binTransferHistory.setToStorageLocation(destination);
        binTransferHistory.setQuantity(transferQuantity);
        binTransferHistory.setMsn(productMsn);
        binTransferHistory.setStatus(status);
        binTransferHistoryRepository.save(binTransferHistory);
    }

    @Scheduled(fixedRate = 30000)
    public void mapForEmails() {
        usersList = rolePermRepository.getPermittedUsers();
        logger.info("Bin transfers user list: " + usersList);
    }

}


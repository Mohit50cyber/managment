package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.moglix.wms.api.request.BuyersCreateSaleOrderRequest;
import com.moglix.wms.api.request.CheckAvailableQtyRequest;
import com.moglix.wms.api.request.CreateSaleOrderRequest;
import com.moglix.wms.api.request.CreateSaleOrderRequest.EmsOrderItem;
import com.moglix.wms.api.request.DeleteSaleOrderItemRequest;
import com.moglix.wms.api.request.EMSPackableQuantityRequest;
import com.moglix.wms.api.request.GetAllocatedQtyByItemRefRequest;
import com.moglix.wms.api.request.GetInventoryForAllocatedQtyRequest;
import com.moglix.wms.api.request.GetInventoryForPackedQtyRequest;
import com.moglix.wms.api.request.GetSaleOrderRequest;
import com.moglix.wms.api.request.InventoryUpdateRequest;
import com.moglix.wms.api.request.RepushSaleOrderRequest;
import com.moglix.wms.api.request.UpdateOrderQuantityRequest;
import com.moglix.wms.api.response.Allocation;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CheckAvailableQtyResponse;
import com.moglix.wms.api.response.CheckDeleteSaleOrderEligibilityResponse;
import com.moglix.wms.api.response.CreateSaleOrderResponse;
import com.moglix.wms.api.response.DeleteSaleOrderItemResponse;
import com.moglix.wms.api.response.GetAllocatedQtyByItemRefResponse;
import com.moglix.wms.api.response.GetInventoryForAllocatedQtyResponse;
import com.moglix.wms.api.response.GetInventoryForPackedQtyResponse;
import com.moglix.wms.api.response.GetSaleOrderResponse;
import com.moglix.wms.api.response.SaleOrderAllocationResponse;
import com.moglix.wms.api.response.SaleOrderDeallocationResponse;
import com.moglix.wms.api.response.UpdateFulFillmentWarehouseResponse;
import com.moglix.wms.api.response.UpdateOrderMsnResponse;
import com.moglix.wms.api.response.UpdateOrderQuantityResponse;
import com.moglix.wms.constants.BulkInvoiceStatus;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.OrderType;
import com.moglix.wms.constants.PacketItemStatus;
import com.moglix.wms.constants.PublishSystemType;
import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.constants.SaleOrderStatus;
import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.dto.InventoryLocationDto;
import com.moglix.wms.dto.SalesOpsItemDetailsDTO;
import com.moglix.wms.dto.SalesOpsOrderDTO;
import com.moglix.wms.dto.SalesOpsOrderDetailsDTO;
import com.moglix.wms.entities.BulkInvoicingSaleOrder;
import com.moglix.wms.entities.Country;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.PacketItem;
import com.moglix.wms.entities.Plant;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.entities.SaleOrderAllocationHistory;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMappingItem;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.kafka.producer.KafkaEmsSalesOpsUpdateProducer;
import com.moglix.wms.producer.FifoProducer;
import com.moglix.wms.queueModel.InventoryAllocationRequest;
import com.moglix.wms.repository.BulkInvoicingSaleOrderRepository;
import com.moglix.wms.repository.CountryRepository;
import com.moglix.wms.repository.PlantRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.SaleOrderAllocationHistoryRepository;
import com.moglix.wms.repository.SaleOrderAllocationRepository;
import com.moglix.wms.repository.SaleOrderRepository;
import com.moglix.wms.repository.SaleOrderSupplierPurchaseOrderMappingItemRepository;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IOrderValidationService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.ISaleOrderService;
import com.moglix.wms.service.IWarehouseService;
import com.moglix.wms.util.JsonUtil;
import com.moglix.wms.util.NumberUtil;

/**
 * @author pankaj on 6/5/19
 */
@Service("saleOrderService")
public class SaleOrderServiceImpl implements ISaleOrderService {

    Logger logger = LogManager.getLogger(SaleOrderServiceImpl.class);

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private SaleOrderAllocationRepository saleOrderAllocationRepository;

    @Autowired
    private SaleOrderAllocationHistoryRepository saleOrderAllocationHistoryRepository;

    @Autowired
    private IWarehouseService warehouseService;

    @Autowired
    private IProductInventoryService productInventoryService;

    @Autowired
	@Qualifier("inventoryService")
    private IInventoryService inventoryService;
    
    @Autowired
	@Qualifier("saleOrderTransferInventoryService")
    private IInventoryService saleOrderTransferInventoryService;

    @Autowired
    private FifoProducer producer;
    
    @Autowired
    private ProductsRepository prodRepo;

    @Autowired
    private BulkInvoicingSaleOrderRepository bulkInvoicingSaleOrderRepository;
    
    @Autowired
    private IInboundStorageService inboundStorageService;
    
    @Autowired
    private PlantRepository plantRepo;
   
    @Value("${queue.allocation}")
    private String ALLOCATION_QUEUE;
 
    @Value("${queue.allocation.new}")
    private String NEW_ALLOCATION_QUEUE;

    @Autowired
	private IProductService productService;
    
    @Autowired
    private CountryRepository countryRepository;
    
	@Autowired
	@Qualifier("orderValidationService")
	IOrderValidationService orderValidationService;
	
	@Autowired
	private SaleOrderSupplierPurchaseOrderMappingItemRepository saleOrderSupplierPurchaseOrderMappingItemRepository;
	
	@Autowired
	KafkaEmsSalesOpsUpdateProducer kafkaPublisherInventoryUpdate;
    
    private Gson gson = new Gson();

    @Override
    public SaleOrder upsert(SaleOrder obj) {
        return saleOrderRepository.save(obj);
    }

    @Override
    public SaleOrder getById(Integer id) {
        return saleOrderRepository.findById(id).orElse(null);
    }

    @Override
    public SaleOrder getByItemRef(String itemRef) {
        return saleOrderRepository.findByItemRef(itemRef);
    }

    @Override
    public List<SaleOrder> findOpenSaleOrderForProduct(Integer productId) {
        // ignore SaleOrder for which STN is generated.
        return saleOrderRepository.findOpenSaleOrderForProduct(productId).stream()
        		.filter(so->so.getStnAssoication()!=true)
        		.collect(Collectors.toList());
    }

    @Override
    public List<SaleOrder>getOrdersfromOrderItemIds(Set<Integer> emsOrderItemIds) {
    	
    	return saleOrderRepository.findAllByEmsOrderItemIdInAndStatus(emsOrderItemIds, SaleOrderStatus.OPEN);
    }

    @Override
    public SaleOrderAllocation upsertAllocation(SaleOrderAllocation obj) {
        return saleOrderAllocationRepository.save(obj);
    }

    @Override
    public SaleOrderAllocation getAllocationById(Integer id) {
        return saleOrderAllocationRepository.findById(id).orElse(null);
    }

    @Override
    public SaleOrderAllocationHistory upsertAllocationHistory(SaleOrderAllocationHistory obj) {
        return saleOrderAllocationHistoryRepository.save(obj);
    }

    @Override
    public Iterable<SaleOrder> upsertAll(List<SaleOrder> saleOrders) {
        return saleOrderRepository.saveAll(saleOrders);
    }

    private Plant createPlant(Integer plantId) {
    	
    	return plantRepo.save(new Plant(plantId));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreateSaleOrderResponse createSaleOrder(CreateSaleOrderRequest request) {
       
    	logger.info("Request received to create sale order: " + new Gson().toJson(request));
    	logger.info("Create Sale Order Service Started");
        
    	CreateSaleOrderResponse response = new CreateSaleOrderResponse();

    	Country country = countryRepository.findByIsoNumber(request.getCountryId());
        Warehouse warehouse = warehouseService.getById(request.getFulfillmentWarehouseId());

        Product product;
        SaleOrder saleOrder;
        for(CreateSaleOrderRequest.EmsOrderItem item : request.getItems()) {
            // product = prodRepo.getUniqueByProductMsn(item.getProductMsn());
            product = orderValidationService.validateProductMSN(item.getProductMsn(), item.getItemRef());
			
            if (product == null) {
                response.setMessage("Invalid Product or ProductMSN is not in sync with wms.");
                response.setStatus(false);
                logger.warn(String.format("Invalid ProductMSN :: [%s]", item.getProductMsn()));
                return response;
            }
        	
            saleOrder = saleOrderRepository.getSaleOrderByEmsOrderItemId(item.getEmsOrderItemId());
            if(saleOrder != null) {
                response.setMessage("Order already exist in WMS");
                logger.warn(String.format("Order [%s] already exist in WMS!!!", item.getEmsOrderItemId()));
                response.setStatus(true);
                return response;
            }

            saleOrder = new SaleOrder();
            saleOrder.setEmsOrderId(request.getEmsOrderId());
            saleOrder.setEmsOrderItemId(item.getEmsOrderItemId());
            saleOrder.setWarehouse(warehouse);
            saleOrder.setProduct(product);
            saleOrder.setOrderedQuantity(NumberUtil.round4(item.getOrderedQuantity()));
            saleOrder.setRemark(item.getRemark());
            saleOrder.setItemRef(item.getItemRef());
            saleOrder.setOrderType(OrderType.valueOf(request.getOrderType()));
            saleOrder.setCountry(country);
            saleOrder.setOrderRef(request.getOrderRef());
            
            if(request.getPlantId() != null) {
            	
            	Plant plant = plantRepo.findByBuyersPlantId(request.getPlantId()).orElse(null);
            	
            	if (plant!=null) {
            		logger.info(String.format("Plant found :: [%s]", request.getPlantId()));
            		saleOrder.setPlant(plant);
            	}
            	else {
            		logger.info(String.format("Plant not found :: [%s]", request.getPlantId()));
            		saleOrder.setPlant(createPlant(request.getPlantId()));
            	}
            }
            
            if(request.getBulkInvoiceId() != null) {
            	saleOrder.setBulkInvoiceId(request.getBulkInvoiceId());
            	saleOrder.setUniqueblockid(request.getInventory_id());
            }
            
            if(request.getBatchRef() != null) {
            	saleOrder.setBatchRef(request.getBatchRef());
            }

            saleOrder = upsert(saleOrder);
    		
            if(saleOrder != null && !item.isCloned()) {
            	response.getSaleOrderIds().add(saleOrder.getId());
            	response.getEmsOrderItemIds().add(saleOrder.getEmsOrderItemId());
            }
            else if(saleOrder != null && item.isCloned()) {
            	BaseResponse purchaseResponse = updatePurchaseDemandMapping(item.getItemRef());

            	if(purchaseResponse.getStatus()) {
            		logger.info("Demand Mapping Updated Successfully");
            	}else {
            		logger.info("Demand Update Failed");
            	}
            }
        }

        response.setMessage("Sale Order Created with " + request.getItems().size() + " items");
        response.setStatus(true);        
        logger.info("Create Sale Order Service Ended");
        return response;
    }

    private BaseResponse updatePurchaseDemandMapping(String itemRef) {
    	RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", Constants.SALES_OPS_AUTH_TOKEN);
		ResponseEntity<BaseResponse> response = template.exchange(Constants.PURCHASE_DEMAND_MAPPING_API + itemRef, HttpMethod.PUT, new HttpEntity<>(headers), BaseResponse.class);
		return response.getBody();
    }
    
	@Override
    public GetSaleOrderResponse getSaleOrder(GetSaleOrderRequest request) {
        logger.info("Get Sale Order Service Started");
        GetSaleOrderResponse response = new GetSaleOrderResponse();

        SaleOrder saleOrder = saleOrderRepository.getSaleOrderByEmsOrderItemId(request.getEmsOrderItemId());
        if(saleOrder != null) {
            response.setMessage("Order exist in WMS");
            logger.info("Order exist in WMS");
            response.setStatus(true);
            return response;
        } else {
            response.setMessage("Sale Order does not exist for emsOrderItemId: " + request.getEmsOrderItemId());
            logger.info("Sale Order does not exist for emsOrderItemId: " + request.getEmsOrderItemId());
        }
        logger.info("Get Sale Order Service Ended");
        return response;
    }

    @Override
    public DeleteSaleOrderItemResponse deleteSaleOrderItem(DeleteSaleOrderItemRequest request) {
        logger.info("Delete Sale Order Item Service Started");
        DeleteSaleOrderItemResponse response = new DeleteSaleOrderItemResponse();

        List<DeleteSaleOrderItemResponse.DeleteItemResponse> deletionResponse = new ArrayList<>();
        DeleteSaleOrderItemResponse.DeleteItemResponse itemDeletion;
        int count=0;
        List<SaleOrder> saleOrders = saleOrderRepository.findSaleOrderByEmsOrderItemIdIn(request.getEmsOrderItemIds());
        Set<Integer> productIds = new HashSet<>();
        for(SaleOrder order : saleOrders) {
            try {
                if (order == null) {
                    itemDeletion = new DeleteSaleOrderItemResponse.DeleteItemResponse(null, false, "Order not found");
                } else if (order.getStatus().equals(SaleOrderStatus.CANCELLED)) {
                    itemDeletion = new DeleteSaleOrderItemResponse.DeleteItemResponse(order.getEmsOrderItemId(), false, "Order is already cancelled");
                } else if (order.getStatus().equals(SaleOrderStatus.FULFILLED)) {
                    itemDeletion = new DeleteSaleOrderItemResponse.DeleteItemResponse(order.getEmsOrderItemId(), false, "Order has been fulfilled");
                } else if (order.getPackedQuantity() > 0) {
                    itemDeletion = new DeleteSaleOrderItemResponse.DeleteItemResponse(order.getEmsOrderItemId(), false, "Order is packed");
                } else {
                    count++;
                    Double allocatedQty = order.getAllocatedQuantity();

                    boolean orderCancelled = deallocateInventoryAndCancelOrder(order);

                    if (allocatedQty > 0 && orderCancelled) {
                        productIds.add(order.getProduct().getId());
                    }

                    List <SaleOrderSupplierPurchaseOrderMappingItem> saleOrderSupplierPurchaseOrderMappingItemList = saleOrderSupplierPurchaseOrderMappingItemRepository.findByItemRefAndSaleOrderIdAndProductMSNAndStatusNot(order.getItemRef(), order.getId(), order.getProduct().getProductMsn(), SaleOrderSupplierPurchaseOrderMappingStatus.CANCELLED);        
                	if (!saleOrderSupplierPurchaseOrderMappingItemList.isEmpty()) {
	                    for(SaleOrderSupplierPurchaseOrderMappingItem item : saleOrderSupplierPurchaseOrderMappingItemList) {
		                    if(item!=null) {
		                    	item.setStatus(SaleOrderSupplierPurchaseOrderMappingStatus.CANCELLED);
								saleOrderSupplierPurchaseOrderMappingItemRepository.save(item);
		                    }
	                    }
                	}
                    itemDeletion = new DeleteSaleOrderItemResponse.DeleteItemResponse(order.getEmsOrderItemId(), true, "Order cancelled");
                }
                deletionResponse.add(itemDeletion);
            } catch (Exception e) {
                logger.info("Exception occured: " + e.toString());
            }
        }

        //push product to inventory allocation queue
        productIds.stream().map(productId -> new InventoryAllocationRequest(null, productId)).forEach(input -> producer.sendMessage(NEW_ALLOCATION_QUEUE, JsonUtil.toJson(input)));

        response.setMessage("Sale Order Items deleted: " + count);
        response.setStatus(true);
        response.setElements(deletionResponse);
        logger.info("Delete Sale Order Item Service Ended");
        return response;
    }

    private boolean deallocateInventoryAndCancelOrder(SaleOrder order) {
        order.setStatus(SaleOrderStatus.CANCELLED);

        Double allocatedQty = order.getAllocatedQuantity();

        if(allocatedQty > 0) {
            inventoryService.deAllocateInventory(order);
            order.setAllocatedQuantity(0d);
        }

        upsert(order);

        return true;
    }
        
    @Transactional
    @Override
	public UpdateOrderMsnResponse deallocateInventoryAndUpdateOrderMsn(Integer emsOrderItemId, String productMsn) {
    	
    	SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(emsOrderItemId);

    	if(order == null) {
    		return new UpdateOrderMsnResponse("Order Not Found", false, HttpStatus.OK.value(), null);
    	}

		Double allocatedQty = order.getAllocatedQuantity();

		//Deduct allocated inventory
		if (allocatedQty > 0) {
			inventoryService.deAllocateInventory(order);
			order.setAllocatedQuantity(0d);
		}

		//change product msn
		order.setProduct(prodRepo.getUniqueByProductMsn(productMsn));

		upsert(order);

		return new UpdateOrderMsnResponse("Order Msn Update", true, HttpStatus.OK.value(), order.getId());
	}

    @Transactional
    @Override
    public CheckAvailableQtyResponse checkAvailability(CheckAvailableQtyRequest request) {
        logger.info("Check Available Qty for Sale Order Service Started");
        CheckAvailableQtyResponse response = new CheckAvailableQtyResponse();

        List<String> msnList = new ArrayList<>();
        List<Integer> warehouseIds = new ArrayList<>();

        request.getMsnWarehouseList().forEach(i -> {
            msnList.add(i.getProductMsn());
            warehouseIds.add(i.getWarehouseId());
        });

        List<ProductInventory> productInventories = productInventoryService.getByWarehouseIdInAndProductProductMsnIn(warehouseIds, msnList);

        if(CollectionUtils.isEmpty(productInventories)) {
            response.setMessage("Product does not exist in warehouse");
        } else {
            for(ProductInventory productInventory : productInventories) {
                response.getAvailableQtyList().add(new CheckAvailableQtyResponse.AvailableQty(productInventory.getWarehouse().getId(), productInventory.getProduct().getProductMsn(),productInventory.getAvailableQuantity()));
            }
            response.setStatus(true);
            response.setMessage("Available qty fetched");
        }

        logger.info("Check Available Qty for Sale Order Service Ended");
        return response;
    }

    @Transactional
    @Override
    public GetAllocatedQtyByItemRefResponse getAllocatedQty(GetAllocatedQtyByItemRefRequest request) {
        logger.info("Get Allocated Qty for Sale Order Service By Item Ref Started");
        GetAllocatedQtyByItemRefResponse response = new GetAllocatedQtyByItemRefResponse();

        List<SaleOrder> saleOrders = saleOrderRepository.findAllByItemRefIn(request.getItemRefs());

        if(!CollectionUtils.isEmpty(saleOrders)) {
            for(SaleOrder saleOrder : saleOrders) {
                response.getAllocatedQtyList().add(new GetAllocatedQtyByItemRefResponse.AllocatedQty(saleOrder.getItemRef(), saleOrder.getAllocatedQuantity(), saleOrder.getPackedQuantity()));
            }
            response.setMessage("Order Found: " + saleOrders.size());
            response.setStatus(true);
        } else {
            response.setMessage("No Order Found");
        }

        logger.info("Get Allocated Qty for Sale Order Service By Item Ref Ended");
        return response;
    }

    @Override
    public GetInventoryForAllocatedQtyResponse getProductInventoryAvailabilityInfo(GetInventoryForAllocatedQtyRequest request) {
        logger.info("Get Inventory locations for allocated quantity service started"+ new Gson().toJson(request));
        Double packed_quant=0d;
        List<SaleOrderAllocation> saleOrderAllocations = new ArrayList<>();
        if(request.getZoneId() != null) {
			if(request.getBinId() != null) {
				saleOrderAllocations = saleOrderAllocationRepository.getForAllocatedQtyAndProductAndZoneAndBinTransferred(request.getWarehouseId(), request.getProductId(), request.getZoneId(), request.getBinId());
			}else {
				saleOrderAllocations = saleOrderAllocationRepository.getForAllocatedQtyAndProductAndZoneTransferred(request.getWarehouseId(), request.getProductId(), request.getZoneId());
			}
		}else {
			saleOrderAllocations = saleOrderAllocationRepository.getForAllocatedQtyAndProductTransferred(request.getWarehouseId(), request.getProductId());
		}
        
        ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(request.getWarehouseId(), request.getProductId());
        GetInventoryForAllocatedQtyResponse response = new GetInventoryForAllocatedQtyResponse();
        if (!CollectionUtils.isEmpty(saleOrderAllocations)) {
            List<InventoryLocationDto.LocationDto> locations = new ArrayList<>();
            for (SaleOrderAllocation saleOrderAllocation : saleOrderAllocations) {
            	if(saleOrderAllocation.getStatus().equals(SaleOrderAllocationStatus.ALLOCATED)) {
                    if (saleOrderAllocation.getAvailableQuantity() > 0 && saleOrderAllocation.getPackedQuantity() == 0) {
                        locations.add(new InventoryLocationDto.LocationDto(saleOrderAllocation, saleOrderAllocation.getAvailableQuantity(), saleOrderAllocation.getPackedQuantity(), "NA"));
                    } else if (saleOrderAllocation.getAvailableQuantity() > 0 && saleOrderAllocation.getPackedQuantity() > 0) {
                        locations.add(new InventoryLocationDto.LocationDto(saleOrderAllocation, saleOrderAllocation.getAvailableQuantity(), 0d, "NA"));
                        // pick available quantity from packet invoice level
                        List<PacketItem> packetItems = saleOrderAllocation.getPacketItems().stream().filter(allocation -> !allocation.getStatus().equals(PacketItemStatus.CANCELLED)).collect(Collectors.toList());
                        Set<String> packetInvoices = packetItems.stream().filter(packetItem -> packetItem.getPacket().getStatus().isPacked()).map(packetItem -> packetItem.getPacket().getInvoiceNumber()).collect(Collectors.toSet());
                        for (String invoice : packetInvoices) {
                            List<PacketItem> invoicePacketItems = packetItems.stream().filter(packetItem -> packetItem.getPacket().getInvoiceNumber().equalsIgnoreCase(invoice)).collect(Collectors.toList());
                            packed_quant+=invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum();
                        }
                    } else if (saleOrderAllocation.getAvailableQuantity() == 0 && saleOrderAllocation.getPackedQuantity() > 0) {
                        List<PacketItem> packetItems = saleOrderAllocation.getPacketItems().stream().filter(allocation -> !allocation.getStatus().equals(PacketItemStatus.CANCELLED)).collect(Collectors.toList());
                        Set<String> packetInvoices = packetItems.stream().filter(packetItem -> packetItem.getPacket().getStatus().isPacked()).map(packetItem -> packetItem.getPacket().getInvoiceNumber()).collect(Collectors.toSet());
                        for (String invoice : packetInvoices) {
                            List<PacketItem> invoicePacketItems = packetItems.stream().filter(packetItem -> packetItem.getPacket().getInvoiceNumber().equalsIgnoreCase(invoice)).collect(Collectors.toList());
                            packed_quant+=invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum();
                        }
                    }
				} else if (saleOrderAllocation.getStatus().equals(SaleOrderAllocationStatus.TRANSFERRED)
						&& saleOrderAllocation.getPackedQuantity() > 0) {
					List<PacketItem> packetItems = saleOrderAllocation.getPacketItems().stream()
							.filter(allocation -> !allocation.getStatus().equals(PacketItemStatus.CANCELLED))
							.collect(Collectors.toList());
					Set<String> packetInvoices = packetItems.stream()
							.filter(packetItem -> packetItem.getPacket().getStatus().isPacked())
							.map(packetItem -> packetItem.getPacket().getInvoiceNumber()).collect(Collectors.toSet());
					for (String invoice : packetInvoices) {
						List<PacketItem> invoicePacketItems = packetItems.stream().filter(
								packetItem -> packetItem.getPacket().getInvoiceNumber().equalsIgnoreCase(invoice))
								.collect(Collectors.toList());
						 packed_quant+=invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum();
					}
				}
            }
            locations.sort(Comparator.comparing(InventoryLocationDto.LocationDto::getZoneId).thenComparing(InventoryLocationDto.LocationDto::getStorageLocationId));
            productInventory.setAllocatedQuantity(productInventory.getAllocatedQuantity()-packed_quant);
            InventoryLocationDto inventoryLocationDto = new InventoryLocationDto(productInventory, locations);
            response.setMessage("Inventory locations found: " + locations.size());
            response.setStatus(true);
            response.setInventoryLocation(inventoryLocationDto);
        } else {
            response.setMessage("No Inventory locations found");
        }
        logger.info("Get Inventory locations for allocated quantity service ended "+ new Gson().toJson(response));
        return response;
    }

    @Override
    public List<Integer> findSaleOrdersWithAllTransferred() {
		List<Integer> saleOrderOutput = new ArrayList<>();
		List<SaleOrder> saleOrders = saleOrderRepository.findByAllocatedQuantityGreaterThan(0.0d);

		logger.info("Orders found: " + saleOrders.size());
		for (SaleOrder order : saleOrders) {
			if (order.getSaleOrderAllocations().isEmpty() && !order.getSaleOrderAllocations().stream().parallel()
					.noneMatch(e -> e.getStatus().equals(SaleOrderAllocationStatus.ALLOCATED))) {
				saleOrderOutput.add(order.getId());
			}
		}
		return saleOrderOutput;
	}
    
    @Override
    public List<Integer> findOutOfSyncSaleOrders() {
		List<Integer> saleOrderOutput = new ArrayList<>();
		List<SaleOrder> saleOrders = saleOrderRepository.findByAllocatedQuantityGreaterThan(0.0d);

		for (SaleOrder order : saleOrders) {
			Double allocationQuantity = order.getSaleOrderAllocations().stream()
					.filter(e -> e.getStatus().equals(SaleOrderAllocationStatus.ALLOCATED))
					.collect(Collectors.summingDouble(e -> e.getAvailableQuantity()));
			if (!allocationQuantity.equals(order.getAllocatedQuantity())) {
				saleOrderOutput.add(order.getEmsOrderItemId());
			}
		}
		return saleOrderOutput;
    }

    @Override
    public GetInventoryForAllocatedQtyResponse getInventoryAllocationForAllocatedQty(GetInventoryForAllocatedQtyRequest request) {
        logger.info("Get Inventory locations for allocated quantity service started");
        GetInventoryForAllocatedQtyResponse response = new GetInventoryForAllocatedQtyResponse();
        List<SaleOrderAllocation> saleOrderAllocations = saleOrderAllocationRepository.getForAllocatedQtyAndProduct(request.getWarehouseId(), request.getProductId());


        ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(request.getWarehouseId(), request.getProductId());
        InventoryLocationDto inventoryLocationDto = new InventoryLocationDto();
        List<InventoryLocationDto.LocationDto> locations;
        Map<String, InventoryLocationDto.LocationDto> map = new HashMap<>();
        String key;
        InventoryLocationDto.LocationDto location;
        if(!CollectionUtils.isEmpty(saleOrderAllocations)) {

            inventoryLocationDto.setAge(productInventory.getAverageAge());
            inventoryLocationDto.setProductId(productInventory.getProduct().getId());
            inventoryLocationDto.setProductMsn(productInventory.getProduct().getProductMsn());
            inventoryLocationDto.setProductName(productInventory.getProduct().getProductName());
            inventoryLocationDto.setQuantity(productInventory.getAllocatedQuantity());
            inventoryLocationDto.setWarehouseId(productInventory.getWarehouse().getId());
            inventoryLocationDto.setWarehouseName(productInventory.getWarehouse().getName());
            inventoryLocationDto.setUom(productInventory.getProduct().getUom());

            for(SaleOrderAllocation saleOrderAllocation : saleOrderAllocations) {
                key = saleOrderAllocation.getSaleOrder().getId() + "_" + saleOrderAllocation.getInboundStorage().getStorageLocation().getId();
                location = map.get(key);
                if(location == null) {
                    location = new InventoryLocationDto.LocationDto();
                    location.setQuantity(saleOrderAllocation.getAvailableQuantity());
                    location.setStorageLocationId(saleOrderAllocation.getInboundStorage().getStorageLocation().getId());
					location.setStorageLocationName(saleOrderAllocation.getInboundStorage().getStorageLocation().getName());
                    location.setZoneId(saleOrderAllocation.getInboundStorage().getStorageLocation().getZone().getId());
                    location.setZoneName(saleOrderAllocation.getInboundStorage().getStorageLocation().getZone().getName());
                    location.setEmsOrderId(saleOrderAllocation.getSaleOrder().getEmsOrderId());
                    location.setEmsOrderItemId(saleOrderAllocation.getSaleOrder().getEmsOrderItemId());
                    location.setSaleOrderId(saleOrderAllocation.getSaleOrder().getId());
                } else {
                    location.setQuantity(NumberUtil.round4(location.getQuantity()+saleOrderAllocation.getAvailableQuantity()));
                }
                map.put(key, location);
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

        logger.info("Get Inventory locations for allocated quantity service ended");
        return response;
    }

    @Transactional
	@Override
	public UpdateOrderQuantityResponse updateSaleOrderQuantity(UpdateOrderQuantityRequest request) {
		
		SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(request.getEmsOrderItemId());
		
		inventoryService.deAllocateInventory(order);
		
		order.setOrderedQuantity(request.getUpdatedOrderQuantity());
		order.setAllocatedQuantity(0.0d);
		
		upsert(order);
		
		return new UpdateOrderQuantityResponse("Order updated with quntity: " + request.getUpdatedOrderQuantity(), true, HttpStatus.OK.value(), order.getId());
	}

	@Override
	public CreateSaleOrderResponse migrateSaleOrder(@Valid CreateSaleOrderRequest request) {
        logger.info("Create Sale Order Service Started");
        CreateSaleOrderResponse response = new CreateSaleOrderResponse();

        Warehouse warehouse = warehouseService.getById(request.getFulfillmentWarehouseId());

        Product product;
        SaleOrder saleOrder;
        for(CreateSaleOrderRequest.EmsOrderItem item : request.getItems()) {
            product = prodRepo.getUniqueByProductMsn(item.getProductMsn());
            if (product == null) {
                response.setMessage("Invalid Product");
                logger.info("Invalid Product");
                return response;
            }

            saleOrder = saleOrderRepository.getSaleOrderByEmsOrderItemId(item.getEmsOrderItemId());
            if(saleOrder != null) {
                response.setMessage("Order already exist in WMS");
                logger.info("Order already exist in WMS");
                return response;
            }

            saleOrder = new SaleOrder();
            saleOrder.setEmsOrderId(request.getEmsOrderId());
            saleOrder.setEmsOrderItemId(item.getEmsOrderItemId());
            saleOrder.setWarehouse(warehouse);
            saleOrder.setProduct(product);
            saleOrder.setOrderedQuantity(NumberUtil.round4(item.getOrderedQuantity()));
            saleOrder.setRemark(item.getRemark());
            saleOrder.setItemRef(item.getItemRef());

            upsert(saleOrder);
        }

        response.setMessage("Sale Order Created with " + request.getItems().size() + " items");
        response.setStatus(true);        
        logger.info("Create Sale Order Service Ended");
        return response;
    }

	@Override
	public BaseResponse markOrderFullfilled(Integer emsOrderItemId) {
		logger.info("Received order to marked order with emsOrderItemId: " + emsOrderItemId);
		BaseResponse response = new BaseResponse();
		SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(emsOrderItemId);
		if(order == null) {
			response.setMessage("Order Not found for emsOrderitemId:" + emsOrderItemId);
			response.setCode(HttpStatus.OK.value());
			response.setStatus(true);
			logger.info("Couldn't Update sale order status to fullfilled: " + emsOrderItemId);
		}else {
			order.setStatus(SaleOrderStatus.FULFILLED);
			saleOrderRepository.save(order);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Order Marked Fullfilled Successfully");
			response.setStatus(true);
			logger.info("Updated sale order status to fullfilled successfully: " + emsOrderItemId);
		}
		return response;
	}

	@Override
	public BaseResponse markOrderOpen(Integer emsOrderItemId) {
		logger.info("Received order to marked order with emsOrderItemId: " + emsOrderItemId);
		BaseResponse response = new BaseResponse();
		SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(emsOrderItemId);
		if(order == null) {
			response.setMessage("Order Not found for emsOrderitemId:" + emsOrderItemId);
			response.setCode(HttpStatus.OK.value());
			response.setStatus(true);
			logger.info("Couldn't Update sale order status to open: " + emsOrderItemId);
		}else {
			order.setStatus(SaleOrderStatus.OPEN);
			saleOrderRepository.save(order);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Order Marked Open Successfully");
			response.setStatus(true);
			logger.info("Updated sale order status to open successfully: " + emsOrderItemId);
		}
		return response;
	}

	@Override
	public SaleOrderAllocationResponse getSaleOrderAllocations(Integer emsOrderItemId) {
		logger.info("Received order to get SaleOrderAllocations for order with emsOrderItemId: " + emsOrderItemId);
		SaleOrderAllocationResponse response = new SaleOrderAllocationResponse("Successfully found allocation for orderItemId: " + emsOrderItemId, true, HttpStatus.OK.value());
		SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(emsOrderItemId);
		if(order == null) {
			return new SaleOrderAllocationResponse("No orders found for emsOrderItemId: " + emsOrderItemId, false, HttpStatus.OK.value());
		}else {
			
			List<SaleOrderAllocation>allocations = saleOrderAllocationRepository.getSaleOrderAllocationBySaleOrderIdAndStatus(order.getId(), SaleOrderAllocationStatus.ALLOCATED);
			
			Map<String, Double> orderSaleOrderAllocationMap = allocations.stream().collect(Collectors.toMap(e -> getGroupingByKey(e.getInboundStorage().getInbound()), e -> e.getAllocatedQuantity()));
			
			for(Map.Entry<String, Double>entry: orderSaleOrderAllocationMap.entrySet()) {
				
				List<String>inboundData = Arrays.asList(entry.getKey().split("-"));
				Allocation allocation = new Allocation();
				
				allocation.setMrnId(inboundData.get(0));
				allocation.setSupplierPoId(Integer.parseInt(inboundData.get(1)));
				allocation.setSupplierPoItemId(Integer.parseInt(inboundData.get(2)));
				allocation.setQuantity(entry.getValue());
				
				response.getAllocations().add(allocation);
			}
		}
		
		logger.debug("Allocations found are: " + response);
		
		logger.info("Succesfully found: " + response.getAllocations().size() + "allocations" + "for orderItemId: " + emsOrderItemId);
		return response;
	}
		
	private String getGroupingByKey(Inbound p) {
		return p.getBatch().getRefNo() + "-" + p.getSupplierPoId() + "-" + p.getSupplierPoItemId();
	}

	@Override
	@Transactional
	public BaseResponse transferOrderInventory(Integer sourceEmsOrderItemId, Integer destinationEmsOrderItemId) {
		
		String uuid = UUID.randomUUID().toString();
		
		BaseResponse response = new BaseResponse();
		SaleOrder sourceOrder = saleOrderRepository.getSaleOrderByEmsOrderItemId(sourceEmsOrderItemId);
		
		SaleOrder destinationOrder = saleOrderRepository.getSaleOrderByEmsOrderItemId(destinationEmsOrderItemId);
		
		if(sourceOrder != null && destinationOrder != null) {
			
			if(!destinationOrder.getStatus().name().equals(SaleOrderStatus.OPEN.name())) {
				logger.warn("Someone Tried transferring inventory to a " + destinationOrder.getStatus().name() +" saleOrder. OrderItemId: " + destinationOrder.getEmsOrderItemId());
				response.setMessage("Cannot Transfer inventory to a " + destinationOrder.getStatus().name() + " order");
				response.setCode(HttpStatus.OK.value());
				response.setStatus(false);
				return response;
			}
			else if(sourceOrder.getBulkInvoiceId() != null || destinationOrder.getBulkInvoiceId() != null) {
				logger.warn("Inventory transfer for bulk invoice order attempted. SourceOrder: " + sourceEmsOrderItemId + " Destination order: " + destinationEmsOrderItemId);
				response.setMessage("Inventory transfer not allowed for Bulk Invoice Items");
				response.setCode(HttpStatus.OK.value());
				response.setStatus(false);
				return response;
			}
			
			if(sourceOrder.getWarehouse().getId() == destinationOrder.getWarehouse().getId()) {
				
				logger.info("Deallocating Inventory from emsOrderItemId: " + sourceEmsOrderItemId + " : " + uuid);
				
				saleOrderTransferInventoryService.deAllocateInventory(sourceOrder);
				
				logger.info("Inventory Successfully deallocated from emsOrderItemId: " + sourceEmsOrderItemId + " : " + uuid);
				
				Double prevAllocateQuantity = destinationOrder.getAllocatedQuantity();
				logger.debug("PrevAllocated Quantity of dest Order: " + prevAllocateQuantity + " : " + uuid);
				
				logger.info("Starting brute force allocation for emsOrderItemId: " + destinationEmsOrderItemId + " : " + uuid);
				saleOrderTransferInventoryService.allocateInventory(destinationOrder);
	
				logger.info("Successfully allocated inventory by brute force for emsOrderItemId: " + destinationEmsOrderItemId + " : " + uuid);
					
				RestTemplate restTemplate = new RestTemplate();
	
				logger.info("Updating ems API for source emsOrderItemId: " + sourceEmsOrderItemId + "with quantity: " + sourceOrder.getAllocatedQuantity() + " : " + uuid);
	
				EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(sourceOrder.getEmsOrderItemId(), sourceOrder.getAllocatedQuantity(),"WMS");
				
				ResponseEntity<BaseResponse> emsResponse = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRequest, BaseResponse.class);
				
				//Update EMS with allocated Quantity.
				logger.info("Updating ems API for destination emsOrderItemId: " + destinationEmsOrderItemId + "with quantity: " + destinationOrder.getAllocatedQuantity());
				EMSPackableQuantityRequest emsRequest1 = new EMSPackableQuantityRequest(destinationOrder.getEmsOrderItemId(),destinationOrder.getAllocatedQuantity(),"WMS");
				
				ResponseEntity<BaseResponse> emsResponse1 = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRequest1, BaseResponse.class);
				
				if(emsResponse1.getBody().getStatus() && emsResponse.getBody().getStatus()) {
	
					// Call sales ops API				
					//SalesOpsDemandRequest salesOpsRequest = new SalesOpsDemandRequest(destinationOrder.getItemRef(), destinationOrder.getAllocatedQuantity(), false);
					InventoryUpdateRequest salesOpsRequest = new InventoryUpdateRequest(destinationOrder.getItemRef(), destinationOrder.getAllocatedQuantity(), false, PublishSystemType.WMS);
					
					logger.info("Calling Sales Ops API to update demands quantity with request: " + salesOpsRequest);
	
					ResponseEntity<BaseResponse> salesOpsResponse = null;
					HttpHeaders headers = new HttpHeaders();
	
					headers.add("Authorization", Constants.SALES_OPS_AUTH_TOKEN);
					try {
						// salesOpsResponse = restTemplate.exchange(Constants.SALES_OPS_DEMAND_API, HttpMethod.POST, new HttpEntity<SalesOpsDemandRequest>(salesOpsRequest, headers), BaseResponse.class);
						salesOpsResponse = restTemplate.exchange(Constants.SALES_OPS_DEMAND_API, HttpMethod.POST, new HttpEntity<InventoryUpdateRequest>(salesOpsRequest, headers), BaseResponse.class);
	
					} catch (Exception e) {
						logger.error("Error occured in updating sales Ops API:", e);
					}
	
					if (salesOpsResponse == null || !salesOpsResponse.getBody().getStatus()) {
						logger.warn("Unusual response from sales Ops API." + salesOpsResponse);
						EMSPackableQuantityRequest emsRollbackRequest = new EMSPackableQuantityRequest(destinationOrder.getEmsOrderItemId(), prevAllocateQuantity , "WMS");
						restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRollbackRequest, BaseResponse.class);
						throw new WMSException("Cannot update through sales ops API. Rolling back stuff");
					}
				}
				else {
					throw new WMSException("Cannot Update EMS API");
				}
				response.setCode(HttpStatus.OK.value());
				response.setMessage("Allocation transfer Successfull");
				response.setStatus(true);
	            logger.info("Inventory successfully transferred from : " + sourceEmsOrderItemId + " to :" + destinationEmsOrderItemId);
			}
			else {
				response.setMessage("Inventory transfer allowed only for same warehouses and same productMSN.");
				response.setCode(HttpStatus.OK.value());
				response.setStatus(false);
			}
		}
		else {
			response.setMessage("Order Not found for emsOrderitemId:" + sourceEmsOrderItemId + " or " + destinationEmsOrderItemId);
			response.setCode(HttpStatus.OK.value());
			response.setStatus(false);
			logger.info("Couldn't transfer inventory from emsOrderItemId: " + sourceEmsOrderItemId + " :to" + destinationEmsOrderItemId);
		}		
		return response;
	}

	@Override
	@Transactional
	public BaseResponse updatePackedQuantity(Integer emsOrderItemId, Double quantity) {
		logger.info("Received order to update packed quantity of order with emsOrderItemId: " + emsOrderItemId);
		BaseResponse response = new BaseResponse();
		SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(emsOrderItemId);
		if(order == null) {
			response.setMessage("Order Not found for emsOrderitemId:" + emsOrderItemId);
			response.setCode(HttpStatus.OK.value());
			response.setStatus(true);
			logger.info("Couldn't Update packed order quantity: " + emsOrderItemId);
		}else {
			order.setPackedQuantity(quantity);
			saleOrderRepository.save(order);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Packed Quantity updated Successfully");
			response.setStatus(true);
			logger.info("Packed Quantity updated Successfully for sale order item: " + emsOrderItemId);
		}
		return response;
	}
	
	@Override
	@Transactional
	public BaseResponse deAllocateInventory(Integer sourceEmsOrderItemId) {
		BaseResponse response = new BaseResponse();
		SaleOrder sourceOrder = saleOrderRepository.getSaleOrderByEmsOrderItemId(sourceEmsOrderItemId);
		
		
		if(sourceOrder != null) {
			logger.info("Deallocating Inventory from emsOrderItemId: " + sourceEmsOrderItemId);
			forceFulldeAllocateInventory(sourceOrder);
			
			sourceOrder.setAllocatedQuantity(0.0d);
			
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Deallocation Successfull for ems OrderItemId: " + sourceEmsOrderItemId);
			response.setStatus(true);
            logger.info("Inventory successfully deallocated from : " + sourceEmsOrderItemId);
		}else {
			response.setMessage("Order Not found for emsOrderitemId:" + sourceEmsOrderItemId);
			response.setCode(HttpStatus.OK.value());
			response.setStatus(false);
			logger.info("Couldn't deallocate invetory from emsOrderItemId: " + sourceEmsOrderItemId);
		}		
		return response;
	}

	@Transactional
	public void forceFulldeAllocateInventory(SaleOrder saleOrder) {
		logger.info("Inventory de-allocation service started");
		ProductInventory productInventory = productInventoryService
				.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
		if (productInventory == null) {
			logger.info("Inventory not allocated for sale order id: " + saleOrder.getId());
		} else {
			List<SaleOrderAllocation> allocations = saleOrderAllocationRepository.getSaleOrderAllocationBySaleOrderIdAndStatus(saleOrder.getId(), SaleOrderAllocationStatus.ALLOCATED);
			InboundStorage inboundStorage;
			for (SaleOrderAllocation allocation : allocations) {
				inboundStorage = allocation.getInboundStorage();
				inboundStorage.setAllocatedQuantity(
						NumberUtil.round4(inboundStorage.getAllocatedQuantity() - allocation.getAvailableQuantity()));
				inboundStorage.setAvailableQuantity(
						NumberUtil.round4(inboundStorage.getAvailableQuantity() + allocation.getAvailableQuantity()));
				inboundStorageService.upsert(inboundStorage);

				allocation.setAllocatedQuantity(allocation.getAllocatedQuantity() - allocation.getAvailableQuantity());
				allocation.setAvailableQuantity(0.0d);
				upsertAllocation(allocation);
			}

			SaleOrderAllocationHistory allocationHistory = new SaleOrderAllocationHistory();
			allocationHistory.setAction("Cancelled");
			allocationHistory.setQuantity(0d);
			allocationHistory.setSaleOrder(saleOrder);
			upsertAllocationHistory(allocationHistory);
		}
		logger.info("Inventory de-allocation service ended");
	}

    @Override
	@Transactional
	public void updateSaleOrderAllocationShippedQuantity(Map<Integer,Double> shippedQuantityMap) {
        for (Map.Entry<Integer,Double> entry : shippedQuantityMap.entrySet()) {
            Optional<SaleOrderAllocation> optionalSaleOrderAllocation = saleOrderAllocationRepository.findById(entry.getKey());
            if (optionalSaleOrderAllocation.isPresent()) {
                SaleOrderAllocation saleOrderAllocation = optionalSaleOrderAllocation.get();
                //saleOrderAllocation.setShippedQuantity( saleOrderAllocation.getShippedQuantity() + entry.getValue());
                saleOrderAllocationRepository.save(saleOrderAllocation);
            }
        }
    }

	@Override
	@Transactional
	public SaleOrderDeallocationResponse deAllocateByItemRef(String itemRef) throws Exception {
		
		if(itemRef == null || itemRef.equals("")) {
			return new SaleOrderDeallocationResponse("deallocation can't happen because itemRef : "+itemRef, false, 500);
		}
		
		SaleOrder saleOrder = saleOrderRepository.findByItemRef(itemRef);
		if(saleOrder == null) {
			return new SaleOrderDeallocationResponse("deallocation can't happen because sale order doesn't exists of itemRef : "+itemRef, false, 500);
		}
		Double beforeAllocatedQ = saleOrder.getAllocatedQuantity();

		String uuid = UUID.randomUUID().toString();
		
		logger.info("Inventory de-allocation service started for sale order with itemRef : "+itemRef  + " : " + uuid);
		
		ProductInventory productInventory = productInventoryService
				.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
		if (productInventory == null || productInventory.getAllocatedQuantity() <= 0) {
			logger.info("Inventory not allocated for sale order id: " + saleOrder.getId() + " of  itemRef :"+itemRef+" : " + uuid);
			return new SaleOrderDeallocationResponse("deallocation can't happen because Inventory not allocated for sale order of  itemRef : "+itemRef, false, 500);
		} else {
			List<SaleOrderAllocation> allocations = saleOrderAllocationRepository.getSaleOrderAllocationBySaleOrderIdAndStatus(saleOrder.getId(), SaleOrderAllocationStatus.ALLOCATED);

			logger.info("Got " + allocations.size() + " allocation for sale order with itemRef: " + itemRef + " : " + uuid);

			if(!allocations.isEmpty()) {
				logger.debug("Setting ProductInventory Allocated Quantity: " + (NumberUtil.round4(productInventory.getAllocatedQuantity() - saleOrder.getAllocatedQuantity())) + " : " + uuid);
				productInventory.setAllocatedQuantity(
						NumberUtil.round4(productInventory.getAllocatedQuantity() - saleOrder.getAllocatedQuantity()));
				
				logger.debug("Setting ProductInventory Available Quantity: " + (
						NumberUtil.round4(productInventory.getAvailableQuantity() + saleOrder.getAllocatedQuantity())) + " : " + uuid);
				productInventory.setAvailableQuantity(
						NumberUtil.round4(productInventory.getAvailableQuantity() + saleOrder.getAllocatedQuantity()));
				
				logger.trace("Updating product inventory with productinventory id: "  + productInventory.getId() +  " : " + uuid);
				productInventoryService.upsert(productInventory);

				Product product = productInventory.getProduct();
				
				logger.debug("Setting Product Allocated Quantity: " + (
						NumberUtil.round4(product.getAllocatedQuantity() - saleOrder.getAllocatedQuantity())) + " : " + uuid);
				product.setAllocatedQuantity(
						NumberUtil.round4(product.getAllocatedQuantity() - saleOrder.getAllocatedQuantity()));
				
				logger.debug("Setting Product Available Quantity: " + (
						NumberUtil.round4(product.getAvailableQuantity() + saleOrder.getAllocatedQuantity())) + " : " + uuid);
				product.setAvailableQuantity(
						NumberUtil.round4(product.getAvailableQuantity() + saleOrder.getAllocatedQuantity()));
				
				logger.trace("Updating Product Entry for product: " + product.getId() + " : " + uuid);
				productService.upsert(product);

				InboundStorage inboundStorage;
				for (SaleOrderAllocation allocation : allocations) {
					if(allocation.getAvailableQuantity() > 0) {
						inboundStorage = allocation.getInboundStorage();
						
						logger.info("Setting inbound storage available : " + inboundStorage.getId() + "----" + (
								NumberUtil.round4(inboundStorage.getAllocatedQuantity() - allocation.getAvailableQuantity())) + " : " + uuid);
						
						inboundStorage.setAllocatedQuantity(
								NumberUtil.round4(inboundStorage.getAllocatedQuantity() - allocation.getAvailableQuantity()));
						
						logger.info("Setting inbound storage available : " + inboundStorage.getId() + "----" + (NumberUtil
								.round4(inboundStorage.getAvailableQuantity() + allocation.getAvailableQuantity())) + " : " + uuid);
						
						inboundStorage.setAvailableQuantity(
								NumberUtil.round4(inboundStorage.getAvailableQuantity() + allocation.getAvailableQuantity()));
						inboundStorageService.upsert(inboundStorage);

						allocation.setStatus(SaleOrderAllocationStatus.CANCELLED);
						allocation.setAvailableQuantity(0.0d);
						upsertAllocation(allocation);
						
						SaleOrderAllocationHistory allocationHistory = new SaleOrderAllocationHistory();
						allocationHistory.setAction("CANCELLED");
						allocationHistory.setQuantity(0d);
						allocationHistory.setSaleOrder(saleOrder);
						upsertAllocationHistory(allocationHistory);
					}else {
						logger.warn("Invalid or Packed sale order allocation for saleOrder  with itemRef: " + itemRef);
					}
				}
				logger.info("Setting saleOrder of itemRef: " + itemRef + " allocated quantity to 0" + " : " + uuid);
				saleOrder.setAllocatedQuantity(0.0d);
				saleOrderRepository.save(saleOrder);
				
			}else {
				logger.warn("Product Inventory Found but no allocations for saleOrder: " +saleOrder.getEmsOrderItemId());
			}
		}
		logger.info("Inventory de-allocation service ended  of saleorder with item ref :"+itemRef + " : " + uuid);
		
		RestTemplate restTemplate = new RestTemplate();

		logger.info("Updating ems API for saleorder with itemRef : " + itemRef + "with quantity: " + saleOrder.getAllocatedQuantity() + " : " + uuid);

		EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(saleOrder.getEmsOrderItemId(),
				saleOrder.getAllocatedQuantity(), "WMS");
		
		logger.info("before api hit to update packable quantity on ems request :"+gson.toJson(emsRequest));
		ResponseEntity<BaseResponse> emsResponse = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API,
				emsRequest, BaseResponse.class);
		logger.info("after  api hit to update packable quantity on ems response :"+gson.toJson(emsResponse));
		SaleOrderDeallocationResponse saleOrderDeallocationResponse = new SaleOrderDeallocationResponse();
		if(emsResponse.getBody().getStatus()) {
			saleOrderDeallocationResponse.setCode(200);
			saleOrderDeallocationResponse.setQuantity(beforeAllocatedQ);
			saleOrderDeallocationResponse.setMessage("inventory successfully deallocated of salorder with itemRef :"+itemRef);
			saleOrderDeallocationResponse.setStatus(true);
		}else {
			throw new Exception("unable to update packable quantity ems side for itemRef "+itemRef);
		}
		 return saleOrderDeallocationResponse;
	}

	@Override
	@Transactional
	public List<CreateSaleOrderResponse> bulkCreateSaleOrder(@Valid List<CreateSaleOrderRequest> requests) {
		List<CreateSaleOrderResponse> bulkSaleOrderResponse = new ArrayList<>();
		for(CreateSaleOrderRequest request: requests) {
			CreateSaleOrderResponse response = createSaleOrder(request);
			bulkSaleOrderResponse.add(response);
		}
		return bulkSaleOrderResponse;
	}

	@Override
	@Transactional
	public BaseResponse buyersCreateOrder(@Valid BuyersCreateSaleOrderRequest request) {
		BaseResponse response = new BaseResponse("Order Successfully created", true, HttpStatus.OK.value());
		
		for (EmsOrderItem orderItem : request.getItems()) {
			BulkInvoicingSaleOrder order = bulkInvoicingSaleOrderRepository.findByItemRefAndStatus(orderItem.getItemRef(), BulkInvoiceStatus.CREATED);
			if (order == null) {
				order = new BulkInvoicingSaleOrder();
			}else {
				logger.info("Order with item ref: " + orderItem.getItemRef() + " already exists");
				continue;
			}
			
			order.setBulkInvoiceId(request.getBulkInvoiceId());
			
			order.setBuyerOrderId(request.getOrderId());
			
			order.setItemRef(orderItem.getItemRef());
			
			order.setProductMsn(orderItem.getProductMsn());
			
			order.setOrderedQuantity(orderItem.getOrderedQuantity());
			
			order.setWarehouseId(request.getFulfillmentWarehouseId());
			
			order.setStatus(BulkInvoiceStatus.CREATED);
			
			order.setUniqueblockid(request.getUniqueblockid());
			
			bulkInvoicingSaleOrderRepository.save(order);
		}
		
		return response;
	}

	@Override
	@Transactional
	public CheckDeleteSaleOrderEligibilityResponse checkDeleteSaleOrderEligibility(Integer emsOrderId) {
		List<SaleOrder> orderItems = saleOrderRepository.getSaleOrderByEmsOrderId(emsOrderId);
		
		for(SaleOrder order : orderItems) {

			if (order == null || order.getStatus().equals(SaleOrderStatus.CANCELLED)
					|| order.getStatus().equals(SaleOrderStatus.FULFILLED) || order.getPackedQuantity() > 0) {
				return new CheckDeleteSaleOrderEligibilityResponse(
						"Cannot Cancel Order because item: " + order.getItemRef() + " is not eligible", false,
						HttpStatus.OK.value());
			}
        }
		return new CheckDeleteSaleOrderEligibilityResponse(
				"Order " + emsOrderId + "is eligible for cancellation", true,
				HttpStatus.OK.value());
	}

	@Override
	public BaseResponse updatePlantId(Integer emsOrderItemId, Integer plantId) {
		
		logger.info("Update Plant Id: " + plantId + " against ems order item id: " + emsOrderItemId);
		
		Plant plant = plantRepo.findByBuyersPlantId(plantId).orElse(new Plant(plantId));
		
		SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(emsOrderItemId);
		
		order.setPlant(plant);
		
		saleOrderRepository.save(order);
		
		logger.info("Updated Plant Id: " + plantId + " against ems order item id: " + emsOrderItemId);
		
		return new BaseResponse("Plant Id updated successfully", true, HttpStatus.OK.value());
	}

	@Override
	@Transactional
	public UpdateFulFillmentWarehouseResponse updateFulFillmentWarehouse(Integer emsOrderItemId, Integer warehouseId) {
		SaleOrder order = saleOrderRepository.getSaleOrderByEmsOrderItemId(emsOrderItemId);
		if(order.getPackedQuantity()!=0) {
			logger.info("Cannot change saleorder :: "+order.getId());
			return new UpdateFulFillmentWarehouseResponse("Some Items are already in packed state. So update warehouse is not allowed.",false,200,order.getId());
		}
		if (!warehouseId.equals(order.getWarehouse().getId())) {
            if(!order.getStnAssoication()) {
                inventoryService.deAllocateInventory(order);
            } else {
                logger.info("sale order id [{}] and emsOrderItemId [{}] is associated with stn so ignoring deallocation logic",order.getId(),emsOrderItemId );
            }
			Warehouse newWarehouse = warehouseService.getById(warehouseId);
			order.setWarehouse(newWarehouse);
			order.setAllocatedQuantity(0.0d);
			upsert(order);
			
			//if (order.getOrderType().equals(OrderType.BULK_INVOICING)) {
			if (order.getBulkInvoiceId() != null) {
				try {
					InventoryAllocationRequest allocationRequest = new InventoryAllocationRequest(order.getId(), null);
					producer.sendMessage(ALLOCATION_QUEUE, JsonUtil.toJson(allocationRequest));
				} catch (Exception e) {
	        		logger.error("Error occured in pushing to queue: " + emsOrderItemId, e);
				}
			}
			//else if(order.getOrderType().equals(OrderType.NEW)) {
			else {
				try {
				    if(!order.getStnAssoication()) {
                        InventoryAllocationRequest allocationRequest = new InventoryAllocationRequest(null, order.getProduct().getId());
                        producer.sendMessage(NEW_ALLOCATION_QUEUE, JsonUtil.toJson(allocationRequest));
                    } else {
                        logger.info("sale order id [{}] and emsOrderItemId [{}] is associated with stn so ignoring allocation logic",order.getId(),emsOrderItemId );
                    }
				} catch (Exception e) {
	        		logger.error("Error occured in pushing to queue: " + emsOrderItemId, e);
				}
			}
			
			return new UpdateFulFillmentWarehouseResponse("Sale Order Warehouse Updated", true, HttpStatus.OK.value(), order.getId());
		} else {
			return new UpdateFulFillmentWarehouseResponse("Sale Order Warehouse already Updated", true, HttpStatus.OK.value(), order.getId());
		}
	}

	@Override
	public GetInventoryForPackedQtyResponse getProductInventoryPackedInfo(
			GetInventoryForPackedQtyRequest request) {
		logger.info("Get Inventory locations for Packed quantity service started"+ new Gson().toJson(request));
        Double packed_quant=0d;
        List<SaleOrderAllocation> saleOrderAllocations = new ArrayList<>();
        if(request.getZoneId() != null) {
			if(request.getBinId() != null) {
				saleOrderAllocations = saleOrderAllocationRepository.getForAllocatedQtyAndProductAndZoneAndBinTransferred(request.getWarehouseId(), request.getProductId(), request.getZoneId(), request.getBinId());
			}else {
				saleOrderAllocations = saleOrderAllocationRepository.getForAllocatedQtyAndProductAndZoneTransferred(request.getWarehouseId(), request.getProductId(), request.getZoneId());
			}
		}else {
			saleOrderAllocations = saleOrderAllocationRepository.getForAllocatedQtyAndProductTransferred(request.getWarehouseId(), request.getProductId());
		}
        
        ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(request.getWarehouseId(), request.getProductId());
        GetInventoryForPackedQtyResponse response = new GetInventoryForPackedQtyResponse();
        if (!CollectionUtils.isEmpty(saleOrderAllocations)) {
            List<InventoryLocationDto.LocationDto> locations = new ArrayList<>();
            for (SaleOrderAllocation saleOrderAllocation : saleOrderAllocations) {
            	if(saleOrderAllocation.getStatus().equals(SaleOrderAllocationStatus.ALLOCATED)) {
                     if (saleOrderAllocation.getAvailableQuantity() > 0 && saleOrderAllocation.getPackedQuantity() > 0) {
                        List<PacketItem> packetItems = saleOrderAllocation.getPacketItems().stream().filter(allocation -> !allocation.getStatus().equals(PacketItemStatus.CANCELLED)).collect(Collectors.toList());
                        Set<String> packetInvoices = packetItems.stream().filter(packetItem -> packetItem.getPacket().getStatus().isPacked()).map(packetItem -> packetItem.getPacket().getInvoiceNumber()).collect(Collectors.toSet());
                        for (String invoice : packetInvoices) {
                            List<PacketItem> invoicePacketItems = packetItems.stream().filter(packetItem -> packetItem.getPacket().getInvoiceNumber().equalsIgnoreCase(invoice)).collect(Collectors.toList());
                            locations.add(new InventoryLocationDto.LocationDto(saleOrderAllocation, 0d, invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum(), invoice));
                            packed_quant+=invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum();
                        }
                    } else if (saleOrderAllocation.getAvailableQuantity() == 0 && saleOrderAllocation.getPackedQuantity() > 0) {
                        List<PacketItem> packetItems = saleOrderAllocation.getPacketItems().stream().filter(allocation -> !allocation.getStatus().equals(PacketItemStatus.CANCELLED)).collect(Collectors.toList());
                        Set<String> packetInvoices = packetItems.stream().filter(packetItem -> packetItem.getPacket().getStatus().isPacked()).map(packetItem -> packetItem.getPacket().getInvoiceNumber()).collect(Collectors.toSet());
                        for (String invoice : packetInvoices) {
                            List<PacketItem> invoicePacketItems = packetItems.stream().filter(packetItem -> packetItem.getPacket().getInvoiceNumber().equalsIgnoreCase(invoice)).collect(Collectors.toList());
                            locations.add(new InventoryLocationDto.LocationDto(saleOrderAllocation, 0d, invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum(), invoice));
                            packed_quant+=invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum();
                        }
                    }
				} else if (saleOrderAllocation.getStatus().equals(SaleOrderAllocationStatus.TRANSFERRED)
						&& saleOrderAllocation.getPackedQuantity() > 0) {
					List<PacketItem> packetItems = saleOrderAllocation.getPacketItems().stream()
							.filter(allocation -> !allocation.getStatus().equals(PacketItemStatus.CANCELLED))
							.collect(Collectors.toList());
					Set<String> packetInvoices = packetItems.stream()
							.filter(packetItem -> packetItem.getPacket().getStatus().isPacked())
							.map(packetItem -> packetItem.getPacket().getInvoiceNumber()).collect(Collectors.toSet());
					for (String invoice : packetInvoices) {
						List<PacketItem> invoicePacketItems = packetItems.stream().filter(
								packetItem -> packetItem.getPacket().getInvoiceNumber().equalsIgnoreCase(invoice))
								.collect(Collectors.toList());
						locations.add(new InventoryLocationDto.LocationDto(saleOrderAllocation, 0d,
								invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum(), invoice));
						packed_quant+=invoicePacketItems.stream().mapToDouble(PacketItem::getQuantity).sum();
					}
				}
            }
            locations.sort(Comparator.comparing(InventoryLocationDto.LocationDto::getZoneId).thenComparing(InventoryLocationDto.LocationDto::getStorageLocationId));
            productInventory.setAllocatedQuantity(packed_quant);
            InventoryLocationDto inventoryLocationDto = new InventoryLocationDto(productInventory, locations);
            response.setMessage("Packed Inventory locations found: " + locations.size());
            response.setStatus(true);
            response.setInventoryLocation(inventoryLocationDto);
        } else {
            response.setMessage("No Inventory locations found");
        }
        logger.info("Get Inventory locations for packed quantity service ended "+ new Gson().toJson(response));
        return response;
		
	}

	public boolean createSaleOrderFromKafka(SalesOpsOrderDTO salesOpsOrderDTO) {
		
        logger.info("Checking order details :: " + new Gson().toJson(salesOpsOrderDTO));
        if (salesOpsOrderDTO.getOrderDetails() == null) {
            throw new WMSException("Order details not found for new order");
        }
        
        if (orderValidationService.validateOrder(salesOpsOrderDTO)) {
        	
        	CreateSaleOrderRequest request = this.convertSalesOpsOrdertDtoToOrderCreateRequest(salesOpsOrderDTO.getOrderDetails(), salesOpsOrderDTO.getItemDetails());
        	logger.info("Create sale order request :: " + new Gson().toJson(request));
        	CreateSaleOrderResponse response = this.createSaleOrder(request);
        	
            if(!response.getSaleOrderIds().isEmpty() && OrderType.valueOf(request.getOrderType()).equals(OrderType.BULK_INVOICING)) {
	            try {
	                // Push order to bulk inventory allocation queue
	         		for(Integer saleOrderId: response.getSaleOrderIds()) {
	         			InventoryAllocationRequest input = new InventoryAllocationRequest(saleOrderId, null);
	                    producer.sendMessage(ALLOCATION_QUEUE, JsonUtil.toJson(input));
	         		}
	         	}
	            catch(Exception e) {
	         		logger.error("Error occured while pushing order to bulk inventory allocation queue :: " + request.getEmsOrderId(), e);
	           }
            }
            else if(!response.getSaleOrderIds().isEmpty() && OrderType.valueOf(request.getOrderType()).equals(OrderType.NEW)) {
            	try {
            		// Push order to new inventory allocation queue
            		for(Integer saleOrderId: response.getSaleOrderIds()) {
            			InventoryAllocationRequest input = new InventoryAllocationRequest(saleOrderId, null);
            			producer.sendMessage(NEW_ALLOCATION_QUEUE, JsonUtil.toJson(input));
            		}
            	}
            	catch(Exception e) {
            		logger.error("Error occured while pushing order to new inventory allocation queue :: " + request.getEmsOrderId(), e);
            	}
            }
        }
        
        return true;
	}
	
	public CreateSaleOrderRequest convertSalesOpsOrdertDtoToOrderCreateRequest(SalesOpsOrderDetailsDTO orderDetailsDTO, List<SalesOpsItemDetailsDTO> salesOpsItemDetailsList) {
		
		CreateSaleOrderRequest request = new CreateSaleOrderRequest();
		
		if(orderDetailsDTO.getEmsOrderId() != null) {
			request.setEmsOrderId(orderDetailsDTO.getEmsOrderId());
        }
		
		if(orderDetailsDTO.getOrderRef() != null) {
			request.setOrderRef(orderDetailsDTO.getOrderRef());
        }
		
		if(orderDetailsDTO.getBatchRef() != null) {
			request.setBatchRef(orderDetailsDTO.getBatchRef());
        }
		
		if(orderDetailsDTO.getBulkInvoiceId() != null) {
			request.setBulkInvoiceId(orderDetailsDTO.getBulkInvoiceId());
        }
		
		if(orderDetailsDTO.getInventoryId() != null) {
			request.setInventory_id(orderDetailsDTO.getInventoryId());
        }
		
		if(orderDetailsDTO.getFulfillmentWarehouseId() != null) {
			request.setFulfillmentWarehouseId(orderDetailsDTO.getFulfillmentWarehouseId());
        }
		
		if(orderDetailsDTO.getPlantId() != null) {
			request.setPlantId(orderDetailsDTO.getPlantId());
		}
	 
		if(orderDetailsDTO.getCountryISONumber() != null) {
			request.setCountryId(orderDetailsDTO.getCountryISONumber());
		}

		if(orderDetailsDTO.getOrderType() != null) {
			request.setOrderType(orderDetailsDTO.getOrderType());
		}
		 
		List<EmsOrderItem> itemsList = new ArrayList<>();
		for(SalesOpsItemDetailsDTO item : salesOpsItemDetailsList) {
			
			EmsOrderItem emsOrderItem = new EmsOrderItem();
			
			if(item.getEmsOrderItemId() != null) {
				emsOrderItem.setEmsOrderItemId(item.getEmsOrderItemId());
	        }
			if(item.getItemRef() != null) {
				emsOrderItem.setItemRef(item.getItemRef());
			}
			if(item.getOrderedQuantity() != null) {
				emsOrderItem.setOrderedQuantity(NumberUtil.round4(Double.valueOf(item.getOrderedQuantity())));
			}
			if(item.getProductMsn() != null) {
				emsOrderItem.setProductMsn(item.getProductMsn());
			}
			if(item.getRemark() != null) {
				emsOrderItem.setRemark(item.getRemark());
			}
			if(item.getIsCloned() != null) {
				emsOrderItem.setCloned(item.getIsCloned());
			}
			if(item.getCloneOf() != null) {
				emsOrderItem.setCloneOf(item.getCloneOf());
			}
			
			itemsList.add(emsOrderItem);
		}
		request.setItems(itemsList);
		
		return request;
	}

	
	@Override
	public BaseResponse repushSaleOrderFailedToPublishInKafkaTopic(RepushSaleOrderRequest request) {
		
		logger.info("Checking saleorder ::" + request.getItemRef());
		
		if (request.getItemRef() == null || request.getItemRef().equals("")) {
			logger.info("SaleOrder id is null");
		} 
		else {
			
			SaleOrder saleOrder = this.getByItemRef(request.getItemRef());

			if (saleOrder == null) {
				logger.info("Invalid SaleOrder.!!! :: [" + request.getItemRef() +"]");
				return new BaseResponse("Invalid Saleorder.", false, HttpStatus.NOT_FOUND.value());
			}
			else {
				
				boolean isInventory = true;
				if (saleOrder.getAllocatedQuantity() == 0) {
					isInventory = false;
				}
				logger.info(String.format("Republish message for salorder :: %s ", saleOrder.getId()));
				kafkaPublisherInventoryUpdate.sendRequest(new InventoryUpdateRequest(saleOrder.getItemRef(), saleOrder.getAllocatedQuantity(), isInventory, PublishSystemType.WMS));
			}
		}
		
		return new BaseResponse("Saleorder publish to kafka topic successfully.", true, HttpStatus.OK.value());
	}
}

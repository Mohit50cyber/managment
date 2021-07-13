package com.moglix.wms.controller;

import com.google.gson.Gson;
import com.moglix.wms.api.request.*;
import com.moglix.wms.api.response.*;
import com.moglix.wms.constants.OrderType;
import com.moglix.wms.producer.FifoProducer;
import com.moglix.wms.queueModel.InventoryAllocationRequest;
import com.moglix.wms.service.ISaleOrderService;
import com.moglix.wms.util.JsonUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

/**
 * @author pankaj on 6/5/19
 */
@RestController
@RequestMapping("/api/saleOrder/")
public class SaleOrderController {

    private Logger logger = LogManager.getLogger(SaleOrderController.class);

    @Autowired
    @Qualifier("saleOrderService")
    private ISaleOrderService saleOrderService;
    
    @Value("${queue.allocation}")
    private String ALLOCATION_QUEUE;
    
    @Value("${queue.allocation.new}")
    private String NEW_ALLOCATION_QUEUE;
    
    @Autowired
    private FifoProducer producer;

    @GetMapping("ping")
    public String ping() {
        return "Welcome to Sale Order Controller";
    }

    @PostMapping("create")
    public CreateSaleOrderResponse create(@Valid @RequestBody CreateSaleOrderRequest request) {
        
    	logger.info("Request received to create sale order: " + new Gson().toJson(request));
    	
        CreateSaleOrderResponse response = saleOrderService.createSaleOrder(request);
        
        if(!response.getSaleOrderIds().isEmpty() && OrderType.valueOf(request.getOrderType()).equals(OrderType.BULK_INVOICING)) {
        	try {
                // Push order to bulk inventory allocation queue
        		for(Integer saleOrderId: response.getSaleOrderIds()) {
        			InventoryAllocationRequest input = new InventoryAllocationRequest(saleOrderId, null);
                    producer.sendMessage(ALLOCATION_QUEUE, JsonUtil.toJson(input));
        		}
        	}catch(Exception e) {
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
        	}catch(Exception e) {
        		logger.error("Error occured while pushing order to new inventory allocation queue :: " + request.getEmsOrderId(), e);
        	}
        }
        
        return response;
    }
    
    @PostMapping("create/bulkinvoice")
    public BaseResponse buyersCreateOrder(@Valid @RequestBody BuyersCreateSaleOrderRequest request) {
        logger.info("Request received to create sale order from buyers: " + request.toString());
        
        return saleOrderService.buyersCreateOrder(request);
    }
    
    @PostMapping("bulkCreate")
    public List<CreateSaleOrderResponse> bulkCreate(@Valid @RequestBody List<CreateSaleOrderRequest> request) {
        
    	logger.info("Request received in bulkCreate() to create sale order: " + request.toString());
        logger.info("Request received in bulkCreate() to create sale order: " + new Gson().toJson(request));
        
        List<CreateSaleOrderResponse> responses = saleOrderService.bulkCreateSaleOrder(request);
        
        if(!responses.isEmpty()) {
        	int currentPosition = 0;
        	for(CreateSaleOrderResponse response: responses) {
        		if(!response.getSaleOrderIds().isEmpty() && OrderType.valueOf(request.get(currentPosition).getOrderType()).equals(OrderType.BULK_INVOICING)) {
        			try {
                        //push order to inventory allocation queue
                		for(Integer saleOrderId: response.getSaleOrderIds()) {
                			InventoryAllocationRequest input = new InventoryAllocationRequest(saleOrderId, null);
                            producer.sendMessage(ALLOCATION_QUEUE, JsonUtil.toJson(input));
                		}
                	}catch(Exception e) {
                		logger.error("Error occured in pushing order to bulk inventory allocation queue :: " + request.get(currentPosition).getEmsOrderId(), e);
                	}
        		}
        		else if(!response.getSaleOrderIds().isEmpty() && OrderType.valueOf(request.get(currentPosition).getOrderType()).equals(OrderType.NEW)) {
                	try {
                		 // Push order to new inventory allocation queue
                		for(Integer saleOrderId: response.getSaleOrderIds()) {
                			InventoryAllocationRequest input = new InventoryAllocationRequest(saleOrderId, null);
                            producer.sendMessage(NEW_ALLOCATION_QUEUE, JsonUtil.toJson(input));
                		}
                	}catch(Exception e) {
                		logger.error("Error occured while pushing order to new inventory allocation queue :: " + request.get(currentPosition).getEmsOrderId(), e);
                	}
                }
        		currentPosition++;
        	}
        }
        
        return responses;
    }
    
    @PostMapping("migrate")
    public CreateSaleOrderResponse migrate(@Valid @RequestBody CreateSaleOrderRequest request) {
        logger.info("Request received to migrate sale order: " + request.toString());
        return saleOrderService.migrateSaleOrder(request);
    }
    
    @GetMapping("/markOrderFullfilled/{emsOrderItemId}")
    public BaseResponse markStatusFullfilled(@PathVariable("emsOrderItemId") Integer emsOrderItemId) {
        logger.info("Request received to set fullfilled order: " + emsOrderItemId);
        return saleOrderService.markOrderFullfilled(emsOrderItemId);
    }
    
    @GetMapping("/markOrderOpen/{emsOrderItemId}")
    public BaseResponse markStatusOpen(@PathVariable("emsOrderItemId") Integer emsOrderItemId) {
        logger.info("Request received to set fullfilled order: " + emsOrderItemId);
        return saleOrderService.markOrderOpen(emsOrderItemId);
    }

    @PostMapping("checkAvailability")
    public CheckAvailableQtyResponse checkAvailability(@Valid @RequestBody CheckAvailableQtyRequest request) {
        logger.info("Request received to check available qty for item:" + request.toString());
        return saleOrderService.checkAvailability(request);
    }

    @PostMapping("cancel")
    public DeleteSaleOrderItemResponse delete(@Valid @RequestBody DeleteSaleOrderItemRequest request) {
        logger.info("Request received to delete sale order items: " + request.toString());
        return saleOrderService.deleteSaleOrderItem(request);
    }
    
    @GetMapping("cancel/checkEligibility/{emsOrderId}")
    public CheckDeleteSaleOrderEligibilityResponse checkDeleteSaleOrderEligibility(@PathVariable("emsOrderId") Integer emsOrderId) {
        logger.info("Request received to check eligibility for emsOrderId: " + emsOrderId);
        return saleOrderService.checkDeleteSaleOrderEligibility(emsOrderId);
    }

    @GetMapping("{emsOrderItemId}")
    public GetSaleOrderResponse get(@PathVariable("emsOrderItemId") Integer emsOrderItemId) {
        GetSaleOrderRequest request = new GetSaleOrderRequest(emsOrderItemId);
        logger.info("Request received to get sale order: " + request.toString());
        return saleOrderService.getSaleOrder(request);
    }
    
    @PostMapping("/updateOrderQuantity")
    public UpdateOrderQuantityResponse updateOrderQuantity(@Valid @RequestBody UpdateOrderQuantityRequest request) {
        logger.info("Request received to get sale order: " + request.toString());
        
        UpdateOrderQuantityResponse response = saleOrderService.updateSaleOrderQuantity(request);

		if (response.getStatus() && response.getSaleOrderId() != null) {
			try {
				InventoryAllocationRequest allocationRequest = new InventoryAllocationRequest(response.getSaleOrderId(), null);
				producer.sendMessage(ALLOCATION_QUEUE, JsonUtil.toJson(allocationRequest));
			} catch (Exception e) {
        		logger.error("Error occured in pushing to queue: " + request.getEmsOrderItemId(), e);
			}
		}
		return response;
    }

    @PostMapping("getByItemRef")
    public GetAllocatedQtyByItemRefResponse getByItemRef(@Valid @RequestBody GetAllocatedQtyByItemRefRequest request) {
        logger.info("Request received to get sale order allocated qty: " + request.toString());
        return saleOrderService.getAllocatedQty(request);
    }

    @PostMapping("/getForAllocatedQty")
    public GetInventoryForAllocatedQtyResponse getForAllocatedQty(@Valid @RequestBody GetInventoryForAllocatedQtyRequest request) {
        logger.info("get orders for allocated qty: " + request.toString());
        //return saleOrderService.getInventoryAllocationForAllocatedQty(request);
        return saleOrderService.getProductInventoryAvailabilityInfo(request);
    }
    
    @PostMapping("/updateOrderMsn")
    public UpdateOrderMsnResponse updateOrderMsn(@Valid @RequestBody UpdateOrderMsnRequest request) {
        logger.info("Received request to update Order: " + request.toString());
        
        UpdateOrderMsnResponse response = saleOrderService.deallocateInventoryAndUpdateOrderMsn(request.getEmsOrderItemId(), request.getUpdatedMsn());
        
        if(response.getStatus() && response.getSaleOrderId() != null) {
        	try {
				// push order to inventory allocation queue
				InventoryAllocationRequest input = new InventoryAllocationRequest(response.getSaleOrderId(), null);
				producer.sendMessage(ALLOCATION_QUEUE, JsonUtil.toJson(input));
			} catch (Exception e) {
        		logger.error("Error occured in pushing to queue: " + request.getEmsOrderItemId(), e);
			}

        }
        return response;
    }
    
    @GetMapping("/getSaleOrderAllocation/{emsOrderItemId}")
    public SaleOrderAllocationResponse getSaleOrderAllocation(@PathVariable("emsOrderItemId") Integer emsOrderItemId) {
        logger.info("Request received to get getSaleOrderAllocation for sale order item id: " + emsOrderItemId);
        return saleOrderService.getSaleOrderAllocations(emsOrderItemId);
    }
    
    @GetMapping("/transfer/source/{sourceEmsOrderItemId}/destination/{destEmsOrderItemId}")
    public BaseResponse pushOrderToQueue(@PathVariable("sourceEmsOrderItemId") Integer sourceEmsOrderItemId, @PathVariable("destEmsOrderItemId") Integer destEmsOrderItemId) {
        logger.info("Request received to transfer inventory from : " + sourceEmsOrderItemId + "to: " + destEmsOrderItemId);
        return saleOrderService.transferOrderInventory(sourceEmsOrderItemId,destEmsOrderItemId);
    }
    
    @GetMapping("/deAllocateByItemRef/{itemRef}")
    public SaleOrderDeallocationResponse deAllocateByItemRef(@PathVariable("itemRef") String itemRef) throws Exception {
        logger.info("Request received to deallocated inventory of itemRef : " + itemRef );
        return saleOrderService.deAllocateByItemRef(itemRef);
    }
    
    @GetMapping("/updatePackedQuantity/{emsOrderItemId}/quantity/{quantity}")
    public BaseResponse updatePackedQuantity(@PathVariable("emsOrderItemId") Integer emsOrderItemId, @PathVariable("quantity") Double quantity) {
        logger.info("Request received to update packed quantity for itemId : " + emsOrderItemId + "with quantity" + quantity);
        return saleOrderService.updatePackedQuantity(emsOrderItemId, quantity);
    }
    
    @GetMapping("/updateFulFillmentWarehouse/{emsOrderItemId}/warehouse/{id}")
    public UpdateFulFillmentWarehouseResponse updateFulFillmentWarehouse(@PathVariable("emsOrderItemId") Integer emsOrderItemId, @PathVariable("id") Integer warehouseId) {
        logger.info("Request received to update updateFulFillmentWarehouse for itemId : " + emsOrderItemId + " with warehouseID " + warehouseId);
        UpdateFulFillmentWarehouseResponse response =  saleOrderService.updateFulFillmentWarehouse(emsOrderItemId, warehouseId);
        
		/*
		 * if (response.getStatus()) { try { InventoryAllocationRequest
		 * allocationRequest = new InventoryAllocationRequest(response.getSaleOrderId(),
		 * null); producer.sendMessage(ALLOCATION_QUEUE,
		 * JsonUtil.toJson(allocationRequest)); } catch (Exception e) {
		 * logger.error("Error occured in pushing to queue: " + emsOrderItemId, e); } }
		 */
        return response;
    }
    
    @GetMapping("/updatePlantId/{emsOrderItemId}/plantId/{plantId}")
    public BaseResponse updatePlantId(@PathVariable("emsOrderItemId") Integer emsOrderItemId, @PathVariable("plantId") Integer plantId) {
        logger.info("Request received to update plant id for itemId : " + emsOrderItemId + "with plantId" + plantId);
        return saleOrderService.updatePlantId(emsOrderItemId, plantId);
    }
    
    @GetMapping("/deallocate/{sourceEmsOrderItemId}")
    public BaseResponse deallocateInventory(@PathVariable("sourceEmsOrderItemId") Integer sourceEmsOrderItemId) {
        logger.info("Request received to deallocate inventory from : " + sourceEmsOrderItemId);
        return saleOrderService.deAllocateInventory(sourceEmsOrderItemId);
    }
    
    @GetMapping("/getStats/allocated")
    public List<Integer> findSaleOrdersWithAllTransferred() {
        logger.info("Request received to get defective allocated saleOrders");
        return saleOrderService.findSaleOrdersWithAllTransferred();
    }
    
    @GetMapping("/getStats/outOfSync")
    public List<Integer> findDefectiveOutOfSyncOrders() {
        logger.info("Request received to get defective out of sync saleOrders");
        return saleOrderService.findOutOfSyncSaleOrders();
    }
    
    @PostMapping("/getForPackedQty")
    public GetInventoryForPackedQtyResponse getForPackedQty(@Valid @RequestBody GetInventoryForPackedQtyRequest request) {
        logger.info("get orders for allocated qty: " + request.toString());
        //return saleOrderService.getInventoryAllocationForAllocatedQty(request);
        return saleOrderService.getProductInventoryPackedInfo(request);
    } 
    
    @PostMapping("/repushSaleOrderFailedToPublishInKafkaTopic")
    public BaseResponse repushSaleOrderFailedToPublishInKafkaTopic(@RequestBody RepushSaleOrderRequest request) {
        logger.info("Request received to repushSaleOrderFailedToPublishInKafkaTopic : " +  new Gson().toJson(request) );
        return saleOrderService.repushSaleOrderFailedToPublishInKafkaTopic(request);
    }
}

package com.moglix.wms.service;

import com.moglix.wms.api.request.*;
import com.moglix.wms.api.response.*;
import com.moglix.wms.dto.SalesOpsOrderDTO;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.entities.SaleOrderAllocationHistory;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pankaj on 6/5/19
 */
public interface ISaleOrderService {
    SaleOrder upsert(SaleOrder obj);

    SaleOrder getById(Integer id);

    Iterable<SaleOrder> upsertAll(List<SaleOrder> saleOrders);

    List<SaleOrder> findOpenSaleOrderForProduct(Integer productId);

    SaleOrder getByItemRef(String itemRef);

    SaleOrderAllocation upsertAllocation(SaleOrderAllocation obj);

    SaleOrderAllocation getAllocationById(Integer id);

    SaleOrderAllocationHistory upsertAllocationHistory(SaleOrderAllocationHistory obj);

    CreateSaleOrderResponse createSaleOrder(CreateSaleOrderRequest request);

    DeleteSaleOrderItemResponse deleteSaleOrderItem(DeleteSaleOrderItemRequest request);

    List<SaleOrder> getOrdersfromOrderItemIds(Set<Integer> emsOrderItemIds);

    CheckAvailableQtyResponse checkAvailability(CheckAvailableQtyRequest request);

    GetSaleOrderResponse getSaleOrder(GetSaleOrderRequest request);

    GetAllocatedQtyByItemRefResponse getAllocatedQty(GetAllocatedQtyByItemRefRequest request);

    GetInventoryForAllocatedQtyResponse getInventoryAllocationForAllocatedQty(GetInventoryForAllocatedQtyRequest request);

    UpdateOrderMsnResponse deallocateInventoryAndUpdateOrderMsn(Integer order, String productMsn);

    UpdateOrderQuantityResponse updateSaleOrderQuantity(UpdateOrderQuantityRequest request);

    CreateSaleOrderResponse migrateSaleOrder(@Valid CreateSaleOrderRequest request);

    BaseResponse markOrderFullfilled(Integer emsOrderItemId);

    BaseResponse markOrderOpen(Integer emsOrderItemId);

    SaleOrderAllocationResponse getSaleOrderAllocations(Integer emsOrderItemId);

    BaseResponse transferOrderInventory(Integer sourceEmsOrderItemId, Integer destinationEmsOrderItemId);

    BaseResponse updatePackedQuantity(Integer emsOrderItemId, Double quantity);

    BaseResponse deAllocateInventory(Integer sourceEmsOrderItemId);

    void updateSaleOrderAllocationShippedQuantity(Map<Integer, Double> shippedQuantityMap);

    GetInventoryForAllocatedQtyResponse getProductInventoryAvailabilityInfo(GetInventoryForAllocatedQtyRequest request);

	List<Integer> findSaleOrdersWithAllTransferred();

	List<Integer> findOutOfSyncSaleOrders();

	SaleOrderDeallocationResponse deAllocateByItemRef(String itemRef) throws Exception;

	List<CreateSaleOrderResponse> bulkCreateSaleOrder(@Valid List<CreateSaleOrderRequest> request);

	BaseResponse buyersCreateOrder(@Valid BuyersCreateSaleOrderRequest request);

	CheckDeleteSaleOrderEligibilityResponse checkDeleteSaleOrderEligibility(Integer emsOrderId);

	BaseResponse updatePlantId(Integer emsOrderItemId, Integer plantId);

	UpdateFulFillmentWarehouseResponse updateFulFillmentWarehouse(Integer emsOrderItemId, Integer warehouseId);

	GetInventoryForPackedQtyResponse getProductInventoryPackedInfo(GetInventoryForPackedQtyRequest request);
	
	boolean createSaleOrderFromKafka(SalesOpsOrderDTO salesOpsOrderDTO);
	
	BaseResponse repushSaleOrderFailedToPublishInKafkaTopic(RepushSaleOrderRequest request);
}

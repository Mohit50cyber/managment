package com.moglix.wms.service;

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
import com.moglix.wms.entities.ProductInventory;
import org.springframework.data.domain.Pageable;

import java.util.List;

import javax.validation.Valid;

/**
 * @author pankaj on 6/5/19
 */
public interface IProductInventoryService {
    ProductInventory upsert(ProductInventory obj);
    void upsertAll(List<ProductInventory> list);
    ProductInventory getById(Integer id);
    List<ProductInventory> getAll();
    ProductInventory getByWarehouseIdAndProductId(Integer warehouseId, Integer productId);
    ProductInventory getByWarehouseIdAndProductProductMsn(Integer warehouseId, String productMsn);
    List<ProductInventory> getByWarehouseIdInAndProductProductMsnIn(List<Integer> warehouseId, List<String> productMsn);

    List<ProductInventory>findByAllocatedQuantityGreaterThan(double value);
    
    SearchProductInventoryResponse searchInventory(SearchProductInventoryRequest request, Integer countryId, Pageable page);
    GetInventoryStatsResponse getInventoryStats(GetInventoryStatsRequest request);
    
    BaseResponse deleteInventory(DeleteInventoryRequest request);
	BaseResponse deleteInventoryByWarehouse(@Valid DeleteWarehouseInventoryRequest request);
	GetInventoryAvailabilityResponse getInventoryAvailability(@Valid GetInventoryAvailabilityRequest request);
	BaseResponse blockInventory(@Valid BlockInventoryRequest request);
	GetInventoryAvailabilityResponse getRealtimeInventoryAvailability(@Valid GetInventoryAvailabilityRequest request);
	BaseResponse uploadProductInventoryConfig(String filename);
	GetFreshAvailableQuantityResponse getFreshAvailableQuantity();
	GetDnItemsResponse getDnInititatedItems(@Valid GetDnDetailItemsRequest request);
	GetDnItemsResponse getDnCreatedItems(@Valid GetDnDetailItemsRequest request);
}

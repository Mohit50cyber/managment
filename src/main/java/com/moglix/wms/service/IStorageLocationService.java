package com.moglix.wms.service;

import com.moglix.wms.api.request.FindStorageLocationRequest;
import com.moglix.wms.api.request.SearchStorageLocationRequest;
import com.moglix.wms.api.request.SearchWithMsnRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.FetchStorageResponse;
import com.moglix.wms.api.response.GetProductsInStorageLocationResponse;
import com.moglix.wms.api.response.SearchStorageLocationResponse;
import com.moglix.wms.entities.StorageLocation;
import org.springframework.data.domain.Pageable;

import java.util.List;

import javax.validation.Valid;

/**
 * @author pankaj on 1/5/19
 */
public interface IStorageLocationService {
    StorageLocation upsert(StorageLocation storageLocation);
    StorageLocation getById(Integer id);
    List<StorageLocation> getAll();

    SearchStorageLocationResponse searchStorageLocation(SearchStorageLocationRequest request, Pageable page);
    
    StorageLocation findByWarehouseIdAndZoneIdAndRackIdAndBinId(Integer warehouseId, Integer zoneId, Integer rackId, Integer binId);
    GetProductsInStorageLocationResponse getProductsInStorageLocation(int storageLocationId, @Valid SearchWithMsnRequest request);
    GetProductsInStorageLocationResponse getProductsInStorageLocation(@Valid SearchWithMsnRequest request);
	SearchStorageLocationResponse searchStorageLocationForBinTransfer(SearchStorageLocationRequest request,
			Pageable page);
	GetProductsInStorageLocationResponse getProductsInStorageLocation(int storageLocationId);
	GetProductsInStorageLocationResponse getProductsInExpiry(Integer storageLocationId,
			@Valid SearchWithMsnRequest request);	
	
	FetchStorageResponse fetchStorageLocation(FindStorageLocationRequest request);
}

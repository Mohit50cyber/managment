package com.moglix.wms.service;

import java.util.List;

import com.moglix.wms.api.request.CreateInboundStorageRequest;
import com.moglix.wms.api.request.GetInventoryLocationsForAvailableQtyRequest;
import com.moglix.wms.api.request.GetInventoryLocationsForTotalQtyRequest;
import com.moglix.wms.api.request.StockTransferRequest;
import com.moglix.wms.api.response.CreateInboundStorageResponse;
import com.moglix.wms.api.response.GetInventoryLocationsForAvailableQtyResponse;
import com.moglix.wms.api.response.GetInventoryLocationsForTotalQtyResponse;
import com.moglix.wms.api.response.StockTransferResponse;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.StockTransferNote;

public interface IInboundStorageService {
	InboundStorage upsert(InboundStorage obj);
	InboundStorage getById(Integer id);
	List<InboundStorage>findAllByInboundIdIn(List<Integer>ids);
	CreateInboundStorageResponse create(CreateInboundStorageRequest request);
	List<InboundStorage> findAllByProductId(Integer productId);
	List<InboundStorage> findAvailableByProduct(Integer productId);
	void saveAll(Iterable<InboundStorage> storages);
	GetInventoryLocationsForTotalQtyResponse getLocationsForTotalQty(GetInventoryLocationsForTotalQtyRequest request);
	GetInventoryLocationsForAvailableQtyResponse getLocationsForAvailableQty(GetInventoryLocationsForAvailableQtyRequest request);
	List<StockTransferResponse> binTransfer(List<StockTransferRequest> request) ;
    void deductInboundStorage(StockTransferNote stockTransferNote);
	
}

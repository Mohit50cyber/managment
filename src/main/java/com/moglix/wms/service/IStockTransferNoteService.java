package com.moglix.wms.service;

import com.moglix.wms.api.request.CreateStockTransferNoteRequest;
import com.moglix.wms.api.request.StockTransferNoteSearchRequest;
import com.moglix.wms.api.request.StockTransferNoteUpdateRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.STNWarehouseEligibleResponse;
import com.moglix.wms.api.response.StockTransferNoteResponse;
import com.moglix.wms.api.response.StockTransferNoteSearchResponse;
import com.moglix.wms.dto.UpdateSTNResponse;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.StockTransferNote;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IStockTransferNoteService {
    long externalIdToInternalId( String externalId);
    SaleOrder checkIfItemRefIsStnEligibility(int warehouseId,
                                             String itemRef);

    String fetchHsnCode(String itemRef);

    StockTransferNoteResponse createSTN(CreateStockTransferNoteRequest stockTransferNoteRequest);
    StockTransferNoteResponse getStockTransferInfo(long stn_id);

    UpdateSTNResponse updateStn(long stn_Id, StockTransferNoteUpdateRequest stockTransferNoteUpdateRequest);
    StockTransferNoteSearchResponse searchPageable(StockTransferNoteSearchRequest request);

	STNWarehouseEligibleResponse checkWarehouseEligibleSTN(String productId, Integer warehouseId);

	BaseResponse receiveWarehouseItems(Long stn_id, String user);

}

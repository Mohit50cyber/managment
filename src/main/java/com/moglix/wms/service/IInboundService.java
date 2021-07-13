package com.moglix.wms.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.moglix.wms.api.request.GetInboundByIdRequest;
import com.moglix.wms.api.request.GetInboundRequest;
import com.moglix.wms.api.request.InventoriseInboundRequest;
import com.moglix.wms.api.request.UpdateInboundTaxRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse;
import com.moglix.wms.api.response.GetInboundByIdResponse;
import com.moglix.wms.api.response.GetInboundByPoItemIdResponse;
import com.moglix.wms.api.response.GetInboundResponse;
import com.moglix.wms.api.response.InventoriseInboundResponse;
import com.moglix.wms.api.response.LotInfoResponse;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.service.impl.UpdateInboundRequest;

/**
 * @author pankaj on 30/4/19
 */
public interface IInboundService {

    Inbound upsert(Inbound inbound);
    Inbound getById(Integer id);
    List<Inbound> getByBatchId(Integer batchId);
    void delete(Inbound inbound);
    void deleteMultiple(List<Inbound> inbounds);
    Page<Inbound>findAllByOrderByModifiedDesc(Pageable page);
    void saveAll(Iterable<Inbound> inbounds);
    Page<Inbound> findByWarehouseId(Integer warehouseId, Pageable page);
    GetInboundResponse getInboundList(GetInboundRequest warehouseId, Pageable page);
	GetInboundByIdResponse getInboundById(GetInboundByIdRequest request);
    InventoriseInboundResponse inventoriseInbound(InventoriseInboundRequest request);
	GetFreeInventoryResponse getFreeInventory(String refNo, Integer supplierPoId);
	GetInboundResponse searchInbounds(GetInboundRequest request, Pageable page);
	GetInboundResponse getInboundByProducMsn(GetInboundRequest request, Pageable page);
	BaseResponse updateInboundTransferPrice(UpdateInboundRequest updateRequest);
	BaseResponse updateInboundTax(UpdateInboundTaxRequest request);
	GetInboundByPoItemIdResponse getInBoundByPoItemId(Integer poItemId);
	LotInfoResponse getLotInfoById(GetInboundByIdRequest request);
	GetInboundResponse searchAppInbounds(@Valid GetInboundRequest request, Pageable page);
}

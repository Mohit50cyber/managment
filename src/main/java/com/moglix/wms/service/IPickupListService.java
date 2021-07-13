package com.moglix.wms.service;

import com.moglix.wms.api.request.BulkGeneratePickupListRequest;
import com.moglix.wms.api.request.GeneratePickupListByMSNRequest;
import com.moglix.wms.api.request.GeneratePickupListRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GeneratePickupListAppResponse;
import com.moglix.wms.api.response.GeneratePickupListResponse;
import com.moglix.wms.api.response.CountWarehouseDataResponse;
import com.moglix.wms.controller.BulkGeneratePickupListResponse;
import com.moglix.wms.entities.PickupList;
import com.moglix.wms.entities.PickupListItem;

import java.util.List;

import javax.validation.Valid;

/**
 * @author pankaj on 14/5/19
 */
public interface IPickupListService {
    PickupList upsert(PickupList obj);
    PickupList getById(Integer id);
    Iterable<PickupList> upsertAll(List<PickupList> pickupLists);

    PickupListItem upsertPickListItem(PickupListItem obj);
    PickupListItem getPickupListItemById(Integer id);

    GeneratePickupListResponse generatePickList(GeneratePickupListRequest request);
    GeneratePickupListAppResponse generatePickListApp(GeneratePickupListRequest request);
    GeneratePickupListResponse generatePickListByMSN(GeneratePickupListByMSNRequest request);
	BulkGeneratePickupListResponse bulkGeneratePickList(@Valid BulkGeneratePickupListRequest request);
	BaseResponse updateStatus(Integer packetId);
	CountWarehouseDataResponse countWarehouseData(Integer warehouseId);
	BaseResponse packetInProgress(Integer packetId,String usermail);
	BaseResponse updatePacketItem(Integer packetId, Integer packetItemId,String user);
	
	
}

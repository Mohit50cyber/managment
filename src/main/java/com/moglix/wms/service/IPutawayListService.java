package com.moglix.wms.service;

import com.moglix.wms.api.request.GeneratePutawayListRequest;
import com.moglix.wms.api.response.GeneratePutawayListResponse;
import com.moglix.wms.entities.PutawayList;

public interface IPutawayListService {
	GeneratePutawayListResponse generatePutawayList(GeneratePutawayListRequest request);
	PutawayList save(PutawayList list);
}

package com.moglix.wms.service;

import com.moglix.wms.api.request.SearchZoneRequest;
import com.moglix.wms.api.response.SearchZoneResponse;
import com.moglix.wms.entities.Zone;

import java.util.List;

import javax.validation.Valid;

/**
 * @author pankaj on 1/5/19
 */
public interface IZoneService {
    Zone upsert(Zone zone);
    Zone getById(Integer id);
    List<Zone> getAll();

    SearchZoneResponse searchZone(SearchZoneRequest request);
	SearchZoneResponse searchZoneForBinTransfer(SearchZoneRequest request);
}

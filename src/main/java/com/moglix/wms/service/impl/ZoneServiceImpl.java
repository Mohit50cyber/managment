package com.moglix.wms.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.moglix.wms.api.request.SearchZoneRequest;
import com.moglix.wms.api.response.SearchZoneResponse;
import com.moglix.wms.constants.StorageLocationType;
import com.moglix.wms.dto.ZoneDto;
import com.moglix.wms.entities.Zone;
import com.moglix.wms.repository.ZoneRepository;
import com.moglix.wms.service.IZoneService;

/**
 * @author pankaj on 1/5/19
 */
@Service("zoneService")
public class ZoneServiceImpl implements IZoneService {

	Logger log = LogManager.getLogger(ZoneServiceImpl.class);

	@Autowired
	private ZoneRepository repository;

	@Override
	public Zone upsert(Zone zone) {
		return repository.save(zone);
	}

	@Override
	public Zone getById(Integer id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<Zone> getAll() {
		return repository.findAll();
	}

	@Override
    @Transactional
	public SearchZoneResponse searchZone(SearchZoneRequest request) {
		log.info("Search Zones By Warehouse Service Started");
		SearchZoneResponse response = new SearchZoneResponse();
		List<Zone> zones = repository.findByWarehouseId(request.getWarehouseId());
		if (!CollectionUtils.isEmpty(zones)) {
			for (Zone zone : zones) {

				if(zone.getRacks().stream().flatMap(e -> e.getBins().stream())
						.flatMap(e -> e.getStorageLocations().stream())
						.anyMatch(e -> e.getType().equals(StorageLocationType.QUARANTINE))) {
					continue;
				}
				response.getZones().add(new ZoneDto(zone));
			}
			response.setMessage("Zone found : " + zones.size());
		} else {
			response.setMessage("No Zone found for warehouse id: " + request.getWarehouseId());

			response.setStatus(true);
			response.setCode(HttpStatus.OK.value());
			log.info("Search Zones By Warehouse Service Ended");
			
		}
		return response;
		}

	@Override
	 @Transactional
	public SearchZoneResponse searchZoneForBinTransfer(SearchZoneRequest request) {
		log.info("Search Zones for bin transfer By Warehouse Service Started");
		SearchZoneResponse response = new SearchZoneResponse();
		List<Zone> zones = repository.findByWarehouseId(request.getWarehouseId());
		if (!CollectionUtils.isEmpty(zones)) {
			for (Zone zone : zones) {
				response.getZones().add(new ZoneDto(zone));
			}
			response.setMessage("Zone found : " + zones.size());
		} else {
			response.setMessage("No Zone found for warehouse id: " + request.getWarehouseId());

			response.setStatus(true);
			response.setCode(HttpStatus.OK.value());
			log.info("Search Zones By Warehouse Service Ended");
			
		}
		return response;
		}

}

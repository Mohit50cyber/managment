package com.moglix.wms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moglix.wms.entities.PutawayListInboundStorageMapping;
import com.moglix.wms.repository.PutawayListInboundStorageMappingRepository;
import com.moglix.wms.service.IPutawayListInboundStorageMappingService;

@Service(value = "putawayListInboundStorageMappingServiceImpl")
public class PutawayListInboundStorageMappingServiceImpl implements IPutawayListInboundStorageMappingService {

	@Autowired
	PutawayListInboundStorageMappingRepository repository;
	@Override
	public PutawayListInboundStorageMapping save(PutawayListInboundStorageMapping entity) {
		return repository.save(entity);
	}

}

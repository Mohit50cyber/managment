package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.PutawayListInboundStorageMapping;

@Repository
public interface PutawayListInboundStorageMappingRepository extends CrudRepository<PutawayListInboundStorageMapping, Integer> {

}

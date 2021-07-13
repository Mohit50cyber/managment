package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.PutawayList;

@Repository
public interface PutawayListRepository extends CrudRepository<PutawayList, Integer> {

}

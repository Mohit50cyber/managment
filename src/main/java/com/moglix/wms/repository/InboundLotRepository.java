package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.InboundLot;


@Repository
public interface InboundLotRepository extends CrudRepository<InboundLot, Integer> {

}

package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.ReturnPacketItem;

@Repository
public interface ReturnPacketItemRepository extends CrudRepository<ReturnPacketItem, Integer> {

}

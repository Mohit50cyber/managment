package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.PacketItem;

@Repository
public interface PacketItemRepository extends CrudRepository<PacketItem, Integer>  {

}

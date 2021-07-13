package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.BlockedProductInventoryHistory;

@Repository
public interface BlockedProductInventoryHistoryRepository extends CrudRepository<BlockedProductInventoryHistory, Integer> {

}

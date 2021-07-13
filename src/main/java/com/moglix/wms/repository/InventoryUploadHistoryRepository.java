package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;

import com.moglix.wms.entities.InventoryUploadHistory;

public interface InventoryUploadHistoryRepository extends CrudRepository<InventoryUploadHistory, Integer> {

}

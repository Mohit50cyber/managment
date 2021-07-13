package com.moglix.wms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.ProductInventoryHistory;

@Repository
public interface ProductInventoryHistoryRepository extends CrudRepository<ProductInventoryHistory, Integer>{
	Page<ProductInventoryHistory> findByProductMsnAndWarehouseId(String productMsn, Integer warehouseId, Pageable page);
}

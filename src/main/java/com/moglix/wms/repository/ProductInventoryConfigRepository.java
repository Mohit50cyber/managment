package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.ProductInventoryConfig;

@Repository
public interface ProductInventoryConfigRepository extends CrudRepository<ProductInventoryConfig, Integer> {

	ProductInventoryConfig findByProductMsnAndWarehouseId(String productMsn, Integer warehouseId);
}

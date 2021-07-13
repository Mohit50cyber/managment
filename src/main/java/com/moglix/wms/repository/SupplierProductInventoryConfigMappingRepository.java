package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.SupplierProductInventoryConfigMapping;

@Repository
public interface SupplierProductInventoryConfigMappingRepository extends CrudRepository<SupplierProductInventoryConfigMapping, Integer> {
	
}

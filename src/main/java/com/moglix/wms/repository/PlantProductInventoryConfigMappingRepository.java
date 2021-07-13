package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.PlantProductInventoryConfigMapping;

@Repository
public interface PlantProductInventoryConfigMappingRepository extends CrudRepository<PlantProductInventoryConfigMapping, Integer> {

}

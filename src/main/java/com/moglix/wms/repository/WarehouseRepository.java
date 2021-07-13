package com.moglix.wms.repository;

import com.moglix.wms.entities.Warehouse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author pankaj on 30/4/19
 */
@Repository
public interface WarehouseRepository extends CrudRepository<Warehouse, Integer> {
  
	@Override
    List<Warehouse> findAll();
    
    Optional<Warehouse> findByName(String name);
   
    List<Warehouse> findAllByIsoNumberAndIsActive(Integer isoNumber, Boolean isActive);
}

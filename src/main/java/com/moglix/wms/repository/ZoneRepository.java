package com.moglix.wms.repository;

import com.moglix.wms.entities.Zone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author pankaj on 1/5/19
 */
@Repository
public interface ZoneRepository extends CrudRepository<Zone, Integer> {
    List<Zone> findByWarehouseId(Integer id);
    @Override
    List<Zone> findAll();
    
    Optional<Zone>findByWarehouseIdAndName(Integer warehouseId, String name);
}

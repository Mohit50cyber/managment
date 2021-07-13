package com.moglix.wms.repository;

import com.moglix.wms.entities.PickupList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pankaj on 14/5/19
 */
@Repository
public interface PickupListRepository extends CrudRepository<PickupList, Integer> {
}

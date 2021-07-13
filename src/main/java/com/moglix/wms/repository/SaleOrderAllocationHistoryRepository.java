package com.moglix.wms.repository;

import com.moglix.wms.entities.SaleOrderAllocationHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pankaj on 8/5/19
 */
@Repository
public interface SaleOrderAllocationHistoryRepository extends CrudRepository<SaleOrderAllocationHistory, Integer> {
}

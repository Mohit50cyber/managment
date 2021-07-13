package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.BinTransferHistory;

@Repository
public interface BinTransferHistoryRepository extends CrudRepository<BinTransferHistory, Integer> {

}

package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.BinTransferDao;

@Repository
public interface BinTransferRepository extends CrudRepository<BinTransferDao, Integer> {

}
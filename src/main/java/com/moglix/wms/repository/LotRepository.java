package com.moglix.wms.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.moglix.wms.entities.Lot;

public interface LotRepository extends CrudRepository<Lot, Integer> {

	Set<Lot> findAllByLotMsnSupplierId(List<String> lotMsnSupplierList);

}

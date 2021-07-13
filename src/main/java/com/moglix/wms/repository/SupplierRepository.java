package com.moglix.wms.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.Supplier;

@Repository
public interface SupplierRepository extends CrudRepository<Supplier, Integer> {

	Optional<Supplier>findByEmsSupplierId(Integer emsSupplierId);
}

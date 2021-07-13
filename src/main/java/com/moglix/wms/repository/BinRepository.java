package com.moglix.wms.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.Bin;

@Repository
public interface BinRepository extends CrudRepository<Bin, Integer> {	
	Optional<Bin>findByRackIdAndName(Integer rackId, String name);
}

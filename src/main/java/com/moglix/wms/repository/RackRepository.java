package com.moglix.wms.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.Rack;

@Repository
public interface RackRepository extends CrudRepository<Rack, Integer> {

	Optional<Rack>findByZoneIdAndName(Integer zoneId, String name);
}

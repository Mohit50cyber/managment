package com.moglix.wms.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.Plant;

@Repository
public interface PlantRepository extends CrudRepository<Plant, Integer> {

	Optional<Plant>findByBuyersPlantId(Integer buyersPlantId);
}

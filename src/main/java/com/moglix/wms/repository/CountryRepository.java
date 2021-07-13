package com.moglix.wms.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.moglix.wms.entities.Country;

/**
 * @author sparsh saxena on 22/4/21
 */
public interface CountryRepository extends CrudRepository<Country, Integer>{
	
	@Override
    List<Country> findAll();
	
	List<Country> findAllByIsActive(Boolean isActive);
	
	Country findByIsoNumber(Integer isoNumber);

}

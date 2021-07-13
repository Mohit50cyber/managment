package com.moglix.wms.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.moglix.wms.api.response.CountryListResponse;
import com.moglix.wms.dto.CountryDTO;
import com.moglix.wms.entities.Country;
import com.moglix.wms.repository.CountryRepository;
import com.moglix.wms.service.ILocationService;

/**
 * @author sparsh saxena on 22/4/21
 */
@Service("locationService")
public class LocationServiceImpl implements ILocationService{

	Logger log = LogManager.getLogger(LocationServiceImpl.class);
	
    @Autowired
    private CountryRepository repository;
	 
	@Override
	public List<Country> getAll() {
		return repository.findAll();
	}
	
	@Override
	public List<Country> getAllByIsActive(Boolean isActive) {
		return repository.findAllByIsActive(isActive);
	}

	@Override
	public CountryListResponse getAllCountries() {
		
		log.info("Country Service Started");
	    CountryListResponse response = new CountryListResponse();
	    // List<Country> countries = getAll();
	    List<Country> countries = getAllByIsActive(true);
	    if(!CollectionUtils.isEmpty(countries)) {
            for(Country country : countries) {
                response.getCountries().add(new CountryDTO(country));
            }
            response.setMessage("Countries found :: " + countries.size());
         } else {
            response.setMessage("No country found.");
         }
         response.setStatus(true);
         response.setCode(HttpStatus.OK.value());
	   
	     log.info("Country Service end");
		   
		 return response;
	}

	
}

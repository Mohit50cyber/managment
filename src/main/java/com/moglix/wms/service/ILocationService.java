package com.moglix.wms.service;

import java.util.List;

import com.moglix.wms.api.response.CountryListResponse;
import com.moglix.wms.entities.Country;

/**
 * @author sparsh saxena on 22/4/21
 */

public interface ILocationService {
	
	 List<Country> getAll();
	 
	 List<Country> getAllByIsActive(Boolean isActive);
	 
	 CountryListResponse getAllCountries();

}

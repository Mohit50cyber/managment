package com.moglix.wms.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.response.CountryListResponse;
import com.moglix.wms.service.ILocationService;

/**
 * @author sparsh saxena on 22/4/21
 */

@RestController
@RequestMapping("/api/Location/")
public class LocationController {
	
	Logger logger = LogManager.getLogger(LocationController.class);
	
	@Autowired
	@Qualifier("locationService")
	ILocationService locationService;
	
	@GetMapping("ping")
    public String ping() {
        return "Welcome to Location Controller";
    }
 
	@GetMapping("getCountries")
    public CountryListResponse getAllCountries() {
		logger.info("Countries api");
        return locationService.getAllCountries();
    }
}

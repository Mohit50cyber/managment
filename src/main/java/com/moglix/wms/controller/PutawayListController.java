package com.moglix.wms.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.request.GeneratePutawayListRequest;
import com.moglix.wms.api.response.GeneratePutawayListResponse;
import com.moglix.wms.service.IPutawayListService;

@RestController
@RequestMapping("/api/putawayLists")
public class PutawayListController {
	Logger log = LogManager.getLogger(PutawayListController.class);
	
	@Autowired
	@Qualifier("putawaylistserviceImpl")
	IPutawayListService putawayListService;
	
	@PostMapping(value = "/")
	public GeneratePutawayListResponse get(@Valid @RequestBody GeneratePutawayListRequest request) {
		log.info("Received request to generate PutawayList for Inbounds: " + request.getInboundIds());
		return putawayListService.generatePutawayList(request);
	}
}

package com.moglix.wms.controller;

import com.moglix.wms.api.request.BulkGeneratePickupListRequest;
import com.moglix.wms.api.request.GeneratePickupListRequest;
import com.moglix.wms.api.response.GeneratePickupListResponse;
import com.moglix.wms.service.IPickupListService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author pankaj on 14/5/19
 */
@RestController
@RequestMapping("/api/pickupLists")
public class PickupListController {

    Logger logger = LogManager.getLogger(PickupListController.class);

    @Autowired
    private IPickupListService pickupListService;

    @PostMapping("/")
    public GeneratePickupListResponse generate(@Valid @RequestBody GeneratePickupListRequest request) {
        logger.info("Request received to generate Pickuplist : " + request.toString());
        return pickupListService.generatePickList(request);
    }
    
    @PostMapping("/bulkCreate")	
    public BulkGeneratePickupListResponse bulkGenerate(@Valid @RequestBody BulkGeneratePickupListRequest request) {
        logger.info("Bulk Request received to generate bulk Pickuplist : " + request.toString());
        return pickupListService.bulkGeneratePickList(request);
    }
}

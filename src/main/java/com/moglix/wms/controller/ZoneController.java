package com.moglix.wms.controller;

import com.moglix.wms.api.request.SearchZoneRequest;
import com.moglix.wms.api.response.SearchZoneResponse;
import com.moglix.wms.service.IZoneService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author pankaj on 1/5/19
 */
@RestController
@RequestMapping("/api/zone/")
public class ZoneController {

    Logger log = LogManager.getLogger(ZoneController.class);

    @Autowired
    @Qualifier("zoneService")
    IZoneService zoneService;

    @GetMapping("ping")
    public String ping() {
        return "Welcome to Zone Controller";
    }

    @PostMapping("search")
    public SearchZoneResponse searchZone(@Valid @RequestBody SearchZoneRequest request) {
        log.info("search zone api : " + request.toString());
        return zoneService.searchZone(request);
    }
    
    @PostMapping("searchForBinTransfer")
    public SearchZoneResponse searchZoneForBinTransfer(@Valid @RequestBody SearchZoneRequest request) {
        log.info("search zone api for bin transfer: " + request.toString());
        return zoneService.searchZoneForBinTransfer(request);
    }
}

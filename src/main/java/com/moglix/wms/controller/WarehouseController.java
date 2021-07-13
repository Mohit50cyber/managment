package com.moglix.wms.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.request.GetWarehouseByIdRequest;
import com.moglix.wms.api.request.SearchWarehouseRequest;
import com.moglix.wms.api.response.BlockedInventoryDataResponse;
import com.moglix.wms.api.response.ExpiredInventoryDataResponse;
import com.moglix.wms.api.response.GetWarehouseByIdResponse;
import com.moglix.wms.api.response.InventoryDataResponse;
import com.moglix.wms.api.response.SearchWarehouseResponse;
import com.moglix.wms.api.response.VmiReportDataResponse;
import com.moglix.wms.service.IWarehouseService;

/**
 * @author pankaj on 30/4/19
 */
@RestController
@RequestMapping("/api/warehouse/")
public class WarehouseController {

    Logger log = LogManager.getLogger(WarehouseController.class);

    @Autowired
    @Qualifier("warehouseService")
    IWarehouseService warehouseService;

    @GetMapping("ping")
    public String ping() {
        return "Welcome to Warehouse Controller";
    }

    @GetMapping("search")
    public SearchWarehouseResponse search(@RequestHeader Integer countryId) {
        log.info("search warehouse api");
        return warehouseService.searchWarehouse(countryId);
    }

    @GetMapping("getById/{id}")
    public GetWarehouseByIdResponse getById(@PathVariable("id") Integer id) {
        log.info("get warehouse by id api : " + id);
        GetWarehouseByIdRequest request = new GetWarehouseByIdRequest();
        request.setId(id);
        return warehouseService.getWarehouseById(request);
    }
    
    @GetMapping("getInventoryData")
	public void getInventoryData(HttpServletResponse response, @RequestParam(value = "warehouseId") Optional<Integer> warehouseId)
			throws IOException {
		log.info("Got request for to download Invnetory: " + warehouseId.orElse(0));
		warehouseService.getInventoryData(warehouseId,response);
	}
    
    @GetMapping("getInventoryDataAngular")
    public InventoryDataResponse getInventoryDataAngular(HttpServletResponse response, @RequestParam(value = "warehouseId") Optional<Integer> warehouseId)
			throws IOException {
		log.info("Got request for to download Invnetory: " + warehouseId.orElse(0));
		return warehouseService.getInventoryDataAngular(warehouseId,response);
	}
    
    @GetMapping("getBlockedInventoryDataAngular")
    public BlockedInventoryDataResponse getBlockedInventoryDataAngular(@RequestParam(value = "warehouseId") Optional<Integer> warehouseId)
			throws IOException {
		log.info("Got request for to download blocked Inventory report: " + warehouseId.orElse(0));
		return warehouseService.getBlockedInventoryDataAngular(warehouseId);
	}
    
	@GetMapping("downloadreport")
	public void downloadInventory(HttpServletResponse response) {
		log.info("Got request for to download Invnetory Report");
		warehouseService.downloadInventory(response);
	}
	
	@GetMapping("getVmiReportDataAngular")
    public VmiReportDataResponse getVmiReportDataAngular(HttpServletResponse response, @RequestHeader Integer countryId)
			throws IOException {
		log.info("Got request for vmi report download : ");
		return warehouseService.getVmiReportDataAngular(response, countryId);
	}
	
	@GetMapping("getExipredInventory")
    public ExpiredInventoryDataResponse getExpiredInventoryReportDataAngular(@RequestParam(value = "warehouseId") Optional<Integer> warehouseId)
			throws IOException {
		log.info("Got request for expiry report download : ");
		return warehouseService.getExpiredInventoryReportData(warehouseId);
	}
}

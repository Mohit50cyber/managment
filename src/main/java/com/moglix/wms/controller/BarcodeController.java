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

import com.moglix.wms.api.request.GenerateBarcodeRequest;
import com.moglix.wms.api.response.GenerateBarcodeResponse;
import com.moglix.wms.service.IBarcodeService;

@RestController
@RequestMapping("/api/barcodes")
public class BarcodeController {
	Logger log = LogManager.getLogger(BarcodeController.class);

	@Autowired
	@Qualifier("barcodeServiceImpl2")
	IBarcodeService barcodeService;
	
	@PostMapping(value = "/")
	public GenerateBarcodeResponse create(@Valid @RequestBody GenerateBarcodeRequest request) {
		log.info("Request received to generate barcode for msn: " + request.getMsn());
		return barcodeService.generateBarcode(request);
	}
}

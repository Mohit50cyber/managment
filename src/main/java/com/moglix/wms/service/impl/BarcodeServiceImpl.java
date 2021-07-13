package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundItem;
import com.moglix.wms.api.request.GenerateBarcodeRequest;
import com.moglix.wms.api.request.GetInboundByIdRequest;
import com.moglix.wms.api.response.GenerateBarcodeResponse;
import com.moglix.wms.api.response.GenerateBarcodeResponse.Result;
import com.moglix.wms.service.IBarcodeService;
import com.moglix.wms.service.IInboundItemService;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.util.BarcodeGenerator;

@Service(value = "barcodeServiceImpl")
public class BarcodeServiceImpl implements IBarcodeService {
	
	Logger log = LogManager.getLogger(BarcodeServiceImpl.class);
	
	GetInboundByIdRequest inboundRequest = new GetInboundByIdRequest();
	@Autowired
	@Qualifier("inboundItemService")
	IInboundItemService inboundItemService;
	
	@Autowired
	@Qualifier("productService")
	IProductService productService;
	
	@Autowired
	IInboundService inboundService;

	@Transactional
	@Override
	public GenerateBarcodeResponse generateBarcode(GenerateBarcodeRequest request) {
		log.info("Barcode Generation started for: " + request.getMsn());
		GenerateBarcodeResponse response = new GenerateBarcodeResponse("Barcodes Generated", true, HttpStatus.OK.value());
		List<Result>results = new ArrayList<>();
		List<InboundItem> inboundItems = new ArrayList<>();
		
		Inbound inbound = inboundService.getById(request.getInboundId());
		
		if(inbound == null) {
			log.info("No inbounds found for Inbound Id: " + request.getInboundId());
			return new GenerateBarcodeResponse("Invalid InboundId Id. No Inbounds found for: " + request.getInboundId(), false, HttpStatus.BAD_REQUEST.value());
		}
		
		//Check constraints and throw error if they fail
		
		if(request.getIsSerializedProduct() && request.getSerialNumbers().size() != request.getQuantity()) {

			return new GenerateBarcodeResponse(
					"Invalid Request: Invalid quantity or serial_numbers. Number of serial numbers specified must be equal to the quantity",
					false, HttpStatus.BAD_REQUEST.value());
		}else {
			for (int i = 0; i < Math.ceil(request.getQuantity()); i++) {
				BarcodeGenerator generator = new BarcodeGenerator();
				Result result = new Result();
				result.setBarcode(generator.toHexString());
				results.add(result);
				InboundItem inboundItem = new InboundItem();
				inboundItem.setBarcode(generator.toHexString());
				inboundItem.setInbound(inbound);
				if(request.getIsSerializedProduct()) {
					result.setSerialNumber(request.getSerialNumbers().get(i));
					inboundItem.setSerialNumber(request.getSerialNumbers().get(i));
				}				
				inboundItems.add(inboundItem);
			}
			inboundItemService.saveInbounds(inboundItems);
			response.setResults(results);
		}		
		
		log.info("Barcodes successfully created");
		return response;
		
	}
	
}

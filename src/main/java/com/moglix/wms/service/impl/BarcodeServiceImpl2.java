package com.moglix.wms.service.impl;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundItem;
import com.moglix.wms.entities.Product;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.api.request.GenerateBarcodeRequest;
import com.moglix.wms.api.request.GetInboundByIdRequest;
import com.moglix.wms.api.response.GenerateBarcodeResponse;
import com.moglix.wms.api.response.GenerateBarcodeResponse.Result;
import com.moglix.wms.service.IBarcodeService;
import com.moglix.wms.service.IInboundItemService;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IProductService;

@Service(value = "barcodeServiceImpl2")
public class BarcodeServiceImpl2 implements IBarcodeService {

	Logger log = LogManager.getLogger(BarcodeServiceImpl2.class);

	GetInboundByIdRequest inboundRequest = new GetInboundByIdRequest();
	@Autowired
	@Qualifier("inboundItemService")
	IInboundItemService inboundItemService;

	@Autowired
	@Qualifier("productService")
	IProductService productService;

	@Autowired
	IInboundService inboundService;
	
	@Autowired
	ProductsRepository productRepo;

	// chose a Character random from this String 
	private static final String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "0123456789"
			+ "abcdefghijklmnopqrstuvxyz"; 


	@Transactional
	@Override
	public GenerateBarcodeResponse generateBarcode(GenerateBarcodeRequest request) {
		log.info("Barcode Generation started for: " + request.getMsn());
		GenerateBarcodeResponse response = new GenerateBarcodeResponse("Barcodes Generated", true, HttpStatus.OK.value());
		List<Result>results = new ArrayList<>();
		List<InboundItem> inboundItems = new ArrayList<>();

		Inbound inbound = inboundService.getById(request.getInboundId());
		boolean updateStatus=false;

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
			int count=0;
			do {
				try {
					updateStatus=createInbound(request, response, results, inboundItems, inbound);
				}catch (Exception e) {
					log.error("Barcodes  creation failed:",e.fillInStackTrace());
				}
				count++;
			}while(!updateStatus&&count<4);
		}		
		
		log.info("Barcodes successfully created");
		return response;

	}

	private boolean createInbound(GenerateBarcodeRequest request, GenerateBarcodeResponse response, List<Result> results,
			List<InboundItem> inboundItems, Inbound inbound) throws ParseException {

		for (int i = 0; i < Math.ceil(request.getQuantity()); i++) {
			Result result = new Result();
			//DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			//formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			//Date expirydate = (Date) formatter.parse(formatter.format(request.getExpirydate()));
			String barcode = getAlphaNumericString();
			result.setBarcode(barcode);
			result.setProductmsn(request.getMsn());
			result.setSuppliername(inbound.getSupplierName());
			result.setSupplierPoId(inbound.getSupplierPoId());
			result.setExpirydate(request.getExpirydate());
			result.setLotnumber(request.getLotno());
			result.setQuantity(request.getQuantity());
			Product product = productRepo.getUniqueByProductMsn(request.getMsn());
			result.setProductDescription(product.getProductName());
			//result.setUom(inbound.getProduct().getUom());
			//result.setProductBrand(inbound.getProduct().getProductBrand());
			results.add(result);
			InboundItem inboundItem = new InboundItem();
			inboundItem.setBarcode(barcode);
			inboundItem.setInbound(inbound);
			if(request.getIsSerializedProduct()) {
				result.setSerialNumber(request.getSerialNumbers().get(i));
				inboundItem.setSerialNumber(request.getSerialNumbers().get(i));
			}				
			inboundItems.add(inboundItem);
		}
		inboundItemService.saveInbounds(inboundItems);
		response.setResults(results);
		return true;
	}

	public static String getAlphaNumericString() 
	{ 
		StringBuilder sb = new StringBuilder(10); 
		int year=Calendar.getInstance().get(Calendar.YEAR)%100;
		sb.append(year);

		for (int i = 0; i < 8; i++) { 

			// generate a random number between 
			// 0 to AlphaNumericString variable length 
			int index 
			= (int)(AlphaNumericString.length() 
					* Math.random()); 

			// add Character one by one in end of sb 
			sb.append(AlphaNumericString 
					.charAt(index)); 
		} 

		return sb.toString(); 
	} 

}

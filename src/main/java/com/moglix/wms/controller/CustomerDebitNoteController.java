package com.moglix.wms.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.request.CreateCustomerDebitNoteRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreateCustomerDebitNoteResponse;
import com.moglix.wms.service.ICustomerDebitNoteService;

@RestController
@RequestMapping("/api/customerdebitnote")
public class CustomerDebitNoteController {
	
	@Autowired
	private ICustomerDebitNoteService customerDebitNoteService;
	
	private Logger log = LogManager.getLogger(CustomerDebitNoteController.class);
	
	@PostMapping("/")
	public CreateCustomerDebitNoteResponse create(@Valid @RequestBody CreateCustomerDebitNoteRequest request) {
		log.info("Request received to create debit note: " + request);
		return customerDebitNoteService.createBatch(request);
	}
	
	@GetMapping("/cancel/{customerDebitNoteNumber}")
	public BaseResponse cancelCustomerDebitNoteNumber(@PathVariable("customerDebitNoteNumber") String customerDebitNoteNumber) {
		log.info("Request received to cancel debit note: " + customerDebitNoteNumber);
		return customerDebitNoteService.cancelCustomerDebitNoteNumber(customerDebitNoteNumber);
	}

}

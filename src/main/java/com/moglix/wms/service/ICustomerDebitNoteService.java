package com.moglix.wms.service;

import javax.validation.Valid;

import com.moglix.wms.api.request.CreateCustomerDebitNoteRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreateCustomerDebitNoteResponse;

public interface ICustomerDebitNoteService {

	CreateCustomerDebitNoteResponse createBatch(@Valid CreateCustomerDebitNoteRequest request);

	BaseResponse cancelCustomerDebitNoteNumber(String customerDebitNoteNumber);

	
}

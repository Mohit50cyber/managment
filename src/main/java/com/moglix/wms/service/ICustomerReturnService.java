package com.moglix.wms.service;

import com.moglix.wms.api.request.GetReturnFreeInventoryRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;

import com.moglix.wms.api.request.GetReturnFreeInventoryRequest;
import com.moglix.wms.api.request.ReturnBatchRequest;
import com.moglix.wms.api.response.GetAvailableInventoryResponse;
import com.moglix.wms.api.response.GetFreeInventoryResponse;
import com.moglix.wms.api.request.ReturnDetailsRequest;
import com.moglix.wms.api.response.GetReturnDetailsResponse;
import com.moglix.wms.api.response.ReturnBatchResponse;
import com.moglix.wms.api.response.ReturnDetailsResponse;
import com.moglix.wms.api.response.ReturnInvoiceLotResponse;
import com.moglix.wms.api.response.ReturnPacketLotInfoRequest;

public interface ICustomerReturnService {
	ReturnBatchResponse returnBatch(ReturnBatchRequest request);

	GetReturnDetailsResponse getReturnBatchDetailsByInvoiceNumber(String invoiceNumber);

	GetFreeInventoryResponse getReturnFreeInventory(GetReturnFreeInventoryRequest request);

	ReturnDetailsResponse getReturnDetails(@Valid ReturnDetailsRequest request);

	ReturnInvoiceLotResponse getLotInfo(@Valid ReturnPacketLotInfoRequest request, Pageable page);

	GetAvailableInventoryResponse getAvailableInventory(Integer supplierPoId);
}

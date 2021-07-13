package com.moglix.wms.service;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;

import com.moglix.wms.api.request.CreateSupplierCreditNoteByMRNRequest;
import com.moglix.wms.api.request.CreateSupplierCreditNoteByReturnRequest;
import com.moglix.wms.api.request.CreateSupplierDebitNoteByFreeQuantityRequest;
import com.moglix.wms.api.request.DNUpdateStatusRequest;
import com.moglix.wms.api.request.DeductInventorisableRequest;
import com.moglix.wms.api.request.GetReturnPickupListByIdRequest;
import com.moglix.wms.api.request.SearchReturnPickupListRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreateSupplierCreditNoteByMRNResponse;
import com.moglix.wms.api.response.CreateSupplierCreditNoteByReturnResponse;
import com.moglix.wms.api.response.CreateSupplierDebitNoteByFreeQuantityResponse;
import com.moglix.wms.api.response.DNStatusUpdateResponse;
import com.moglix.wms.api.response.DeleteBatchResponse;
import com.moglix.wms.api.response.GetReturnPickupListByIdResponse;
import com.moglix.wms.api.response.GetSuppliersByPacketResponse;
import com.moglix.wms.api.response.GetSuppliersByReturnPacketResponse;
import com.moglix.wms.api.response.SearchReturnPacketsResponse;
import com.moglix.wms.api.response.SearchReturnPickupListResponse;
import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.entities.ReturnPickupList;

public interface ISupplierReturnService {
	ReturnPickupList upsert(ReturnPickupList obj);
	ReturnPickupList getById(Integer id);

	SearchReturnPickupListResponse searchReturnPickup(SearchReturnPickupListRequest request, Pageable page);
	GetReturnPickupListByIdResponse getReturnPickupById(GetReturnPickupListByIdRequest request);
	CreateSupplierCreditNoteByMRNResponse createSupplierCreditNoteByMRN(CreateSupplierCreditNoteByMRNRequest data);
	GetSuppliersByPacketResponse getSuppliersByPacket(Integer id);
	SearchReturnPacketsResponse getPacketReturns(Integer warehouseId, String searchKey, PacketStatus status, Pageable page);
	CreateSupplierCreditNoteByReturnResponse createSupplierCreditNoteByReturn(
			CreateSupplierCreditNoteByReturnRequest data);
	DNStatusUpdateResponse dnStatusUpdate(DNUpdateStatusRequest request);
	GetSuppliersByReturnPacketResponse getSuppliersByReturnPacket(Integer emsReturnId);
	DeleteBatchResponse checkIfBatchIsDeletable(Integer emsReturnId);
	DeleteBatchResponse cancelReturn(Integer emsReturnId);
	BaseResponse checkDebiNoteEligiblity(Integer emsReturnId, Integer supplierId);
	BaseResponse deductInventorisable(@Valid DeductInventorisableRequest request);
	BaseResponse cancelReturnPickupList(Integer emsreturnNoteId);
	CreateSupplierDebitNoteByFreeQuantityResponse createSupplierCreditNoteByFreeQuantity(
			CreateSupplierDebitNoteByFreeQuantityRequest data);
}

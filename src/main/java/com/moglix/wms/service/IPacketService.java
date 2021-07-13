package com.moglix.wms.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;

import com.moglix.wms.api.request.CreatePacketRequest;
import com.moglix.wms.api.request.DeletePacketRequest;
import com.moglix.wms.api.request.GetPacketByIdRequest;
import com.moglix.wms.api.request.MSNListRequest;
import com.moglix.wms.api.request.PacketLotInfoRequest;
import com.moglix.wms.api.request.ReturnBatchRequest.PacketQuantityMapping;
import com.moglix.wms.api.request.SearchPacketForPickupRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreatePacketResponse;
import com.moglix.wms.api.response.DeductInboundStorageResponse;
import com.moglix.wms.api.response.DeletePacketResponse;
import com.moglix.wms.api.response.GetPacketByIdResponse;
import com.moglix.wms.api.response.GetTPByEmsPacketIdResponse;
import com.moglix.wms.api.response.MSNListResponse;
import com.moglix.wms.api.response.PacketLotResponse;
import com.moglix.wms.api.response.SearchPacketForPickupResponse;
import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.dto.ReturnDetail;
import com.moglix.wms.entities.Packet;

/**
 * 
 * @author Vaibhav Thapliyal
 * Date: 7/05/2019
 *
 */
public interface IPacketService {

	Packet upsert(Packet obj);

	Packet getById(Integer id);

	CreatePacketResponse createPacket(CreatePacketRequest request);
	
	DeletePacketResponse deletePacket(DeletePacketRequest request);

	SearchPacketForPickupResponse searchPacketForPickup(SearchPacketForPickupRequest request, Pageable page);

	Optional<Packet> findByEmsPacketIdAndStatusNot(Integer emsPacketId, PacketStatus cancelled);

	void inboundPacket(Packet packet);

	GetPacketByIdResponse getPacketById(GetPacketByIdRequest request);

	void returnPacket(Packet packet, List<PacketQuantityMapping> packetQuantityMapping, String string, Integer emsReturnId);

	Optional<Packet> findByEmsPacketId(Integer emsPacketId);

	List<Packet> getByIdIn(List<Integer> ids);

	DeductInboundStorageResponse deductInboundStorages(Integer emsPacketId);

	GetTPByEmsPacketIdResponse getTransferPriceByPacketId(Integer emsPacketId);

	BaseResponse markShipped(Integer emsPacketId);

	Set<String> findUnshippedOrders();

	Optional<Packet> findByInvoiceNumberAndStatusNot(String invoiceNumber, PacketStatus status);

	List<ReturnDetail> getReturnPacketDetails(String invoiceNumber);

	List<Packet> findByInvoiceNumbersIn(List<String> invoiceNumbers);

	BaseResponse markScanned(Integer emsPacketId);

	PacketLotResponse getLotInfo(@Valid PacketLotInfoRequest request, Pageable page);
	
	MSNListResponse getMSNList(MSNListRequest request);
	
	SearchPacketForPickupResponse appSearchPacketForPickup(SearchPacketForPickupRequest request, Pageable page,String authName);

}

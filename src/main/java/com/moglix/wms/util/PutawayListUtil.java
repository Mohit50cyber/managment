package com.moglix.wms.util;

import com.moglix.wms.dto.PutawayListItemsDTO;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.PutawayList;

public class PutawayListUtil {
	
	private PutawayListUtil() {

	}
	
	
	public static PutawayListItemsDTO getPutAwayListItemsDTOFromInboundStorage(InboundStorage storage) {
		PutawayListItemsDTO putawayListDTO = new PutawayListItemsDTO();
		putawayListDTO.setLotNumber(storage.getLotNumber());
		putawayListDTO.setId(storage.getId());
		putawayListDTO.setQuantity(storage.getQuantity());
		putawayListDTO.setSupplierName(storage.getInbound().getSupplierName());
		putawayListDTO.setSupplierId(storage.getInbound().getSupplierId());
		putawayListDTO.setProductMsn(storage.getInbound().getProduct().getProductMsn());
		putawayListDTO.setProductName(storage.getInbound().getProductName());
		putawayListDTO.setUom(storage.getInbound().getUom());
		putawayListDTO.setZoneId(storage.getStorageLocation().getZone().getName());
		putawayListDTO.setRackId(storage.getStorageLocation().getRack().getName());
		putawayListDTO.setBinId(storage.getStorageLocation().getBin().getName());
		putawayListDTO.setLocation(storage.getStorageLocation().getName());
		
		return putawayListDTO;

	}
	
	public static PutawayList getPutAwayListFromInboundStorage(InboundStorage storage) {
		PutawayList putawayList = new PutawayList();
		putawayList.setSupplierName(storage.getInbound().getSupplierName());
		putawayList.setWarehouseId(storage.getInbound().getWarehouseId());
		putawayList.setSupplierId(storage.getInbound().getSupplierId());		
		return putawayList;

	}
	
}

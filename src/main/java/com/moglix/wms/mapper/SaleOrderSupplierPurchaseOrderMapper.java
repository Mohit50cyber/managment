package com.moglix.wms.mapper;

import com.moglix.wms.api.request.SaleOrderSupplierPurchaseOrderMappingRequest;
import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMapping;

/**
 * @author sparsh saxena on 9/3/21
 */
public class SaleOrderSupplierPurchaseOrderMapper {

	private SaleOrderSupplierPurchaseOrderMapper() {
		
	}
	
	public static SaleOrderSupplierPurchaseOrderMapping createEntityFromSaleOrderSupplierPurchaseOrderMappingRequest(SaleOrderSupplierPurchaseOrderMappingRequest request) {
		
		SaleOrderSupplierPurchaseOrderMapping soSPOMappingObj = new SaleOrderSupplierPurchaseOrderMapping();
		
		if (request.getWarehouseId() != null) {
			soSPOMappingObj.setWarehouseId(request.getWarehouseId());
		}
		
		if (request.getWarehouseName() != null) {
			soSPOMappingObj.setWarehouseName(request.getWarehouseName());
		}
		
		if (request.getPickUpWarehouseId() != null) {
			soSPOMappingObj.setPickUpWarehouseId(request.getPickUpWarehouseId());
		}
		
		if (request.getPickUpWarehouseName() != null) {
			soSPOMappingObj.setPickUpWarehouseName(request.getPickUpWarehouseName());
		}
		
		if (request.getPurchaseDate() != null) {
			soSPOMappingObj.setPurchaseDate(request.getPurchaseDate());
		}
		
		if (request.getCreatedBy() != null) {
			soSPOMappingObj.setCreatedBy(request.getCreatedBy());
		}
		
		if (request.getSourcerEmail() != null) {
			soSPOMappingObj.setSourcerEmail(request.getSourcerEmail());
		}
		
		if (request.getSupplierId() != null) {
			soSPOMappingObj.setSupplierId(request.getSupplierId());
		}
		
		if (request.getSupplierName() != null) {
			soSPOMappingObj.setSupplierName(request.getSupplierName());
		}
		
		soSPOMappingObj.setStatus(SaleOrderSupplierPurchaseOrderMappingStatus.DEMAND_GENERATED);
		soSPOMappingObj.setIsActive(true);
		
		return soSPOMappingObj;
	}
}

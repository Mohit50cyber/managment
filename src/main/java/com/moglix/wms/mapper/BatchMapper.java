package com.moglix.wms.mapper;

import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.dto.BatchCSVRecordContent;
import com.moglix.wms.dto.EMSInventoryDTO;
import com.moglix.wms.entities.Batch;

public class BatchMapper {
	private BatchMapper() {

	}

	public static Batch createEntityFromInput(CreateBatchRequest request) {
		Batch batch = new Batch();
		if (request.getRefNo() != null) {
			batch.setRefNo(request.getRefNo());
		}		
		if(request.getEmsReturnId() != null) {
			batch.setEmsReturnId(request.getEmsReturnId());
		}
		if (request.getBatchCode() != null) {
			batch.setBatchCode(request.getBatchCode());
		}
		if (request.getBatchType() != null) {
			batch.setBatchType(request.getBatchType());
		}
		if (request.getWarehouseId() != null) {
			batch.setWarehouseId(request.getWarehouseId());
		}
		if (request.getWarehouseName() != null) {
			batch.setWarehouseName(request.getWarehouseName());
		}
		if (request.getPurchaseDate() != null) {
			batch.setPurchaseDate(request.getPurchaseDate());
		}
		if (request.getInboundedBy() != null) {
			batch.setInboundedBy(request.getInboundedBy());
		}
		if (request.getSupplierId() != null) {
			batch.setSupplierId(request.getSupplierId());
		}
		
		if (request.getSupplierName() != null) {
			batch.setSupplierName(request.getSupplierName());
		}
		
		if(request.getParentRefNo() != null) {
			batch.setParentRefNo(request.getParentRefNo());
		}
		
		return batch;
	}
	
	public static Batch createBatchFromCsvRecord(BatchCSVRecordContent request) {
		Batch batch = new Batch();
		if (request.getRefNo() != null) {
			batch.setRefNo(request.getRefNo());
		}		
		
		if (request.getBatchCode() != null) {
			batch.setBatchCode(request.getBatchCode());
		}
		if (request.getBatchType() != null) {
			batch.setBatchType(request.getBatchType());
		}
		if (request.getWarehouseId() != null) {
			batch.setWarehouseId(request.getWarehouseId());
		}
		if (request.getWarehouseName() != null) {
			batch.setWarehouseName(request.getWarehouseName());
		}
		if (request.getPurchaseDate() != null) {
			batch.setPurchaseDate(request.getPurchaseDate());
		}
		if (request.getInboundedBy() != null) {
			batch.setInboundedBy(request.getInboundedBy());
		}
		if (request.getSupplierId() != null) {
			batch.setSupplierId(request.getSupplierId());
		}
		
		if (request.getSupplierName() != null) {
			batch.setSupplierName(request.getSupplierName());
		}
		return batch;
	}
	
	public static Batch createBatchFromEmsInventory(EMSInventoryDTO request) {
		Batch batch = new Batch();

		if (request.getMrnId() != null) {
			batch.setRefNo(String.valueOf(request.getMrnId()));
		}

		batch.setBatchType(BatchType.INBOUND);

		if (request.getWarehouseId() != null) {
			batch.setWarehouseId(request.getWarehouseId());
		}

		if (request.getMrnDate() != null) {
			batch.setPurchaseDate(request.getMrnDate());
		}
		batch.setInboundedBy("Generated by Automatic Migration");

		if (request.getSupplierId() != null) {
			batch.setSupplierId(request.getSupplierId());
		}

		if (request.getSupplierName() != null) {
			batch.setSupplierName(request.getSupplierName());
		}
		return batch;
	}
	
}
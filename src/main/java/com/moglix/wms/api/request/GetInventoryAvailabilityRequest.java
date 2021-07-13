package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.moglix.wms.constants.StorageLocationType;

public class GetInventoryAvailabilityRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2901766098231566170L;
	
	@NotNull
	private List<String> productMSNList;
	
	@NotNull
	private Integer warehouseId;
	
	private StorageLocationType storageLocationtype =  StorageLocationType.GOOD;

	private String bulkInvoiceId;
	
	public List<String> getProductMSNList() {
		return productMSNList;
	}

	public void setProductMSNList(List<String> productMSNList) {
		this.productMSNList = productMSNList;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public StorageLocationType getStorageLocationtype() {
		return storageLocationtype;
	}

	public void setStorageLocationtype(StorageLocationType storageLocationtype) {
		this.storageLocationtype = storageLocationtype;
	}
	
	public String getBulkInvoiceId() {
		return bulkInvoiceId;
	}

	public void setBulkInvoiceId(String bulkInvoiceId) {
		this.bulkInvoiceId = bulkInvoiceId;
	}

	@Override
	public String toString() {
		return "GetInventoryAvailabilityRequest [productMSNList=" + productMSNList + ", warehouseId=" + warehouseId
				+ ", storageLocationtype=" + storageLocationtype + ", bulkInvoiceId=" + bulkInvoiceId + "]";
	}
}

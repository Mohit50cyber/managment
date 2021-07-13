package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.api.request.CreateSupplierCreditNoteByReturnRequest.ReturnPickupListItemMapping;
import com.moglix.wms.validator.CheckValidWarehouse;

public class CreateSupplierDebitNoteByFreeQuantityRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4284040504080354064L;

	@NotNull
	@Size(min  = 1)
	List<ReturnPickupListItemMapping> items;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;

	private String debitNoteNumber;
	
	@NotNull
	private Integer supplierId;
	
	@NotNull
	private Integer supplierPoId;
	
	@NotBlank
	private String supplierName;
	
	@NotNull
	private String emsReturnNoteId;
	
	@NotNull
	private String status;

	public List<ReturnPickupListItemMapping> getItems() {
		return items;
	}

	public void setItems(List<ReturnPickupListItemMapping> items) {
		this.items = items;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getDebitNoteNumber() {
		return debitNoteNumber;
	}

	public void setDebitNoteNumber(String debitNoteNumber) {
		this.debitNoteNumber = debitNoteNumber;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	
	public String getEmsReturnNoteId() {
		return emsReturnNoteId;
	}

	public void setEmsReturnNoteId(String emsReturnNoteId) {
		this.emsReturnNoteId = emsReturnNoteId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CreateSupplierDebitNoteByFreeQuantityRequest [items=" + items + ", warehouseId=" + warehouseId
				+ ", debitNoteNumber=" + debitNoteNumber + ", supplierId=" + supplierId + ", supplierPoId="
				+ supplierPoId + ", supplierName=" + supplierName + ", emsreturnnoteid=" + emsReturnNoteId + ", status="
				+ status + "]";
	}
}



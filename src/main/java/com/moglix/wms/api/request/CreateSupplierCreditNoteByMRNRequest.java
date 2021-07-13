package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.validator.CheckValidWarehouse;

public class CreateSupplierCreditNoteByMRNRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6792842197081491469L;
	
	@NotNull
	private String refNo;
	
	@NotNull
	@Size(min  = 1)
	List<CreditNoteItems> items;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;

	private String creditNoteNumber;
	
	@NotNull
	private String emsReturnNoteId;
	
	@NotNull
	private String status;
	
	public static class CreditNoteItems implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -3344116167545508287L;

		private String productMsn;
		private Double quantity;
		
		private Integer supplierPoItemId;

		public String getProductMsn() {
			return productMsn;
		}

		public void setProductMsn(String productMsn) {
			this.productMsn = productMsn;
		}

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public Integer getSupplierPoItemId() {
			return supplierPoItemId;
		}

		public void setSupplierPoItemId(Integer supplierPoItemId) {
			this.supplierPoItemId = supplierPoItemId;
		}
	}
	
	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getCreditNoteNumber() {
		return creditNoteNumber;
	}

	public void setCreditNoteNumber(String creditNoteNumber) {
		this.creditNoteNumber = creditNoteNumber;
	}

	public List<CreditNoteItems> getItems() {
		return items;
	}

	public void setItems(List<CreditNoteItems> items) {
		this.items = items;
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


}

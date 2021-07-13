package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.validator.CheckValidWarehouse;

public class CreateSupplierCreditNoteByReturnRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1323043770944901069L;

	@NotNull
	private Integer supplierId;
	
	@NotNull
	private Integer supplierPoId;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@NotNull
	private Integer emsReturnId;
	
	private String creditNoteNumber;
	
	@NotBlank
	private String supplierName;
	
	@NotNull
	private String emsReturnNoteId;
	
	@NotNull
	private String status;

	@NotNull
	@Size(min  = 1)
	private List<ReturnPickupListItemMapping> returnPickupListItemMapping;
	
	public static class ReturnPickupListItemMapping implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6998239101296617099L;
		
		private String productMsn;
		
		private Integer inboundId;

		public Integer getInboundId() {
			return inboundId;
		}

		public void setInboundId(Integer inboundId) {
			this.inboundId = inboundId;
		}

		private Double quantity;

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

		@Override
		public String toString() {
			return "ReturnPickupListItemMapping [productMsn=" + productMsn + ", inboundId=" + inboundId + ", quantity="
					+ quantity + "]";
		}
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
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

	public String getSupplierName() {
		return supplierName;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}

	public List<ReturnPickupListItemMapping> getReturnPickupListItemMapping() {
		return returnPickupListItemMapping;
	}

	public void setReturnPickupListItemMapping(List<ReturnPickupListItemMapping> returnPickupListItemMapping) {
		this.returnPickupListItemMapping = returnPickupListItemMapping;
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
		return "CreateSupplierCreditNoteByReturnRequest [supplierId=" + supplierId + ", supplierPoId=" + supplierPoId
				+ ", warehouseId=" + warehouseId + ", emsReturnId=" + emsReturnId + ", creditNoteNumber="
				+ creditNoteNumber + ", supplierName=" + supplierName + ", emsreturnnoteid=" + emsReturnNoteId
				+ ", status=" + status + ", returnPickupListItemMapping=" + returnPickupListItemMapping + "]";
	}
	
	
	
}

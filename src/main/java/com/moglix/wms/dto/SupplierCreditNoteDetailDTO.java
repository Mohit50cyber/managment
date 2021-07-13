package com.moglix.wms.dto;

import java.io.Serializable;

public class SupplierCreditNoteDetailDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9053142048892351163L;
	
	private Integer supplierPoItemId;
	
	private String productName;
	
	private String productMsn;
	
	private Double returnedQuantity;
	
	private Double creditDoneQuantity = 0.0d;
	
	private Double inventrisableQuantiy = 0.0d;
	
	private Double tax;
	
	private Double purchasePrice;
	
	private Integer inboundId;
	

	public Integer getSupplierPoItemId() {
		return supplierPoItemId;
	}

	public void setSupplierPoItemId(Integer supplierPoItemId) {
		this.supplierPoItemId = supplierPoItemId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Double getInventrisableQuantiy() {
		return inventrisableQuantiy;
	}

	public void setInventrisableQuantiy(Double inventrisableQuantiy) {
		this.inventrisableQuantiy = inventrisableQuantiy;
	}

	public Double getReturnedQuantity() {
		return returnedQuantity;
	}

	public void setReturnedQuantity(Double returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}

	public Double getCreditDoneQuantity() {
		return creditDoneQuantity;
	}

	public void setCreditDoneQuantity(Double creditDoneQuantity) {
		this.creditDoneQuantity = creditDoneQuantity;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}
	
	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public Integer getInboundId() {
		return inboundId;
	}

	public void setInboundId(Integer inboundId) {
		this.inboundId = inboundId;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		
		if(obj instanceof SupplierCreditNoteDetailDTO) {
			SupplierCreditNoteDetailDTO supplierCreditNoteDetail = (SupplierCreditNoteDetailDTO) obj;
			return (supplierCreditNoteDetail.productMsn.equals(this.productMsn) && Integer.compare(supplierCreditNoteDetail.supplierPoItemId, this.supplierPoItemId) == 0);
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hashcode = 0;
		hashcode = supplierPoItemId *20;
		hashcode += productMsn.hashCode();
		return hashcode;
	}
}

package com.moglix.wms.dto;

import java.io.Serializable;

import com.moglix.wms.validator.CheckValidTaxRate;

public class CutomerDebitNoteReturnDetailDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3732102108226172167L;
	
	private Integer InboundId;
	
	private Integer emsReturnId;
	
	private Integer supplierId;
	
	private String supplierName;
	
	private Double purchasePrice;
	
	private Integer supplierPoId;
	
	private Integer supplierPoItemId;
	
	@CheckValidTaxRate
	private Double tax;

	private String productMsn;
	
	private String productName;
	
	private String uom;
	
	private Double quantity;

	public Integer getInboundId() {
		return InboundId;
	}

	public void setInboundId(Integer inboundId) {
		InboundId = inboundId;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}


	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}

	public Integer getSupplierPoItemId() {
		return supplierPoItemId;
	}

	public void setSupplierPoItemId(Integer supplierPoItemId) {
		this.supplierPoItemId = supplierPoItemId;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "CutomerDebitNoteReturnDetailDTO [InboundId=" + InboundId + ", emsReturnId=" + emsReturnId
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", purchasePrice=" + purchasePrice
				+ ", supplierPodId=" + supplierPoId + ", supplierPoItemId=" + supplierPoItemId + ", tax=" + tax
				+ ", productMsn=" + productMsn + ", productName=" + productName + ", uom=" + uom + ", quantity="
				+ quantity + "]";
	}
}

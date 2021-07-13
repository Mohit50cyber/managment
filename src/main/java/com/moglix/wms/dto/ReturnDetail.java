package com.moglix.wms.dto;

import java.io.Serializable;

public class ReturnDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 691936737880909525L;

	private Integer emsReturnId;
	
	private Integer emsReturnItemId;
	
	private Double returnedQuantity;
	
	private Double debitDoneQuantity;
	
	private Integer supplierId;
	
	private String supplierName;
	
	private Integer warehouseId;
	
	private String warehouseName;
	
	private Double purchasePrice;
	
	private Integer supplierPoId;
	
	private Integer supplierPoItemId;
	
	private Double tax;
	
	private String productMsn;
	
	private String productName;
	
	private String uom;
	
	private Integer inboundId;

	public Integer getInboundId() {
		return inboundId;
	}

	public void setInboundId(Integer inboundId) {
		this.inboundId = inboundId;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public Integer getEmsReturnItemId() {
		return emsReturnItemId;
	}

	public void setEmsReturnItemId(Integer emsReturnItemId) {
		this.emsReturnItemId = emsReturnItemId;
	}

	public Double getReturnedQuantity() {
		return returnedQuantity;
	}

	public void setReturnedQuantity(Double returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}

	public Double getDebitDoneQuantity() {
		return debitDoneQuantity;
	}

	public void setDebitDoneQuantity(Double debitDoneQuantity) {
		this.debitDoneQuantity = debitDoneQuantity;
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

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
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
	
	
}

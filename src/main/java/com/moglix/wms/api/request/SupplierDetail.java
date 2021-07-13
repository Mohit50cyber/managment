package com.moglix.wms.api.request;

public class SupplierDetail {
	private String mrnId;
	private Integer supplierId;
	private String supplierName;
	private Integer supplierPoId;
	private Integer supplierPoItemId;
	private Double transferPrice;
	private Double tax;
	private Double quantity;

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getMrnId() {
		return mrnId;
	}

	public void setMrnId(String mrnId) {
		this.mrnId = mrnId;
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

	public Double getTransferPrice() {
		return transferPrice;
	}

	public void setTransferPrice(Double transferPrice) {
		this.transferPrice = transferPrice;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}
}
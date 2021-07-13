package com.moglix.wms.api.response;

import java.io.Serializable;

public class Allocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6267766165136920457L;

	private String mrnId;
	
	private Integer supplierPoId;
	
	private Integer supplierPoItemId;
	
	private Double quantity;

	public String getMrnId() {
		return mrnId;
	}

	public void setMrnId(String mrnId) {
		this.mrnId = mrnId;
	}

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
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

	@Override
	public String toString() {
		return "Allocation [mrnId=" + mrnId + ", supplierPoId=" + supplierPoId + ", supplierPoItemId="
				+ supplierPoItemId + ", quantity=" + quantity + "]";
	}
}

package com.moglix.wms.api.request;

import java.util.List;

public class FakeReturnBatchMapping {
	private String productMsn;
	private Double quantity;
	private Integer orderItemId;
	private Double orderQuantity;
	private Integer emsOrderId;
	List<SupplierDetail> suppliers;

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

	public Integer getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Double getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(Double orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public Integer getEmsOrderId() {
		return emsOrderId;
	}

	public void setEmsOrderId(Integer emsOrderId) {
		this.emsOrderId = emsOrderId;
	}

	public List<SupplierDetail> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<SupplierDetail> suppliers) {
		this.suppliers = suppliers;
	}
}
package com.moglix.wms.dto;

import java.util.Date;

public class EMSInventoryDTO {
	private Integer warehouseId;
	private Integer mrnId;
	private Integer poId;
	private Integer poItemId;
	private Integer supplierId;
	private Date mrnDate;
	private String productMpn;
	private String productName;
	private String brandName;
	private String supplierName;
	private String productUnit;
	private Double arrivedQuantity;
	private Double taxRate;
	private Double transferPrice;
	
	public EMSInventoryDTO(EMSInventory emsInventory) {
		warehouseId = emsInventory.getWarehouseId();
		mrnId = emsInventory.getMrnId();
		poId = emsInventory.getPoId();
		poItemId = emsInventory.getPoItemId();
		supplierId = emsInventory.getSupplierId();
		mrnDate = emsInventory.getMrnDate();
		productMpn = emsInventory.getProductMpn();
		productName = emsInventory.getProductName();
		brandName = emsInventory.getBrandName();
		supplierName = emsInventory.getSupplierName();
		productUnit = emsInventory.getProductUnit();
		arrivedQuantity = emsInventory.getArrivedQuantity();
		taxRate = emsInventory.getTaxRate();
		transferPrice = emsInventory.getTransferPrice();
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	public Integer getMrnId() {
		return mrnId;
	}
	public void setMrnId(Integer mrnId) {
		this.mrnId = mrnId;
	}
	public Integer getPoId() {
		return poId;
	}
	public void setPoId(Integer poId) {
		this.poId = poId;
	}
	public Integer getPoItemId() {
		return poItemId;
	}
	public void setPoItemId(Integer poItemId) {
		this.poItemId = poItemId;
	}
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	public Date getMrnDate() {
		return mrnDate;
	}
	public void setMrnDate(Date mrnDate) {
		this.mrnDate = mrnDate;
	}
	public String getProductMpn() {
		return productMpn;
	}
	public void setProductMpn(String productMpn) {
		this.productMpn = productMpn;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getProductUnit() {
		return productUnit;
	}
	public void setProductUnit(String productUnit) {
		this.productUnit = productUnit;
	}
	public Double getArrivedQuantity() {
		return arrivedQuantity;
	}
	public void setArrivedQuantity(Double arrivedQuantity) {
		this.arrivedQuantity = arrivedQuantity;
	}
	public Double getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}
	public Double getTransferPrice() {
		return transferPrice;
	}
	public void setTransferPrice(Double transferPrice) {
		this.transferPrice = transferPrice;
	}
}

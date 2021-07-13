package com.moglix.wms.api.request;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.gson.Gson;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.validator.CheckValidWarehouse;

public class CreateBatchRequest extends BaseRequest{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2350691437297642500L;
	
	@NotNull
	private String refNo;
	
	private String parentRefNo;
	
	private Integer batchCode;
	
	private Integer emsPacketId;	
	
	private Integer emsReturnId;
		
	@NotNull
	private BatchType batchType;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@NotNull
	private Date purchaseDate;
	
	@NotNull
	private String inboundedBy;
	
	@NotNull
	private Integer supplierId;
	
	@NotBlank
	private String supplierName;
	
	@NotBlank
	private String warehouseName;
	
	@Valid
	@NotNull
	@Size(min = 1)
	private List<ProductInput>products;
	
	public String getRefNo() {
		return refNo;
	}
	
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	
	public String getParentRefNo() {
		return parentRefNo;
	}
	
	public void setParentRefNo(String parentRefNo) {
		this.parentRefNo = parentRefNo;
	}
	
	public Integer getBatchCode() {
		return batchCode;
	}
	
	public void setBatchCode(Integer batchCode) {
		this.batchCode = batchCode;
	}
	
	public Integer getEmsPacketId() {
		return emsPacketId;
	}
	
	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}
	
	public Integer getEmsReturnId() {
		return emsReturnId;
	}
	
	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}
	
	public BatchType getBatchType() {
		return batchType;
	}
	
	public void setBatchType(BatchType batchType) {
		this.batchType = batchType;
	}
	
	public Integer getWarehouseId() {
		return warehouseId;
	}
	
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	public String getInboundedBy() {
		return inboundedBy;
	}
	
	public void setInboundedBy(String inboundedBy) {
		this.inboundedBy = inboundedBy;
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
	
	public String getWarehouseName() {
		return warehouseName;
	}
	
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	
	public List<ProductInput> getProducts() {
		return products;
	}
	public void setProducts(List<ProductInput> products) {
		this.products = products;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
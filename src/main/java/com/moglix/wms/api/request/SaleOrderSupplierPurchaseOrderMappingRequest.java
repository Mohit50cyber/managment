package com.moglix.wms.api.request;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.gson.Gson;
import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.validator.CheckValidWarehouse;

/**
 * @author sparsh saxena on 9/3/21
 */

public class SaleOrderSupplierPurchaseOrderMappingRequest extends BaseRequest  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8468641883327876358L;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@NotBlank
	private String warehouseName;
	
	@NotNull
	private Integer pickUpWarehouseId;
	
	@NotBlank
	private String pickUpWarehouseName;
	
	
	private Date purchaseDate;
	
	@NotBlank
	private String createdBy;
	
	@NotBlank
	private String sourcerEmail;
	
	@NotNull
	private Integer supplierId;
	
	@NotBlank
	private String supplierName;
	
	
    private SaleOrderSupplierPurchaseOrderMappingStatus status = SaleOrderSupplierPurchaseOrderMappingStatus.DEMAND_GENERATED;
	
	@Valid
	@NotNull
	@Size(min = 1)
	private List<ProductInput>products;
	

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

	public Integer getPickUpWarehouseId() {
		return pickUpWarehouseId;
	}

	public void setPickUpWarehouseId(Integer pickUpWarehouseId) {
		this.pickUpWarehouseId = pickUpWarehouseId;
	}

	public String getPickUpWarehouseName() {
		return pickUpWarehouseName;
	}

	public void setPickUpWarehouseName(String pickUpWarehouseName) {
		this.pickUpWarehouseName = pickUpWarehouseName;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getSourcerEmail() {
		return sourcerEmail;
	}

	public void setSourcerEmail(String sourcerEmail) {
		this.sourcerEmail = sourcerEmail;
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

	public SaleOrderSupplierPurchaseOrderMappingStatus getStatus() {
		return status;
	}

	public void setStatus(SaleOrderSupplierPurchaseOrderMappingStatus status) {
		this.status = status;
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

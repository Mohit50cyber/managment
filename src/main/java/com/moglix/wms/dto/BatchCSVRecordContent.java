package com.moglix.wms.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.DangerType;
import com.moglix.wms.constants.ProductType;
import com.moglix.wms.constants.StorageType;
import com.moglix.wms.validator.CheckValidTaxRate;
import com.moglix.wms.validator.CheckValidWarehouse;

public class BatchCSVRecordContent implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5608730480093937262L;

	@JsonProperty("batch_code")
	private Integer batchCode;
	
	@JsonProperty("ref_no")
	private String refNo;
	
	@JsonProperty("batch_type")
	private BatchType batchType;
	
	@JsonProperty("warehouse_id")
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@JsonProperty("warehouse_name")
	private String warehouseName;
	
	@JsonProperty("supplier_id")
	private Integer supplierId;
	
	@JsonProperty("supplier_name")
	private String supplierName;
	
	@JsonProperty("purchase_date")
	private Date purchaseDate;
	
	@JsonProperty("inbounded_by")
	private String inboundedBy;
	
	@JsonProperty("supplier_po_id")
	private Integer supplierPoId;
	
	@JsonProperty("supplier_po_item_id")
	private Integer supplierPoItemId;
	
	@JsonProperty("product_type")
	private ProductType productType;
	
	@JsonProperty("product_msn")
	private String productMsn;
	
	@JsonProperty("product_name")
	private String productName;
	
	@JsonProperty("quantity")
	private Double quantity;
	
	@JsonProperty("uom")
	private String uom;
	
	@JsonProperty("purchase_price")
	private Double purchasePrice;	
	
	@JsonProperty("tax_rate")
	@CheckValidTaxRate
	private Double taxRate;
	
	@JsonProperty("storage_type")
	private StorageType storageType;
	
	@JsonProperty("danger_type")
	private DangerType dangerType;
	
	@JsonProperty("is_serialized")
	private Boolean isSerialized;
	
	@JsonProperty("mfg_date")
	private Date mfgDate;
	
	@JsonProperty("expiry_date")
	private Date expiryDate;

	public Integer getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(Integer batchCode) {
		this.batchCode = batchCode;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
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

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
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

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
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

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public DangerType getDangerType() {
		return dangerType;
	}

	public void setDangerType(DangerType dangerType) {
		this.dangerType = dangerType;
	}

	public Boolean getIsSerialized() {
		return isSerialized;
	}

	public void setIsSerialized(Boolean isSerialized) {
		this.isSerialized = isSerialized;
	}

	public Date getMfgDate() {
		return mfgDate;
	}

	public void setMfgDate(Date mfgDate) {
		this.mfgDate = mfgDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
}

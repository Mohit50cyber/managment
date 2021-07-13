package com.moglix.wms.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moglix.wms.constants.DangerType;
import com.moglix.wms.constants.ProductType;
import com.moglix.wms.constants.StorageType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCSVRecordContent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4971223045491264914L;


	@JsonProperty("product_msn")
	private String productMsn;
	
	@JsonProperty("brand_name")
	private String productBrand;
	
	@JsonProperty("product_name")
	private String productName;
	
	@JsonProperty("uom")
	private String uom;
	
	@JsonProperty("product_type")
	private ProductType productType = ProductType.DURABLE;
	
	@JsonProperty("total_quantity")
	private Double totalQuantity = 0.0;
	
	@JsonProperty("current_quantity")
	private Double currentQuantity = 0.0;
	
	@JsonProperty("storage_type")
	private StorageType storageType = StorageType.ROOM_TEMP;
	
	@JsonProperty("danger_type")
	private DangerType dangerType ;
	
	@JsonProperty("serialized_product")
	private Boolean serializedProduct=false;
	
	@JsonProperty("lot_management_enabled")
	private Boolean lotManagementEnabled=false;
	
	@JsonProperty("expiry_date_management_enabled")
	private Boolean expiryDateManagementEnabled=false;

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

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Double getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(Double currentQuantity) {
		this.currentQuantity = currentQuantity;
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

	public Boolean getSerializedProduct() {
		return serializedProduct;
	}

	public void setSerializedProduct(Boolean serializedProduct) {
		this.serializedProduct = serializedProduct;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public Boolean getLotManagementEnabled() {
		return lotManagementEnabled;
	}

	public void setLotManagementEnabled(Boolean lotManagementEnabled) {
		this.lotManagementEnabled = lotManagementEnabled;
	}

	public Boolean getExpiryDateManagementEnabled() {
		return expiryDateManagementEnabled;
	}

	public void setExpiryDateManagementEnabled(Boolean expiryDateManagementEnabled) {
		this.expiryDateManagementEnabled = expiryDateManagementEnabled;
	}	
}

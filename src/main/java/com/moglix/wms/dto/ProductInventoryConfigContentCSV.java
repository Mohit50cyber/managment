package com.moglix.wms.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInventoryConfigContentCSV implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8393136749386128725L;
	
	@JsonProperty("warehouse_id")
	private Integer warehouseId;
	
	@JsonProperty("msn")
	private String productMsn;
	
	@JsonProperty("plant_id")
	private Integer plantId;
	
	@JsonProperty("supplier_id")
	private Integer supplierId;
	
	@JsonProperty("max_qty")
	private Double maxQty;

	@JsonProperty("min_qty")
	private Double minQty;
	
	@JsonProperty("purchase_price")
	private Double purchasePrice;

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Integer getPlantId() {
		return plantId;
	}

	public void setPlantId(Integer plantId) {
		this.plantId = plantId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Double getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(Double maxQty) {
		this.maxQty = maxQty;
	}

	public Double getMinQty() {
		return minQty;
	}

	public void setMinQty(Double minQty) {
		this.minQty = minQty;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Override
	public String toString() {
		return "ProductInventoryConfigContentCSV [warehouseId=" + warehouseId + ", productMsn=" + productMsn
				+ ", plantId=" + plantId + ", supplierId=" + supplierId + ", maxQty=" + maxQty + ", minQty=" + minQty
				+ ", purchasePrice=" + purchasePrice + "]";
	}
}

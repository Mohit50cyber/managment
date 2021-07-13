
package com.moglix.wms.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInboundInventoryImportCSVContent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2255977066901435485L;

	/**
	 * 
	 */
	@JsonProperty("Warehouse Name")
	private String warehouseName;

	@JsonProperty("MSN")
	private String productMsn;

	@JsonProperty("Product Description")
	private String productName;

	@JsonProperty("UOM")
	private String uom;

	@JsonProperty("Quantity in stock")
	private String quantity;

	@JsonProperty("warehouse_id")
	private String warehouseId;

	@JsonProperty("InboundId")
	private String inboundId;

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

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getInboundId() {
		return inboundId;
	}

	public void setInboundId(String inboundId) {
		this.inboundId = inboundId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

package com.moglix.wms.api.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.moglix.wms.validator.CheckValidWarehouse;

public class DeleteInventoryRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = -923437701526394697L;

	@NotNull
	private String productMsn;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@NotNull
	@DecimalMin(value = "0.0001", message = "The value must be positive")
	private Double quantity;

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "DeleteInventoryRequest [productMsn=" + productMsn + ", warehouseId=" + warehouseId + ", quantity="
				+ quantity + "]";
	}
}

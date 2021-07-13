package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class SalesOpsDemandRequest extends BaseRequest {

	@Override
	public String toString() {
		return "SalesOpsDemandRequest [itemref=" + itemref + ", quantity=" + quantity + ", isInventory=" + isInventory
				+ "]";
	}

	public SalesOpsDemandRequest(String itemRef, Double allocatedQuantity, Boolean isInventory) {
		this.itemref = itemRef;
		this.quantity = allocatedQuantity;
		this.isInventory = isInventory;		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -995922116030478888L;

	@NotNull
	private String itemref;
	
	@NotNull
	private Double quantity;
	
	@NotNull
	private Boolean isInventory;

	public Boolean getIsInventory() {
		return isInventory;
	}

	public void setIsInventory(Boolean isInventory) {
		this.isInventory = isInventory;
	}

	public String getItemref() {
		return itemref;
	}

	public void setItemref(String itemref) {
		this.itemref = itemref;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
}

package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.SerializedName;
import com.moglix.wms.constants.PublishSystemType;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author sparsh saxena on 10/5/21
 */
@Data
@NoArgsConstructor
public class InventoryUpdateRequest {
	
	@NotNull (message = "itemRef is mandatory")
	private String itemref;
	
	@NotNull(message = "quantity is mandatory")
	private Double quantity;
	
	@NotNull (message = "is inventory info missing")
	private Boolean isInventory = Boolean.TRUE;
	
    @SerializedName("system")
    private PublishSystemType systemType = PublishSystemType.WMS;
    
	public InventoryUpdateRequest(String itemRef, Double quantity, Boolean isInventory, PublishSystemType systemType) {
		super();
		this.itemref     = itemRef;
		this.quantity    = quantity;
		this.isInventory = isInventory;
		this.systemType  = systemType;
	}
}

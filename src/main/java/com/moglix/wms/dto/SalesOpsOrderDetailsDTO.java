package com.moglix.wms.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author sparsh saxena on 10/5/21
 */

@JsonPropertyOrder({
	"emsOrderId",
    "orderRef",
    "batchRef",
    "bulkInvoiceId",
    "fulfillmentWarehouseId",
    "inventoryId",
    "orderType",
    "plantId",
    "countryId"
})

@Data
@NoArgsConstructor
public class SalesOpsOrderDetailsDTO implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3272453252115305323L;
	
	@JsonProperty("emsOrderId")
    @SerializedName("emsOrderId")
    private Integer emsOrderId;
	
	@JsonProperty("orderRef")
	@SerializedName("orderRef")
	private String orderRef;
    
	@JsonProperty("fulfillmentWarehouseId")
	@SerializedName("fulfillmentWarehouseId")
	private Integer fulfillmentWarehouseId;

	@JsonProperty ("orderType")
	@SerializedName ("orderType")
	private Integer orderType;
	
	@JsonProperty("plantId")
	@SerializedName("plantId")
	private Integer plantId;
	
	@JsonProperty ("countryId")
	@SerializedName ("countryId")
	private Integer countryISONumber;
	
	// Bulk operation properties

	@JsonProperty("batchRef")
	@SerializedName("batchRef")
	private String batchRef;
	
	@JsonProperty("bulkInvoiceId")
	@SerializedName("bulkInvoiceId")
	private String bulkInvoiceId;
	
	@JsonProperty("inventoryId")
	@SerializedName("inventoryId")
	private String inventoryId;
}

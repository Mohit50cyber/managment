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

@JsonPropertyOrder ({
	"emsOrderItemId",
	"itemRef",
	"orderedQuantity",
	"productMsn",
	"remark",
	"cloneOf",
	"isCloned",
})

@Data
@NoArgsConstructor
public class SalesOpsItemDetailsDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 706197560118070783L;
	
	@JsonProperty("emsOrderItemId")
    @SerializedName("emsOrderItemId")
    private Integer emsOrderItemId;
	
	@JsonProperty ("itemRef")
	@SerializedName ("itemRef")
	private String itemRef;
	
	@JsonProperty ("orderedQuantity")
	@SerializedName ("orderedQuantity")
	private String orderedQuantity;
	
	@JsonProperty ("productMsn")
	@SerializedName ("productMsn")
	private String productMsn;
	
	@JsonProperty ("remark")
	@SerializedName ("remark")
	private String remark;
	
	@JsonProperty ("cloneOf")
	@SerializedName ("cloneOf")
	private String cloneOf;
	    
	@JsonProperty ("isCloned")
	@SerializedName ("isCloned")
	private Boolean isCloned;
	
}
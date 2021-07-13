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
	"idProduct",
	"productName",
	"uom",
	"shortDescription",
	"expiryManagement",
	"lotManagement"
})

@Data
@NoArgsConstructor
public class ProductCatalogResponseDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -5153763696267473052L;
	
	@JsonProperty ("idProduct")
	@SerializedName ("idProduct")
	private String productMsn;
	
	@JsonProperty ("productName")
	@SerializedName ("productName")
	private String productName;
	
	@JsonProperty ("uom")
	@SerializedName ("uom")
	private String uom;
	
	@JsonProperty ("shortDescription")
	@SerializedName ("shortDescription")
	private String brand;
	    
	@JsonProperty ("expiryManagement")
	@SerializedName ("expiryManagement")
	private Boolean isExpiryEnabled;
	
	@JsonProperty ("lotManagement")
	@SerializedName ("lotManagement")
	private Boolean isLotEnabled;
}

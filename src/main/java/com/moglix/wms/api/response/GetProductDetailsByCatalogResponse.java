package com.moglix.wms.api.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;
import com.moglix.wms.dto.ProductCatalogResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sparsh saxena on 15/5/21
 */


@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
	"status",
	"message",
	"statusCode",
	"errors",
    "productDetails"
})


@Data
@NoArgsConstructor
public class GetProductDetailsByCatalogResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 294608752544257008L;
	
	@JsonProperty ("status")
	@SerializedName ("status")
	private Boolean status;
	
	@JsonProperty ("message")
	@SerializedName ("message")
	private String message;
	    
	@JsonProperty ("statusCode")
	@SerializedName ("statusCode")
	private Integer statusCode;
	
    @JsonProperty ("errors")
    @SerializedName ("errors")
    private List<Object> errors = new ArrayList<>();
	
	@JsonProperty ("productDetails")
    @SerializedName ("productDetails")
    private List<ProductCatalogResponseDTO> productDetails = new ArrayList<ProductCatalogResponseDTO>();
}

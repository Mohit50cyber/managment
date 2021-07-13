package com.moglix.wms.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sparsh saxena on 10/5/21
 */


@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
        "orderDetails",
        "items"
})

@Data
@NoArgsConstructor
public class SalesOpsOrderDTO implements Serializable{
	
	private static final long serialVersionUID = 991757114544455347L;
	
    @JsonProperty ("orderDetails")
    @SerializedName ("orderDetails")
    private SalesOpsOrderDetailsDTO orderDetails;
    
    @JsonProperty ("items")
    @SerializedName ("items")
    private List<SalesOpsItemDetailsDTO> itemDetails = new ArrayList<SalesOpsItemDetailsDTO>();
    
}

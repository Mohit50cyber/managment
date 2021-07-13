package com.moglix.wms.api.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class GeneratePickupListByMSNRequest extends BaseRequest{
	
	@NotNull
    private Integer warehouseId;

    @NotNull
    private Integer packetId;
    
    @NotNull
    private Integer productId;

	private String generatedBy;

   
    

}

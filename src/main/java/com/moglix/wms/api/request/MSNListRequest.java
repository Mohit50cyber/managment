package com.moglix.wms.api.request;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class MSNListRequest extends BaseRequest{

	 private static final long serialVersionUID = 5964191425367910291L;

	    @NotNull
	    private Integer warehouseId;

	    @NotNull
	    private Integer packetId;
	    
		@Override
		public String toString() {
			return "MSNListRequest [warehouseId=" + warehouseId + ", packetId=" + packetId + "]";
		}
   
	
}

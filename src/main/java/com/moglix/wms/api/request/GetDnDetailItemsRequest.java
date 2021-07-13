package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class GetDnDetailItemsRequest {

	 private static final long serialVersionUID = 2101109423573157701L;

	 	@NotNull
	    private Integer warehouseId;

	 	@NotNull
	    private Integer productId;

		public Integer getWarehouseId() {
			return warehouseId;
		}

		public void setWarehouseId(Integer warehouseId) {
			this.warehouseId = warehouseId;
		}

		public Integer getProductId() {
			return productId;
		}

		public void setProductId(Integer productId) {
			this.productId = productId;
		}

		
	    
	    
}

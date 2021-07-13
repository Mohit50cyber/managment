package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author Harshit on 11/5/21
 */
public class GetInventoryForPackedQtyRequest extends BaseRequest {
    private static final long serialVersionUID = 2101109423573157701L;

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer productId;
    
    private Integer zoneId;
    
    private Integer binId;
    
    public Integer getZoneId() {
		return zoneId;
	}

	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}

	public Integer getBinId() {
		return binId;
	}

	public void setBinId(Integer binId) {
		this.binId = binId;
	}

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

    @Override
	public String toString() {
		return "GetInventoryForAllocatedQtyRequest [warehouseId=" + warehouseId + ", productId=" + productId
				+ ", zoneId=" + zoneId + ", binId=" + binId + "]";
	}
}

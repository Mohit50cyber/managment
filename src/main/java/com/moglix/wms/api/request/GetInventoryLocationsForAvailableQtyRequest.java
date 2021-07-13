package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 15/5/19
 */
public class GetInventoryLocationsForAvailableQtyRequest extends BaseRequest {
    private static final long serialVersionUID = 2101109423573157701L;

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer productId;
    
    private Integer binId;
    
    private Integer zoneId;

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
    
    public Integer getBinId() {
		return binId;
	}

	public void setBinId(Integer binId) {
		this.binId = binId;
	}

	public Integer getZoneId() {
		return zoneId;
	}

	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}

	@Override
	public String toString() {
		return "GetInventoryLocationsForAvailableQtyRequest [warehouseId=" + warehouseId + ", productId=" + productId
				+ ", binId=" + binId + ", zoneId=" + zoneId + "]";
	}
}

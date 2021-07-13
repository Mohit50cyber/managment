package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 15/5/19
 */
public class GetInventoryLocationsForTotalQtyRequest extends BaseRequest {
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
		return "GetInventoryLocationsForTotalQtyRequest [warehouseId=" + warehouseId + ", productId=" + productId
				+ ", zoneId=" + zoneId + ", binId=" + binId + "]";
	}
}

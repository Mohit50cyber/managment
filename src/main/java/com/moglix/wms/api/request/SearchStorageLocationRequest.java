package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 1/5/19
 */
public class SearchStorageLocationRequest extends BaseRequest {
    private static final long serialVersionUID = -4587001756672107007L;

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer zoneId;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public String toString() {
        return "SearchStorageLocationRequest{" +
                "warehouseId=" + warehouseId +
                ", zoneId=" + zoneId +
                '}';
    }
}

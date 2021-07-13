package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 1/5/19
 */
public class SearchZoneRequest extends BaseRequest {
    private static final long serialVersionUID = -4587001756672107007L;

    @NotNull
    private Integer warehouseId;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public String toString() {
        return "SearchZoneRequest{" +
                "warehouseId=" + warehouseId +
                '}';
    }
}

package com.moglix.wms.api.request;

/**
 * @author pankaj on 17/5/19
 */
public class GetInventoryStatsRequest extends BaseRequest {
    private static final long serialVersionUID = -8965471205842824597L;

    private Integer warehouseId;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public String toString() {
        return "GetInventoryStatsRequest{" +
                "warehouseId=" + warehouseId +
                '}';
    }
}

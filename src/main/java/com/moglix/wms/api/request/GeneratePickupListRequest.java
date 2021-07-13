package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 14/5/19
 */
public class GeneratePickupListRequest extends BaseRequest {
    private static final long serialVersionUID = 5964191425367910291L;

    @NotNull
    private Integer warehouseId;

    @NotNull
    @Size(min=1)
    private List<Integer> packetIds = new ArrayList<>();

    private String generatedBy;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<Integer> getPacketIds() {
        return packetIds;
    }

    public void setPacketIds(List<Integer> packetIds) {
        this.packetIds = packetIds;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    @Override
    public String toString() {
        return "GeneratePickupListRequest{" +
                "warehouseId=" + warehouseId +
                ", packetIds=" + packetIds +
                ", generatedBy='" + generatedBy + '\'' +
                '}';
    }
}

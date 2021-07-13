package com.moglix.wms.dto;

import com.moglix.wms.api.response.CountWarehouseDataResponse;
import com.moglix.wms.entities.PickupList;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pankaj on 14/5/19
 */
@Data
public class PickupListAppDto {

    private Integer id;
    private String status;
    private int itemCount;
    private String generatedBy;
    private String assignedTo;
    private Integer warehouseId;
    private String warehouseName;
    private Date created;
    private Date modified;
    private List<PickupListItemAppDto> pickupListItems = new ArrayList<>();
    private List<Integer> packetIds = new ArrayList<>();

    public PickupListAppDto() {
    }

    public PickupListAppDto(PickupList obj) {
        this.id = obj.getId();
        this.warehouseId = obj.getWarehouse().getId();
        this.warehouseName = obj.getWarehouse().getName();
        this.status = obj.getStatus().name();
        this.itemCount = obj.getItemCount();
        this.generatedBy = obj.getGeneratedBy();
        this.assignedTo = obj.getAssignedTo();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
    }
    
}

package com.moglix.wms.dto;

import com.moglix.wms.entities.PickupList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author harshit on 14/5/19
 */
public class PickupListDto {

    private Integer id;
    private String status;
    private int itemCount;
    private String generatedBy;
    private String assignedTo;
    private Integer warehouseId;
    private String warehouseName;
    private Date created;
    private Date modified;
    private List<PickupListItemDto> pickupListItems = new ArrayList<>();
    private List<Integer> packetIds = new ArrayList<>();

    public PickupListDto() {
    }

    public PickupListDto(PickupList obj) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public List<PickupListItemDto> getPickupListItems() {
        return pickupListItems;
    }

    public void setPickupListItems(List<PickupListItemDto> pickupListItems) {
        this.pickupListItems = pickupListItems;
    }

    public List<Integer> getPacketIds() {
        return packetIds;
    }

    public void setPacketIds(List<Integer> packetIds) {
        this.packetIds = packetIds;
    }
}

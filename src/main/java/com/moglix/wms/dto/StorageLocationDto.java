package com.moglix.wms.dto;

import com.moglix.wms.entities.StorageLocation;

import java.util.Date;

/**
 * @author pankaj on 1/5/19
 */
public class StorageLocationDto {

    private Integer id;
    private String name;
    private String warehouseName;
    private String zoneName;
    private String rackName;
    private String binName;
    private Integer warehouseId;
    private Integer zoneId;
    private Integer rackId;
    private Integer binId;
    private Double height;
    private Double width;
    private Double depth;
    private boolean full;
    private Date created;
    private Date modified;

    public StorageLocationDto() {
    }

    public StorageLocationDto(StorageLocation obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.warehouseName = obj.getWarehouse().getName();
        this.zoneName = obj.getZone().getName();
        this.rackName = obj.getRack().getName();
        this.binName = obj.getBin().getName();
        this.warehouseId = obj.getWarehouse().getId();
        this.zoneId = obj.getZone().getId();
        this.rackId = obj.getRack().getId();
        this.binId = obj.getBin().getId();
        this.height = obj.getHeight();
        this.width = obj.getWidth();
        this.depth = obj.getDepth();
        this.full = obj.isFull();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getRackName() {
        return rackName;
    }

    public void setRackName(String rackName) {
        this.rackName = rackName;
    }

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }
    
    public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

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

    public Integer getRackId() {
        return rackId;
    }

    public void setRackId(Integer rackId) {
        this.rackId = rackId;
    }

    public Integer getBinId() {
        return binId;
    }

    public void setBinId(Integer binId) {
        this.binId = binId;
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
}

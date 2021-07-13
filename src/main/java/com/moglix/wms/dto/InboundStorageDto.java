package com.moglix.wms.dto;

import com.moglix.wms.entities.InboundStorage;
import java.util.Date;

/**
 * @author pankaj on 3/5/19
 */
public class InboundStorageDto {

    private Integer id;
    private Double quantity;
    private boolean confirmed;
    private Integer inboundId;
    private String storageLocationName;
    private Integer storageLocationId;
    private String zoneName;
    private Integer zoneId;
    private String binName;
    private Integer binId;
    private Date created;
    private Date modified;

    public InboundStorageDto() {
    }

    public InboundStorageDto(InboundStorage obj) {
        this.id = obj.getId();
        this.quantity = obj.getQuantity();
        this.inboundId = obj.getInbound().getId();
        this.confirmed = obj.isConfirmed();
        this.zoneName = obj.getStorageLocation().getZone().getName();
        this.zoneId = obj.getStorageLocation().getZone().getId();
        this.binName = obj.getStorageLocation().getBin().getName();
        this.binId = obj.getStorageLocation().getBin().getId();
        this.storageLocationName = obj.getStorageLocation().getName();
        this.storageLocationId = obj.getStorageLocation().getId();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getInboundId() {
        return inboundId;
    }

    public void setInboundId(Integer inboundId) {
        this.inboundId = inboundId;
    }

    public String getStorageLocationName() {
        return storageLocationName;
    }

    public void setStorageLocationName(String storageLocationName) {
        this.storageLocationName = storageLocationName;
    }

    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        this.storageLocationId = storageLocationId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
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

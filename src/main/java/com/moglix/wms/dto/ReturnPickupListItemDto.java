package com.moglix.wms.dto;

import com.moglix.wms.entities.ReturnPickupListItem;

import java.util.Date;

/**
 * @author pankaj on 20/5/19
 */
public class ReturnPickupListItemDto {

    private Integer id;
    private Integer returnPickupListId;
    private Integer productId;
    private String productMsn;
    private String productName;
    private String uom;
    private Integer storageLocationId;
    private String storageLocationName;
    private Integer zoneId;
    private String zoneName;
    private Integer rackId;
    private String rackName;
    private Double quantity;
    private Date created;
    private Date modified;

    public ReturnPickupListItemDto() {
    }

    public ReturnPickupListItemDto(ReturnPickupListItem obj) {
        this.id = obj.getId();
        this.returnPickupListId = obj.getReturnPickupList().getId();
        this.productId = obj.getProduct().getId();
        this.productMsn = obj.getProduct().getProductMsn();
        this.productName = obj.getProduct().getProductName();
        this.uom = obj.getProduct().getUom();
        this.storageLocationId = obj.getStorageLocation().getId();
        this.storageLocationName = obj.getStorageLocation().getName();
        this.zoneId = obj.getStorageLocation().getZone().getId();
        this.zoneName = obj.getStorageLocation().getZone().getName();
        this.rackId = obj.getStorageLocation().getRack().getId();
        this.rackName = obj.getStorageLocation().getRack().getName();
        this.quantity = obj.getQuantity();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReturnPickupListId() {
        return returnPickupListId;
    }

    public void setReturnPickupListId(Integer returnPickupListId) {
        this.returnPickupListId = returnPickupListId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductMsn() {
        return productMsn;
    }

    public void setProductMsn(String productMsn) {
        this.productMsn = productMsn;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        this.storageLocationId = storageLocationId;
    }

    public String getStorageLocationName() {
        return storageLocationName;
    }

    public void setStorageLocationName(String storageLocationName) {
        this.storageLocationName = storageLocationName;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Integer getRackId() {
        return rackId;
    }

    public void setRackId(Integer rackId) {
        this.rackId = rackId;
    }

    public String getRackName() {
        return rackName;
    }

    public void setRackName(String rackName) {
        this.rackName = rackName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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

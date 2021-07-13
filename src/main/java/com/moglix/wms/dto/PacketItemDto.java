package com.moglix.wms.dto;

import com.moglix.wms.entities.PacketItem;

import java.util.Date;

/**
 * @author pankaj on 15/5/19
 */
public class PacketItemDto {

    private Integer id;
    private Integer packetId;
    private Integer saleOrderId;
    private Integer emsOrderId;
    private Integer emsOrderItemId;
    private Integer productId;
    private String productMsn;
    private String productName;
    private String uom;
    private Integer zoneId;
    private String zoneName;
    private Integer storageLocationId;
    private String storageLocationName;
    private Double quantity;
    private Date created;
    private Date modified;

    public PacketItemDto() {
    }

    public PacketItemDto(PacketItem obj) {
        this.id = obj.getId();
        this.packetId = obj.getPacket().getId();
        this.saleOrderId = obj.getSaleOrder().getId();
        this.emsOrderId = obj.getSaleOrder().getEmsOrderId();
        this.emsOrderItemId = obj.getSaleOrder().getEmsOrderItemId();
        this.productId = obj.getSaleOrder().getProduct().getId();
        this.productMsn = obj.getSaleOrder().getProduct().getProductMsn();
        this.productName = obj.getSaleOrder().getProduct().getProductName();
        this.uom = obj.getSaleOrder().getProduct().getUom();
        this.zoneId = obj.getInboundStorage().getStorageLocation().getZone().getId();
        this.zoneName = obj.getInboundStorage().getStorageLocation().getZone().getName();
        this.storageLocationId = obj.getInboundStorage().getStorageLocation().getId();
        this.storageLocationName = obj.getInboundStorage().getStorageLocation().getName();
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

    public Integer getPacketId() {
        return packetId;
    }

    public void setPacketId(Integer packetId) {
        this.packetId = packetId;
    }

    public Integer getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(Integer saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public Integer getEmsOrderId() {
        return emsOrderId;
    }

    public void setEmsOrderId(Integer emsOrderId) {
        this.emsOrderId = emsOrderId;
    }

    public Integer getEmsOrderItemId() {
        return emsOrderItemId;
    }

    public void setEmsOrderItemId(Integer emsOrderItemId) {
        this.emsOrderItemId = emsOrderItemId;
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
}

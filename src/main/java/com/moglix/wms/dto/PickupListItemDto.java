package com.moglix.wms.dto;

import com.moglix.wms.entities.PickupListItem;

import java.util.Date;

/**
 * @author pankaj on 14/5/19
 */
public class PickupListItemDto {

    //private Integer id;
    private Integer pickupListId;
    private Integer packetId;
//    private Integer packetItemId;
    private Double quantity;
    private Integer storageLocationId;
    private String storageLocationName;
    private String lotNumber;
    private Integer zoneId;
    private String zoneName;
    private Integer rackId;
    private Integer binId;
    private Integer productId;
    private String productName;
    private String productMsn;
    private String uom;
    private Date created;
    private Date modified;
    private String invoiceNumber;

    public PickupListItemDto() {
    }

    public PickupListItemDto(PickupListItem obj) {
        //this.id = obj.getId();
        this.pickupListId = obj.getPickupList().getId();
        this.packetId = obj.getPacketItem().getPacket().getId();
        //this.packetItemId = obj.getPacketItem().getId();
        this.quantity = obj.getPacketItem().getQuantity();
        this.storageLocationId = obj.getPacketItem().getInboundStorage().getStorageLocation().getId();
        this.storageLocationName = obj.getPacketItem().getInboundStorage().getStorageLocation().getName();
        this.lotNumber = obj.getPacketItem().getInboundStorage().getLotNumber();
        this.zoneId = obj.getPacketItem().getInboundStorage().getStorageLocation().getZone().getId();
        this.zoneName = obj.getPacketItem().getInboundStorage().getStorageLocation().getZone().getName();
        this.rackId = obj.getPacketItem().getInboundStorage().getStorageLocation().getRack().getId();
        this.binId = obj.getPacketItem().getInboundStorage().getStorageLocation().getBin().getId();
        this.productId = obj.getPacketItem().getInboundStorage().getProduct().getId();
        this.productName = obj.getPacketItem().getInboundStorage().getProduct().getProductName();
        this.productMsn = obj.getPacketItem().getInboundStorage().getProduct().getProductMsn();
        this.uom = obj.getPacketItem().getInboundStorage().getProduct().getUom();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
        this.invoiceNumber = obj.getPacketItem().getPacket().getInvoiceNumber();
    }

    /*public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }*/

    public Integer getPickupListId() {
        return pickupListId;
    }

    public void setPickupListId(Integer pickupListId) {
        this.pickupListId = pickupListId;
    }

    /*public Integer getPacketId() {
        return packetId;
    }

    public void setPacketId(Integer packetId) {
        this.packetId = packetId;
    }

    public Integer getPacketItemId() {
        return packetItemId;
    }

    public void setPacketItemId(Integer packetItemId) {
        this.packetItemId = packetItemId;
    }*/

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductMsn() {
        return productMsn;
    }

    public void setProductMsn(String productMsn) {
        this.productMsn = productMsn;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
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

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public Integer getPacketId() {
		return packetId;
	}

	public void setPacketId(Integer packetId) {
		this.packetId = packetId;
	}
	
	
	
}

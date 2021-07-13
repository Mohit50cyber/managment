package com.moglix.wms.dto;

import com.moglix.wms.api.response.CountWarehouseDataResponse;
import com.moglix.wms.entities.PickupListItem;

import lombok.Data;

import java.util.Date;

/**
 * @author harshit on 14/5/19
 */
@Data
public class PickupListItemAppDto {

	private Integer packetItemId;
    private Integer pickupListId;
    private Integer packetId;
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
    private Integer serialNumber;
    private String status;

	public PickupListItemAppDto() {
    }

    public PickupListItemAppDto(PickupListItem obj) {
    	this.packetItemId=obj.getPacketItem().getId();
        this.pickupListId = obj.getPickupList().getId();
        this.packetId = obj.getPacketItem().getPacket().getId();
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
        this.status=obj.getPacketItem().getStatus().toString();
    }
}

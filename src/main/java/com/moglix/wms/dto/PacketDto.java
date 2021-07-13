package com.moglix.wms.dto;

import com.moglix.wms.entities.Packet;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pankaj on 14/5/19
 */
@Data
public class PacketDto {

    private Integer id;
    private Integer emsPacketId;
    private String invoiceNumber;
    private Boolean cancelled;
    private String status;
    private Date created;
    private Date modified;

    private int msnCount;
    private Double totalQuantity;
    
    private Boolean isLotEnabled;
    private String pickedBy;
    private Boolean isPickBySame;
    private String userName;

    private List<PacketItemDto> packetItems = new ArrayList<>();

    public PacketDto() {
    }

	public PacketDto(Packet obj) {
		this.id = obj.getId();
		this.emsPacketId = obj.getEmsPacketId();
		this.invoiceNumber = obj.getInvoiceNumber();
		this.cancelled = obj.getCancelled();
		this.status = obj.getStatus().name();
		this.created = obj.getCreated();
		this.modified = obj.getModified();
		this.msnCount = obj.getMsnCount();
		this.totalQuantity = obj.getTotalQuantity();
		this.isLotEnabled = obj.getIsLotEnabled();
		this.pickedBy=obj.getPickedby();
	}
	
	public PacketDto(Packet obj,Boolean isPickedby) {
		this.id = obj.getId();
		this.emsPacketId = obj.getEmsPacketId();
		this.invoiceNumber = obj.getInvoiceNumber();
		this.cancelled = obj.getCancelled();
		this.status = obj.getStatus().name();
		this.created = obj.getCreated();
		this.modified = obj.getModified();
		this.msnCount = obj.getMsnCount();
		this.totalQuantity = obj.getTotalQuantity();
		this.isLotEnabled = obj.getIsLotEnabled();
		this.pickedBy=obj.getPickedby();
		this.isPickBySame=isPickedby;
	}
	
	public PacketDto(Packet obj,Boolean isPickedby,String userName) {
		this.id = obj.getId();
		this.emsPacketId = obj.getEmsPacketId();
		this.invoiceNumber = obj.getInvoiceNumber();
		this.cancelled = obj.getCancelled();
		this.status = obj.getStatus().name();
		this.created = obj.getCreated();
		this.modified = obj.getModified();
		this.msnCount = obj.getMsnCount();
		this.totalQuantity = obj.getTotalQuantity();
		this.isLotEnabled = obj.getIsLotEnabled();
		this.pickedBy=obj.getPickedby();
		this.isPickBySame=isPickedby;
		this.userName=userName;
	}	
}

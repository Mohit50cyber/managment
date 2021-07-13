package com.moglix.wms.dto;

import com.moglix.wms.constants.ReturnPickupListStatus;
import com.moglix.wms.entities.ReturnPickupList;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pankaj on 20/5/19
 */
public class ReturnPickupListDto {
    private Integer id;
    private String creditNoteNumber;
    private Integer supplierId;
    private ReturnPickupListStatus status;
    private String supplierName;
    private Integer warehouseId;
    private String warehouseName;
    private Integer supplierPoId;
    private Integer emsPacketId;
    private Integer packetId;
    private String invoiceNumber;
    private Date created;
    private Date modified;

    private Set<ReturnPickupListItemDto> returnPickupListItems = new HashSet<>();

    public ReturnPickupListDto() {
    }

    public ReturnPickupListDto(ReturnPickupList obj) {
        this.id = obj.getId();
        this.creditNoteNumber = obj.getCreditNoteNumber();
        this.supplierId = obj.getSupplierId();
        this.supplierName = obj.getSupplierName();
        this.warehouseId = obj.getWarehouse().getId();
        this.warehouseName = obj.getWarehouse().getName();
        if(obj.getReturnPacket() != null) {
        	 this.packetId = obj.getReturnPacket().getPacket().getId();
             this.emsPacketId = obj.getReturnPacket().getPacket().getEmsPacketId();
             this.invoiceNumber = obj.getReturnPacket().getPacket().getInvoiceNumber();
        } 
        this.created = obj.getCreated();
        this.modified = obj.getModified();
        this.supplierPoId = obj.getSupplierPoId();
        this.status = obj.getStatus();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public ReturnPickupListStatus getStatus() {
		return status;
	}

	public void setStatus(ReturnPickupListStatus status) {
		this.status = status;
	}

	public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Integer getEmsPacketId() {
        return emsPacketId;
    }

    public void setEmsPacketId(Integer emsPacketId) {
        this.emsPacketId = emsPacketId;
    }

    public Integer getPacketId() {
        return packetId;
    }

    public void setPacketId(Integer packetId) {
        this.packetId = packetId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCreditNoteNumber() {
		return creditNoteNumber;
	}

	public void setCreditNoteNumber(String creditNoteNumber) {
		this.creditNoteNumber = creditNoteNumber;
	}

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}

	public Set<ReturnPickupListItemDto> getReturnPickupListItems() {
        return returnPickupListItems;
    }

    public void setReturnPickupListItems(Set<ReturnPickupListItemDto> returnPickupListItems) {
        this.returnPickupListItems = returnPickupListItems;
    }
}

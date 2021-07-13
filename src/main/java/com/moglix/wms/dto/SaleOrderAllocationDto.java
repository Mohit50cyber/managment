package com.moglix.wms.dto;

import com.moglix.wms.entities.SaleOrderAllocation;

import java.util.Date;

/**
 * @author pankaj on 6/5/19
 */
public class SaleOrderAllocationDto {

    private Integer id;
    private Integer saleOrderId;
    private Integer inboundStorageId;
    private String inboundStorageName;
    private Double allocatedQuantity;
    private Double availableQuantity;
    private Double packedQuantity;
    private String status;
    private Date created;
    private Date modified;

    public SaleOrderAllocationDto() {
    }

    public SaleOrderAllocationDto(SaleOrderAllocation obj) {
        this.id = obj.getId();
        this.saleOrderId = obj.getSaleOrder().getId();
        this.inboundStorageId = obj.getInboundStorage().getId();
        this.inboundStorageName = obj.getInboundStorage().getStorageLocation().getName();
        this.allocatedQuantity = obj.getAllocatedQuantity();
        this.availableQuantity = obj.getAvailableQuantity();
        this.packedQuantity = obj.getPackedQuantity();
        this.status = obj.getStatus().name();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(Integer saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public Integer getInboundStorageId() {
        return inboundStorageId;
    }

    public void setInboundStorageId(Integer inboundStorageId) {
        this.inboundStorageId = inboundStorageId;
    }

    public String getInboundStorageName() {
        return inboundStorageName;
    }

    public void setInboundStorageName(String inboundStorageName) {
        this.inboundStorageName = inboundStorageName;
    }

    public Double getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(Double allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
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

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Double getPackedQuantity() {
        return packedQuantity;
    }

    public void setPackedQuantity(Double packedQuantity) {
        this.packedQuantity = packedQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

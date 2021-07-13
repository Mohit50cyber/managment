package com.moglix.wms.dto;

import com.moglix.wms.entities.SaleOrder;

import java.util.Date;

/**
 * @author pankaj on 6/5/19
 */
public class SaleOrderDto {
    private Integer id;
    private Integer emsOrderId;
    private Integer emsOrderItemId;
    private Integer productId;
    private String productMsn;
    private String productName;
    private Integer warehouseId;
    private String warehouseName;
    private Double orderedQuantity;
    private Double allocatedQuantity;
    private Double packedQuantity;
    private String remark;
    private String status;
    private Date created;
    private Date modified;

    public SaleOrderDto() {
    }

    public SaleOrderDto(SaleOrder obj) {
        this.id = obj.getId();
        this.emsOrderId = obj.getEmsOrderId();
        this.emsOrderItemId = obj.getEmsOrderItemId();
        this.productId = obj.getProduct().getId();
        this.productName = obj.getProduct().getProductName();
        this.productMsn = obj.getProduct().getProductMsn();
        this.warehouseId = obj.getWarehouse().getId();
        this.warehouseName = obj.getWarehouse().getName();
        this.orderedQuantity = obj.getOrderedQuantity();
        this.allocatedQuantity = obj.getAllocatedQuantity();
        this.packedQuantity = obj.getPackedQuantity();
        this.remark = obj.getRemark();
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

    public Double getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Double orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(Double allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
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

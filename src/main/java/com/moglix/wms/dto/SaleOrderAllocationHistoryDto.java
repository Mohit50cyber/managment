package com.moglix.wms.dto;

import com.moglix.wms.entities.SaleOrderAllocationHistory;

import java.util.Date;

/**
 * @author pankaj on 8/5/19
 */
public class SaleOrderAllocationHistoryDto {

    private Integer id;
    private Integer saleOrderId;
    private Double quantity;
    private String action;
    private Date created;

    public SaleOrderAllocationHistoryDto() {

    }

    public SaleOrderAllocationHistoryDto(SaleOrderAllocationHistory obj) {
        this.id = obj.getId();
        this.saleOrderId = obj.getSaleOrder().getId();
        this.quantity = obj.getQuantity();
        this.action = obj.getAction();
        this.created = obj.getCreated();
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}

package com.moglix.wms.queueModel;

import java.io.Serializable;

/**
 * @author pankaj on 9/5/19
 */
public class InventoryAllocationRequest implements Serializable {

    private static final long serialVersionUID = -6841130013515319219L;
    private Integer saleOrderId;

    private Integer productId;

    public InventoryAllocationRequest() {
    }

    public InventoryAllocationRequest(Integer saleOrderId, Integer productId) {
        this.saleOrderId = saleOrderId;
        this.productId = productId;
    }

    public Integer getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(Integer saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "InventoryAllocationRequest{" +
                "saleOrderId=" + saleOrderId +
                ", productId=" + productId +
                '}';
    }
}

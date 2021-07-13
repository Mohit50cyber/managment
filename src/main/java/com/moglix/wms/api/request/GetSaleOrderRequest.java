package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 13/5/19
 */
public class GetSaleOrderRequest extends BaseRequest {
    private static final long serialVersionUID = -5778714058380702331L;

    @NotNull
    private Integer emsOrderItemId;

    public GetSaleOrderRequest(@NotNull Integer emsOrderItemId) {
        this.emsOrderItemId = emsOrderItemId;
    }

    public Integer getEmsOrderItemId() {
        return emsOrderItemId;
    }

    public void setEmsOrderItemId(Integer emsOrderItemId) {
        this.emsOrderItemId = emsOrderItemId;
    }

    @Override
    public String toString() {
        return "GetSaleOrderRequest{" +
                "emsOrderItemId=" + emsOrderItemId +
                '}';
    }
}

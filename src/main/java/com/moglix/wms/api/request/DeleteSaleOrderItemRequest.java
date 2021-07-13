package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 6/5/19
 */
public class DeleteSaleOrderItemRequest extends BaseRequest {
    private static final long serialVersionUID = 6182141782510356279L;

    @NotNull
    @Size(min=1)
    private List<Integer> emsOrderItemIds = new ArrayList<>();

    public List<Integer> getEmsOrderItemIds() {
        return emsOrderItemIds;
    }

    public void setEmsOrderItemIds(List<Integer> emsOrderItemIds) {
        this.emsOrderItemIds = emsOrderItemIds;
    }

    @Override
    public String toString() {
        return "DeleteSaleOrderItemRequest{" +
                "emsOrderItemIds=" + emsOrderItemIds +
                '}';
    }
}

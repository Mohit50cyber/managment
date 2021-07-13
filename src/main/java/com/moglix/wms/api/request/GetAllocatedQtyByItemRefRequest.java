package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 15/5/19
 */
public class GetAllocatedQtyByItemRefRequest extends BaseRequest {
    private static final long serialVersionUID = -1142720800095754270L;

    @NotNull
    @Size(min=1)
    private List<String> itemRefs = new ArrayList<>();

    public GetAllocatedQtyByItemRefRequest() {
    }

    public List<String> getItemRefs() {
        return itemRefs;
    }

    public void setItemRefs(List<String> itemRefs) {
        this.itemRefs = itemRefs;
    }

    @Override
    public String toString() {
        return "GetAllocatedQtyByItemRefRequest{" +
                "itemRefs=" + itemRefs +
                '}';
    }
}

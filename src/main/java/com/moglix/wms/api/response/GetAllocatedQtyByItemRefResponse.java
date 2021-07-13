package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 15/5/19
 */
public class GetAllocatedQtyByItemRefResponse extends BaseResponse {
    private static final long serialVersionUID = 7435433936257171444L;

    private List<AllocatedQty> allocatedQtyList = new ArrayList<>();

    public List<AllocatedQty> getAllocatedQtyList() {
        return allocatedQtyList;
    }

    public void setAllocatedQtyList(List<AllocatedQty> allocatedQtyList) {
        this.allocatedQtyList = allocatedQtyList;
    }

    public static class AllocatedQty {
        private String itemRef;
        private Double allocatedQuantity;
        private Double packedQuantity;

        public AllocatedQty() {
        }

        public AllocatedQty(String itemRef, Double allocatedQuantity, Double packedQuantity) {
            this.itemRef = itemRef;
            this.allocatedQuantity = allocatedQuantity;
            this.packedQuantity = packedQuantity;
        }

        public String getItemRef() {
            return itemRef;
        }

        public void setItemRef(String itemRef) {
            this.itemRef = itemRef;
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
    }
}

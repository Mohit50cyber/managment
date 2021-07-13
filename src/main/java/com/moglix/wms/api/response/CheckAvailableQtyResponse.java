package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 8/5/19
 */
public class CheckAvailableQtyResponse extends BaseResponse {
    private static final long serialVersionUID = 1261745003948928952L;

    private List<AvailableQty> availableQtyList = new ArrayList<>();

    public List<AvailableQty> getAvailableQtyList() {
        return availableQtyList;
    }

    public void setAvailableQtyList(List<AvailableQty> availableQtyList) {
        this.availableQtyList = availableQtyList;
    }

    public static class AvailableQty {
        private Integer warehouseId;
        private String productMsn;
        private Double quantity;

        public AvailableQty() {
        }

        public AvailableQty(Integer warehouseId, String productMsn, Double quantity) {
            this.warehouseId = warehouseId;
            this.productMsn = productMsn;
            this.quantity = quantity;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public Integer getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(Integer warehouseId) {
            this.warehouseId = warehouseId;
        }

        public String getProductMsn() {
            return productMsn;
        }

        public void setProductMsn(String productMsn) {
            this.productMsn = productMsn;
        }
    }
}

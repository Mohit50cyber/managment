package com.moglix.wms.api.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 8/5/19
 */
public class CheckAvailableQtyRequest extends BaseRequest {
    private static final long serialVersionUID = 367067340177210328L;

    @NotNull
    @Size(min=1)
    @Valid
    private List<MsnList> msnWarehouseList = new ArrayList<>();

    public List<MsnList> getMsnWarehouseList() {
        return msnWarehouseList;
    }

    public void setMsnWarehouseList(List<MsnList> msnWarehouseList) {
        this.msnWarehouseList = msnWarehouseList;
    }

    @Override
    public String toString() {
        return "CheckAvailableQtyRequest{" +
                "msnWarehouseList=" + msnWarehouseList +
                '}';
    }

    public static class MsnList {

        @NotBlank
        private String productMsn;

        @NotNull
        private Integer warehouseId;

        public String getProductMsn() {
            return productMsn;
        }

        public void setProductMsn(String productMsn) {
            this.productMsn = productMsn;
        }

        public Integer getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(Integer warehouseId) {
            this.warehouseId = warehouseId;
        }

        @Override
        public String toString() {
            return "MsnList{" +
                    "productMsn='" + productMsn + '\'' +
                    ", warehouseId=" + warehouseId +
                    '}';
        }
    }
}

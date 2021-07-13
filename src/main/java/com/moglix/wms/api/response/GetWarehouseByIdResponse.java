package com.moglix.wms.api.response;

import com.moglix.wms.dto.WarehouseDto;

/**
 * @author pankaj on 30/4/19
 */
public class GetWarehouseByIdResponse extends BaseResponse {
    private static final long serialVersionUID = 1506721425727218756L;

    private WarehouseDto warehouse;

    public WarehouseDto getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseDto warehouse) {
        this.warehouse = warehouse;
    }
}

package com.moglix.wms.api.response;

import com.moglix.wms.dto.WarehouseDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 30/4/19
 */
public class SearchWarehouseResponse extends BaseResponse {
    private static final long serialVersionUID = 1506721425727218756L;


    private List<WarehouseDto> warehouses = new ArrayList<>();

    public List<WarehouseDto> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<WarehouseDto> warehouses) {
        this.warehouses = warehouses;
    }
}

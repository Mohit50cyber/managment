package com.moglix.wms.api.response;

import com.moglix.wms.dto.InventoryLocationDto;

/**
 * @author Harshit on 11/5/21
 */
public class GetInventoryForPackedQtyResponse extends BaseResponse {
    private static final long serialVersionUID = -7402829679513210899L;

    private InventoryLocationDto inventoryLocation;

    public InventoryLocationDto getInventoryLocation() {
        return inventoryLocation;
    }

    public void setInventoryLocation(InventoryLocationDto inventoryLocation) {
        this.inventoryLocation = inventoryLocation;
    }
}

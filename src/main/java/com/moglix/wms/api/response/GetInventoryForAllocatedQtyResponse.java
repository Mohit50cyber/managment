package com.moglix.wms.api.response;

import com.moglix.wms.dto.InventoryLocationDto;

/**
 * @author pankaj on 15/5/19
 */
public class GetInventoryForAllocatedQtyResponse extends BaseResponse {
    private static final long serialVersionUID = -7402829679513210899L;

    private InventoryLocationDto inventoryLocation;

    public InventoryLocationDto getInventoryLocation() {
        return inventoryLocation;
    }

    public void setInventoryLocation(InventoryLocationDto inventoryLocation) {
        this.inventoryLocation = inventoryLocation;
    }
}

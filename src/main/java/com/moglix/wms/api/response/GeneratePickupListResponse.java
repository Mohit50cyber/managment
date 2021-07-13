package com.moglix.wms.api.response;

import com.moglix.wms.dto.PickupListDto;

/**
 * @author pankaj on 14/5/19
 */
public class GeneratePickupListResponse extends BaseResponse {
    private static final long serialVersionUID = -8768160967073005162L;

    private PickupListDto pickupList;

    public PickupListDto getPickupList() {
        return pickupList;
    }

    public void setPickupList(PickupListDto pickupList) {
        this.pickupList = pickupList;
    }
}

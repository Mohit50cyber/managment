package com.moglix.wms.api.response;

import com.moglix.wms.dto.PickupListAppDto;

import lombok.Data;

/**
 * @author Harshit on 30/3/21
 */
@Data
public class GeneratePickupListAppResponse extends BaseResponse {
    private static final long serialVersionUID = -8768160967073005162L;

    private PickupListAppDto pickupList;
}

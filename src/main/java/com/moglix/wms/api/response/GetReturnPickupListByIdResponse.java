package com.moglix.wms.api.response;

import com.moglix.wms.dto.ReturnPickupListDto;

/**
 * @author pankaj on 20/5/19
 */
public class GetReturnPickupListByIdResponse extends BaseResponse {
    private static final long serialVersionUID = -1140828312378252433L;

    private ReturnPickupListDto returnPickupList;

    public ReturnPickupListDto getReturnPickupList() {
        return returnPickupList;
    }

    public void setReturnPickupList(ReturnPickupListDto returnPickupList) {
        this.returnPickupList = returnPickupList;
    }
}

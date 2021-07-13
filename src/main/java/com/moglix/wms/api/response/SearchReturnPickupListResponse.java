package com.moglix.wms.api.response;

import com.moglix.wms.dto.ReturnPickupListDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 20/5/19
 */
public class SearchReturnPickupListResponse extends PaginationResponse {
    private static final long serialVersionUID = -1140828312378252433L;

    private List<ReturnPickupListDto> returnPickupList = new ArrayList<>();

    public SearchReturnPickupListResponse(String message, boolean status, int code) {
        super(message, status, code);
    }

    public List<ReturnPickupListDto> getReturnPickupList() {
        return returnPickupList;
    }

    public void setReturnPickupList(List<ReturnPickupListDto> returnPickupList) {
        this.returnPickupList = returnPickupList;
    }
}

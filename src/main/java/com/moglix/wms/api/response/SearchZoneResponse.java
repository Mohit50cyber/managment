package com.moglix.wms.api.response;

import com.moglix.wms.dto.ZoneDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 1/5/19
 */
public class SearchZoneResponse extends BaseResponse {
    private static final long serialVersionUID = 1895064853536584165L;

    private List<ZoneDto> zones = new ArrayList<>();

    public List<ZoneDto> getZones() {
        return zones;
    }

    public void setZones(List<ZoneDto> zones) {
        this.zones = zones;
    }
}

package com.moglix.wms.api.response;

import com.moglix.wms.dto.StorageLocationDto;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 1/5/19
 */
public class SearchStorageLocationResponse extends PaginationResponse {

    private static final long serialVersionUID = 1895064853536584165L;

    public SearchStorageLocationResponse(String message, boolean status, int code) {
        super(message, status, code);
    }

    private List<StorageLocationDto> storageLocations = new ArrayList<>();

    public List<StorageLocationDto> getStorageLocations() {
        return storageLocations;
    }

    public void setStorageLocations(List<StorageLocationDto> storageLocations) {
        this.storageLocations = storageLocations;
    }
}

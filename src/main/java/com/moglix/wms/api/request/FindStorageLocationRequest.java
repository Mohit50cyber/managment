package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FindStorageLocationRequest {

	@NotNull
    private Integer warehouseId;

    @NotNull
    private String zoneBin;

}

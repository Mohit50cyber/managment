package com.moglix.wms.api.request;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
public class StockTransferNoteSearchRequest {
    Integer warehouseFrom;
    Integer warehouseTo;
    @Min(1)
    int pageSize=10;
    @Min(0)
    int pageNumber=0;
    String stnId;
}

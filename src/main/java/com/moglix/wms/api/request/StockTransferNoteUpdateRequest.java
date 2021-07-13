package com.moglix.wms.api.request;


import com.moglix.wms.constants.StockTransferNoteState;
import com.moglix.wms.dto.ProductHsnInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class StockTransferNoteUpdateRequest {
    @NotNull
    private StockTransferNoteState stockTransferNoteState;
    private List<ProductHsnInfo> productHsnInfoList;
}

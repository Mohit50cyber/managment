package com.moglix.wms.dto;

import com.moglix.wms.constants.StockTransferNoteState;
import com.moglix.wms.constants.StockTransferNoteStatus;
import lombok.*;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public @Data class UpdateSTNResponse {
	private String message;
	private boolean success;
	private String invoiceOrChallanNumber;
	private String invoiceOrChallanUrl;
    private StockTransferNoteStatus status;
    private StockTransferNoteState nextStockTransferNoteState;
}
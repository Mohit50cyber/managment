package com.moglix.wms.api.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.moglix.wms.constants.StockTransferNoteState;
import com.moglix.wms.constants.StockTransferType;
import com.moglix.wms.constants.StockTransferNoteStatus;
import com.moglix.wms.constants.StockTransferNoteType;
import com.moglix.wms.dto.StockTransferItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockTransferNoteResponse {
        @NonNull
        private String StockTransferNoteId;
        private String message;
        private String invoiceOrChallanNumber;
        private String invoiceOrChallanUrl;
        private Double quantity;
        private String hsnCode;
        private Double freightCharge;
        private Double miscCharges;
        private String itemRef;
        private Date  pickupDate;
        private String remarks;
        private Integer warehouseSource;
        private Integer warehouseTarget;
        private StockTransferType stockTransferType;
        private StockTransferNoteStatus stockTransferNoteStatus;
        private StockTransferNoteType stockTransferNoteType;
        private List<StockTransferItem> stockTransferItem;
        private Integer totalPages;
        private StockTransferNoteState nextStockTransferNoteState;


        public static StockTransferNoteResponseBuilder builder(String id) {
                return hiddenBuilder().StockTransferNoteId(id);
        }
}

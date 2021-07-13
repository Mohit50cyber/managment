package com.moglix.wms.api.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.moglix.wms.constants.StockTransferNoteType;
import com.moglix.wms.dto.StockWarehouseTransfer;
import com.moglix.wms.validator.CheckValidWarehouse;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateStockTransferNoteRequest {
    @NotNull
    @CheckValidWarehouse
    int warehouseIdFrom;

    @NotNull
    @CheckValidWarehouse
    int warehouseIdTo;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date pickUpDate;

    String itemRef;

    private Double freightCharge = 0d;
    private Double miscCharges = 0d;
    private String remarks;

    private List<StockWarehouseTransfer> warehouseStock;


    @NotNull
    @Enumerated(EnumType.STRING)
    StockTransferNoteType stockTransferNoteType;
}

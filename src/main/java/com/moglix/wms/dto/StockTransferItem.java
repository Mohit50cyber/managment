package com.moglix.wms.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class StockTransferItem {
    int supplierId;
    int supplierPoId;
    int supplierPoItemId;
    private String productMsn;
    private int inboundStorageId;
    private String productName;
    private Double purchasePrice;
    private double taxPercentage;
    private Double quantity;
    private String zoneName;
    private String binName;
    private long stockTransferItemId;
    private String hsnCode;
    private boolean hsnRequired;
}


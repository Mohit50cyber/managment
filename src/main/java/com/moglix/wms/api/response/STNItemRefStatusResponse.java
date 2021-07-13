package com.moglix.wms.api.response;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class STNItemRefStatusResponse {
    @NonNull
    private String itemRef;
    
    @NonNull
    private boolean stnEligibility;
    private String hsnCode;
    private String reason;
    private String productMsn;
    private Double quantity;
}

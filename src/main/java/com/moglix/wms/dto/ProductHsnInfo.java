package com.moglix.wms.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductHsnInfo {
    private String productMsn;
    private String hsnCode;
}

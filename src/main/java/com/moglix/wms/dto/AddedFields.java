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
public class AddedFields{
	private String brandName;
	private Object pOItemId;
	private String productUOQ;
	private String grNo;
	private String transport;
	private String vehicleNo;
	private String station;
	private String vendorCode;
	private String customerPoc;
}
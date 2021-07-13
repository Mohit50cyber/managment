package com.moglix.wms.api.response;

import java.util.List;


import lombok.Data;

@Data
public class STNWarehouseEligibleResponse extends BaseResponse {

	Double eligibleItemsQuantity;
	String hsnCode;
}

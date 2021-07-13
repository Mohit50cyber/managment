package com.moglix.wms.service;

import com.moglix.wms.api.request.SaleOrderSupplierPurchaseOrderMappingRequest;
import com.moglix.wms.api.response.BaseResponse;

/**
 * @author sparsh saxena on 14/3/21
 */
public interface ISaleOrderSupplierPurchaseOrderMappingService {
	
	BaseResponse saveMapping(SaleOrderSupplierPurchaseOrderMappingRequest request);

}

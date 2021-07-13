package com.moglix.wms.service;

import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;

import java.util.List;

public interface ISaleOrderAllocationService {
    List<SaleOrderAllocation> getSaleOrderAllocationBySaleOrderIdAndStatus(Integer saleOrderId, SaleOrderAllocationStatus status);
    List<SaleOrderAllocation> getSaleOrderAllocationByInboundStorageAndSaleOrder(InboundStorage is, SaleOrder so);

    void updateStatus(int saleOrderId,SaleOrderAllocationStatus status);
}

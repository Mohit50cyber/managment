package com.moglix.wms.service.impl;

import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.repository.SaleOrderAllocationRepository;
import com.moglix.wms.service.ISaleOrderAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleOrderAllocationService implements ISaleOrderAllocationService {
    @Autowired
    private SaleOrderAllocationRepository saleOrderAllocationRepository;

    @Override
    public List<SaleOrderAllocation> getSaleOrderAllocationBySaleOrderIdAndStatus(Integer saleOrderId, SaleOrderAllocationStatus status) {
        return saleOrderAllocationRepository.getSaleOrderAllocationBySaleOrderIdAndStatus(
                saleOrderId, status);
    }

    @Override
    public List<SaleOrderAllocation> getSaleOrderAllocationByInboundStorageAndSaleOrder(InboundStorage is, SaleOrder so) {
        return saleOrderAllocationRepository.findAllByInboundStorageAndSaleOrder(is,so);
    }


    @Override
    public void updateStatus(int saleOrderId, SaleOrderAllocationStatus status) {
        List<SaleOrderAllocation> list = saleOrderAllocationRepository.getSaleOrderAllocationBySaleOrderIdAndStatus(
                saleOrderId, SaleOrderAllocationStatus.ALLOCATED);
        list.forEach(item->item.setStatus(status));
        saleOrderAllocationRepository.saveAll(list);

    }

}

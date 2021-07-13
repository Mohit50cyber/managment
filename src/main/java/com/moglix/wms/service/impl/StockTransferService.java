package com.moglix.wms.service.impl;

import com.moglix.wms.entities.StockTransfer;
import com.moglix.wms.repository.StockTransferRepository;
import com.moglix.wms.service.IStockTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StockTransferService implements IStockTransferService {

    @Autowired
    private StockTransferRepository stockTransferRepository;


    @Override
    public List<StockTransfer> saveStockTransfers(List<StockTransfer> stockTransfer) {
        List<StockTransfer> stockTransfers = new ArrayList<>();
        stockTransferRepository.saveAll(stockTransfer).forEach(stockTransfers::add);
        return stockTransfers;
    }
}

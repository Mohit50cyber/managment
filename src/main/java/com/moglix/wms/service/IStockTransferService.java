package com.moglix.wms.service;

import com.moglix.wms.api.request.CreateStockTransferNoteRequest;
import com.moglix.wms.entities.StockTransfer;

import java.util.List;

public interface IStockTransferService {
    List<StockTransfer> saveStockTransfers(List<StockTransfer> stockTransfer);
}

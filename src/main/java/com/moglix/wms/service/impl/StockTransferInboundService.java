package com.moglix.wms.service.impl;


import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.StockTransferInbound;
import com.moglix.wms.entities.StockTransferNote;
import com.moglix.wms.repository.StockTransferInboundRepository;
import com.moglix.wms.repository.StockTransferNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockTransferInboundService {
    @Autowired
    private StockTransferInboundRepository stockTransferInboundRepository;

    public List<StockTransferInbound> saveAll( Iterable<StockTransferInbound> entities){

        List<StockTransferInbound> result = new ArrayList<>();
         stockTransferInboundRepository.saveAll(entities).forEach(result::add);
         return result;
    }

    public StockTransferInbound findByInboundId(Inbound inboundId){
        return stockTransferInboundRepository.findByInbound(inboundId);
    }
}

package com.moglix.wms.repository;


import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.StockTransferInbound;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTransferInboundRepository extends CrudRepository<StockTransferInbound,
        Long> {
    StockTransferInbound findByInbound(Inbound inbound);
}

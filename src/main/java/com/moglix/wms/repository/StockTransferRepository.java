package com.moglix.wms.repository;


import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.StockTransfer;
import com.moglix.wms.entities.StockTransferNote;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTransferRepository extends CrudRepository<StockTransfer,
        Long> {

	
	@Query(value = "select sum(ibs.available_quantity) from inbound_storage ibs,inbound ib,product pro where ibs.available_quantity>0 and ib.product_id=pro.id and ibs.inbound_id=ib.id and ib.warehouse_id=?1 and pro.id=?2  and ibs.confirmed=true group by pro.product_msn ",nativeQuery = true)
	Double getEligibleItemsByWarehouseandProduct(Integer warehouseId, Integer productId);
	
	@Query(value = "select * from stock_transfer where stock_transfer_note_id=?1 ",nativeQuery = true)
	List <StockTransfer> findAllByStockTransferNoteId(Long StockNoteTransferId);
}

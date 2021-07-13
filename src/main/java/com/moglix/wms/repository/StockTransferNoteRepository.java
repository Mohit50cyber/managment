package com.moglix.wms.repository;

import com.moglix.wms.entities.StockTransferNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransferNoteRepository extends JpaRepository<StockTransferNote,
        Long> {

        List<StockTransferNote> findByItemRefAndWarehouseFrom(String ItemRef,
                                                              int warehousefrom);
        Page<StockTransferNote>  findAllByWarehouseFrom(int warehousefrom, Pageable pageable);
        Page<StockTransferNote>  findAllByWarehouseTo(int warehouseTo, Pageable pageable);
//        List<StockTransferNote> findAllByStockTransferNoteIdAndWarehouseTo(long id,int warehouseTo, Pageable pageable);
        StockTransferNote  findAllByStockTransferNoteId(long id);
        Page<StockTransferNote> findAllByWarehouseFromAndWarehouseTo(int warehouseFrom, int wareHouseTo, Pageable pageable);
//        List<StockTransferNote> findAllByStockTransferNoteIdAndWarehouseFrom(long id,int warehouseFrom, Pageable pageable);
}

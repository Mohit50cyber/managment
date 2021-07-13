package com.moglix.wms.repository;

import com.moglix.wms.constants.BatchType;
import com.moglix.wms.dto.FreeInventoryData;
import com.moglix.wms.dto.ReturnInventoryData;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.entities.Batch;

@Repository
public interface BatchRepository extends CrudRepository<Batch, Integer>{
	Batch findByRefNo(String refNo);
	List<Batch> findByRefNoInAndBatchType(List<String> refNos, BatchType batchtype);
	Optional<Batch> findByRefNoAndBatchType(String refNo, BatchType batchType);	
	Optional<List<Batch>>findByEmsReturnId(Integer emsReturnId);
	Optional<List<Batch>>findByEmsReturnIdAndBatchType(Integer emsReturnId, BatchType batchType);
	Optional<Batch> findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(Integer emsReturnId, Integer supplierId);
	List<Batch>findByParentRefNo(String parentRefNo);
	@Modifying
    @Transactional
    @Query("DELETE FROM Batch p WHERE p.id in ?1")
    void deleteBatches(Set<Integer> ids);
	
	
	@Query(value = "select ref_no as refNumber, supplier_po_id as supplierPoId, supplier_po_item_id as supplierPoItemId, sum(ist.available_quantity) as freeQuantity, sum(ist.allocated_quantity) as allocatedQuantity from batch b left join inbound inb on inb.batch_id = b.id left join inbound_storage ist on ist.inbound_id = inb.id left join storage_location sl on ist.storage_location_id = sl.id where b.batch_type = \"CUSTOMER_RETURN\" and sl.type = \"GOOD\" and sl.active = true and b.ref_no = ?1 group by supplier_po_id, supplier_po_item_id", nativeQuery = true)
	List<ReturnInventoryData> getReturnInventoryData(String refNumber);
	
	@Query(value = "select inb.id as inboundId, ref_no as refNumber, supplier_po_id as supplierPoId, supplier_po_item_id as supplierPoItemId, sum(ist.available_quantity) as availableQuantity, sum(ist.allocated_quantity) as allocatedQuantity, b.batch_type as batchType from batch b left join inbound inb on inb.batch_id = b.id left join inbound_storage ist on ist.inbound_id = inb.id left join storage_location sl on ist.storage_location_id = sl.id where sl.type = \"GOOD\" and sl.active = true and ist.confirmed = true and inb.supplier_po_id = ?1 group by inb.type, b.ref_no, supplier_po_id, supplier_po_item_id, inb.id", nativeQuery = true)
	List<FreeInventoryData> getFreeInventoryData(Integer supplierPoId);
}

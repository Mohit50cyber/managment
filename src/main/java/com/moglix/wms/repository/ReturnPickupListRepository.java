package com.moglix.wms.repository;

import com.moglix.wms.constants.ReturnPickupListStatus;
import com.moglix.wms.dto.DnDetailItemDTO;
import com.moglix.wms.entities.ReturnPickupList;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReturnPickupListRepository extends CrudRepository<ReturnPickupList, Integer> {

    Page<ReturnPickupList> findByWarehouseIdAndStatusNotOrderByCreated(Integer warehouseId, ReturnPickupListStatus status, Pageable page);
    Page<ReturnPickupList> findByStatusNotOrderByCreated(ReturnPickupListStatus status, Pageable page);
    Optional<ReturnPickupList> findByCreditNoteNumber(String creditNoteNumber);
	Page<ReturnPickupList> findByWarehouseIdAndCreditNoteNumberAndStatusNotOrderByCreated(Integer warehouseId,
			String searchKey, ReturnPickupListStatus cancelled, Pageable page);
	Page<ReturnPickupList> findByCreditNoteNumberAndStatusNotOrderByCreated(String searchKey,
			ReturnPickupListStatus cancelled, Pageable page);
	@Query(value = "select * from return_pickup_list where ems_return_note_id = ?1", nativeQuery = true)
	ReturnPickupList findByreturnnoteid(Integer returnnoteid);
	
	@Query(value = "select round( coalesce(sum(quantity),0) ,4) from return_pickup_list_item,return_pickup_list where product_id in (select id from product where product_msn=?1) and return_pickup_list.id=return_pickup_list_item.return_pickup_list_id and return_pickup_list.warehouse_id=?2 and return_pickup_list.status in ('INITIATED')", nativeQuery = true)
	Double findDNInitiatedQuantity(String productMSN ,Integer warehouseId);
	
	@Query(value = "select round( coalesce(sum(quantity),0) ,4) from return_pickup_list_item,return_pickup_list where product_id in (select id from product where product_msn=?1) and return_pickup_list.id=return_pickup_list_item.return_pickup_list_id and return_pickup_list.warehouse_id=?2 and return_pickup_list.status in ('CREATED')", nativeQuery = true)
	Double findDNCreatedQuantity(String productMSN ,Integer warehouseId);
	
	@Query(value = "select NULLIF(return_pickup_list.ems_return_note_id,'') as EmsReturnId,round ( return_pickup_list_item.quantity,4) as TotalQuantity,return_pickup_list.credit_note_number as DebitNoteNumber from return_pickup_list_item,return_pickup_list where return_pickup_list_item.product_id in (select id from product where product_msn=?1) and return_pickup_list.id=return_pickup_list_item.return_pickup_list_id and return_pickup_list.warehouse_id=?2 and return_pickup_list.status in ('INITIATED')", nativeQuery = true)
	List<DnDetailItemDTO> findDNInitiatedItems(String productMSN ,Integer warehouseId);
	
	@Query(value = "select NULLIF(return_pickup_list.ems_return_note_id,'') as EmsReturnId,round( return_pickup_list_item.quantity,4) as TotalQuantity,return_pickup_list.credit_note_number as DebitNoteNumber from return_pickup_list_item,return_pickup_list where return_pickup_list_item.product_id in (select id from product where product_msn=?1) and return_pickup_list.id=return_pickup_list_item.return_pickup_list_id and return_pickup_list.warehouse_id=?2 and return_pickup_list.status in ('CREATED')", nativeQuery = true)
	List<DnDetailItemDTO> findDNCreatedItems(String productMSN ,Integer warehouseId);
}

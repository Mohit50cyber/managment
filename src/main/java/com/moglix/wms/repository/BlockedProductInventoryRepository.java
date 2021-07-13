package com.moglix.wms.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.dto.BlockedInventoryDTO;
import com.moglix.wms.entities.BlockedProductInventory;

@Repository
public interface BlockedProductInventoryRepository extends CrudRepository<BlockedProductInventory, Integer> {
	
	
	List<BlockedProductInventory> findByWarehouseIdAndProductMsn(Integer warehouseId, String productMsn);

	@Query(value = "select bpi.blocked_quantity as blockedQuantity, bpi.product_msn as productMsn, bpi.status as status, bpi.warehouse_id as warehouseId, biso.bulk_invoice_id as bulkInvoiceId, biso.buyers_order_id as buyersOrderId, biso.item_ref as itemRef, biso.ordered_quantity as orderedQuantity, biso.status as orderStatus from blocked_product_inventory bpi left join bulk_invoicing_sale_order biso on bpi.product_msn = biso.product_msn and bpi.warehouse_id = biso.warehouse_id where bpi.blocked_quantity > 0 and ordered_quantity > 0 and biso.status != \"CANCELLED\" and bpi.warehouse_id = ?1 group by bpi.product_msn, biso.item_ref", nativeQuery = true)
	List<BlockedInventoryDTO> getBlockedProductInventoryOrderByWarehouseId(Integer warehouseId);

	@Query(value = "select blocked_quantity as blockedQuantity , product_msn as productMsn, status as status, warehouse_id as warehouseId from blocked_product_inventory where product_msn not in ?1 and blocked_quantity > 0 and warehouse_id = ?2", nativeQuery = true)
	List <BlockedInventoryDTO> findByProductMsnNotInAndWarehouseId(Set<String> productMSN, Integer warehouseId);

	@Query(value = "select bpi.blocked_quantity as blockedQuantity, bpi.product_msn as productMsn, bpi.status as status, bpi.warehouse_id as warehouseId, biso.bulk_invoice_id as bulkInvoiceId, biso.buyers_order_id as buyersOrderId, biso.item_ref as itemRef, biso.ordered_quantity as orderedQuantity, biso.status as orderStatus from blocked_product_inventory bpi left join bulk_invoicing_sale_order biso on bpi.product_msn = biso.product_msn and bpi.warehouse_id = biso.warehouse_id where bpi.blocked_quantity > 0 and ordered_quantity > 0 and biso.status != \"CANCELLED\" group by bpi.product_msn, biso.item_ref", nativeQuery = true)
	List<BlockedInventoryDTO> getBlockedProductInventoryOrder();
	
	@Query(value = "select blocked_quantity as blockedQuantity , product_msn as productMsn, status as status, warehouse_id as warehouseId from blocked_product_inventory where product_msn not in ?1 and blocked_quantity > 0", nativeQuery = true)
	List <BlockedInventoryDTO> findByProductMsnNotIn(Set<String> productMSN);

	@Query(value = "select blocked_quantity as blockedQuantity , product_msn as productMsn, status as status, warehouse_id as warehouseId from blocked_product_inventory where blocked_quantity > 0 and warehouse_id = ?1", nativeQuery = true)
	List<BlockedInventoryDTO> findByWarehouseId(Integer warehouseId);
	
	@Query(value = "select * from blocked_product_inventory where warehouse_id = ?1 and product_msn = ?2 and blocked_quantity > 0 and unique_block_transaction_id = ?3", nativeQuery = true)
	BlockedProductInventory findByWarehouseIdAndProductMsnAndUniqueID(Integer warehouseId, String productMsn,String uniqueid);
	
	@Query(value = "select coalesce(sum(blocked_quantity),0) from blocked_product_inventory where warehouse_id = ?1 and product_msn = ?2", nativeQuery = true)
	Double findtotalblockedquantity(Integer warehouseId, String productMsn);

}

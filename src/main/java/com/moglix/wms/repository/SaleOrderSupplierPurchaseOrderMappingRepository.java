package com.moglix.wms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMapping;

/**
 * @author sparsh saxena on 22/3/21
 */

public interface SaleOrderSupplierPurchaseOrderMappingRepository extends CrudRepository<SaleOrderSupplierPurchaseOrderMapping, Integer> {
	
	List<SaleOrderSupplierPurchaseOrderMapping> findByItemRef(String itemRef);
	
	@Query(value = "SELECT sum(quantity) FROM wms.sale_order_supplier_purchase_order_mapping where product_msn = ?1 and warehouse_id= ?2 and status=\"INBOUNDED\"; ", nativeQuery = true)
	Double getInboundedInventoryForOtherOrders(String productMSN, Integer warehouseId);
	
	SaleOrderSupplierPurchaseOrderMapping findByItemRefAndSupplierPoId(String itemRef, Integer supplierPoId);
	
	List<SaleOrderSupplierPurchaseOrderMapping> findAllByItemRefAndSupplierPoId(String itemRef, Integer supplierPoId);
}

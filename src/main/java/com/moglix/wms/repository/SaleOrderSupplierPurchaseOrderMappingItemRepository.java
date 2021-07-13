package com.moglix.wms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMappingItem;

/**
 * @author sparsh saxena on 22/3/21
 */

public interface SaleOrderSupplierPurchaseOrderMappingItemRepository extends CrudRepository<SaleOrderSupplierPurchaseOrderMappingItem, Integer> {

    List<SaleOrderSupplierPurchaseOrderMappingItem> findByItemRefAndSupplierPoIdAndProductMSNAndStatus(String itemRef, Integer supplierPoId, String productMSN, SaleOrderSupplierPurchaseOrderMappingStatus status);

    @Query(value = "SELECT coalesce(sum(quantity), 0) FROM wms" +
            ".sale_order_supplier_purchase_order_mapping_item where " +
            "product_msn = ?1 and warehouse_id= ?2 and " +
            "status=\"BIN_ASSIGNED\"; ", nativeQuery = true)
    Double getInboundedInventoryForOtherOrders(String productMSN,
                                               Integer warehouseId);

    List<SaleOrderSupplierPurchaseOrderMappingItem> findByItemRefAndSaleOrderSupplierPurchaseOrderMappingIdAndStatus(String itemRef, Integer saleOrderSupplierPurchaseOrderMappingId, SaleOrderSupplierPurchaseOrderMappingStatus status);

    List<SaleOrderSupplierPurchaseOrderMappingItem> findByBatchIdAndProductIDAndSupplierPoIdAndSupplierPoItemIdAndPickUpWarehouseIdAndStatus(Integer batchId, Integer productId, Integer supplierPoId, Integer supplierPoItemId, Integer warehouseId, SaleOrderSupplierPurchaseOrderMappingStatus status);

	List <SaleOrderSupplierPurchaseOrderMappingItem> findByItemRefAndSaleOrderIdAndProductMSNAndStatusNot(String itemRef, Integer saleOrderId, String productMSN, SaleOrderSupplierPurchaseOrderMappingStatus status);
}

package com.moglix.wms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moglix.wms.dto.ProductInventoryData;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.projectionObjects.AverageAgeAndActiveMsn;
import com.moglix.wms.projectionObjects.InventoryStats;

/**
 * @author pankaj on 6/5/19
 */
@Repository
public interface ProductInventoryRepository extends CrudRepository<ProductInventory, Integer> {
    Page<ProductInventory> findByWarehouseIdOrderByModifiedDesc(Integer warehouseId, Pageable page);
    ProductInventory findByWarehouseIdAndProductId(Integer warehouseId, Integer productId);
    ProductInventory findByWarehouseIdAndProductProductMsn(Integer warehouseId, String productMsn);
    Page<ProductInventory> findByWarehouseIdAndProductProductMsn(Integer warehouseId, String productMsn, Pageable page);
    List<ProductInventory> findAllByWarehouseIdInAndProductProductMsnIn(List<Integer> warehouseId, List<String> productMsn);
    Page<ProductInventory> findByProductProductMsnAndWarehouseIsoNumberOrderByModifiedDesc(String productMsn, Integer countryId, Pageable page);
    Page<ProductInventory> findAllByWarehouseIsoNumberOrderByModifiedDesc(Integer countryId, Pageable page);
   
    @Override
    List<ProductInventory> findAll();

    @Query("SELECT new com.moglix.wms.projectionObjects.InventoryStats(sum(obj.currentQuantity) as totalInventory, sum(obj.currentQuantity * obj.averagePrice) as totalPrice, sum(obj.availableQuantity) as availableInventory, sum(obj.allocatedQuantity) as allocatedInventory, count(distinct obj.product.productMsn) as msnInInventory) from ProductInventory obj where obj.warehouse.id = :warehouseId and obj.currentQuantity > 0")
    InventoryStats getAggregatedDataByWarehouse(@Param("warehouseId") Integer warehouseId);

    @Query("SELECT new com.moglix.wms.projectionObjects.InventoryStats(sum(obj.currentQuantity) as totalInventory, sum(obj.currentQuantity * obj.averagePrice) as totalPrice, sum(obj.availableQuantity) as availableInventory, sum(obj.allocatedQuantity) as allocatedInventory, count(distinct obj.product.productMsn) as msnInInventory) from ProductInventory obj where obj.currentQuantity > 0")
    InventoryStats getAggregatedData();

    @Query("SELECT new com.moglix.wms.projectionObjects.AverageAgeAndActiveMsn(avg(obj.averageAge) as avgAge, count(distinct obj.product.productMsn) as msnActive) from ProductInventory obj where obj.warehouse.id = :warehouseId")
    AverageAgeAndActiveMsn getAverageAgeAndActiveMsnByWarehouse(@Param("warehouseId") Integer warehouseId);

    @Query("SELECT new com.moglix.wms.projectionObjects.AverageAgeAndActiveMsn(avg(obj.averageAge) as avgAge, count(distinct obj.product.productMsn) as msnActive) from ProductInventory obj")
    AverageAgeAndActiveMsn getAverageAgeAndActiveMsn();
	List<ProductInventory> findByWarehouseIdAndAvailableQuantityGreaterThan(Integer warehouseId, double availableQuantity);
	List<ProductInventory> findByAllocatedQuantityGreaterThan(double value);
	
	//@Query(value = "select sl.type as type, sum(ist.available_quantity) - coalesce(bpi.blocked_quantity, 0) as availableQuantity, pt.product_msn as productMsn, sl.warehouse_id as warehouseId from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on sl.id  = ist.storage_location_id left join blocked_product_inventory bpi on bpi.product_msn = pt.product_msn where sl.warehouse_id = ?1 and sl.type = ?2 and pt.product_msn in ?3 group by pt.product_msn", nativeQuery = true)
	@Query(value = "select a.type AS type, SUM(a.available_quantity) - SUM(COALESCE(b.blocked_quantity, 0)) AS availableQuantity, a.product_msn AS productMsn, a.warehouse_id AS warehouseId from (SELECT sl.type AS type, SUM(ist.available_quantity) AS available_quantity, pt.product_msn AS product_msn, sl.warehouse_id AS warehouse_id FROM inbound_storage ist inner JOIN product pt ON ist.product_id = pt.id inner JOIN storage_location sl ON sl.id = ist.storage_location_id WHERE sl.warehouse_id = ?1 AND sl.type = ?2 AND pt.product_msn IN ?3 And sl.active = true and ist.available_quantity > 0 and ist.confirmed = true GROUP BY pt.product_msn,sl.warehouse_id,sl.type) a left join (select SUM(blocked_quantity) as blocked_quantity, product_msn from blocked_product_inventory WHERE product_msn IN ?3 and status='BLOCKED'and warehouse_id = ?1 GROUP BY product_msn) b on b.product_msn=a.product_msn group by a.product_msn,a.warehouse_id,a.type", nativeQuery = true)
	List<ProductInventoryData>getProductInventoryData(Integer warehouseId, String type, List<String> productMsn);
	
	@Query(value = "select \"GOOD\" as type, bpi.product_msn as productMsn, greatest(0, (coalesce(bpi.blocked_quantity,0) - sum(coalesce(biso.ordered_quantity,0)))) as availableQuantity, bpi.warehouse_id as warehouseId from blocked_product_inventory bpi left join bulk_invoicing_sale_order biso on biso.product_msn = bpi.product_msn AND biso.warehouse_id = bpi.warehouse_id where bpi.product_msn in ?2 and bpi.warehouse_id = ?1 and coalesce(biso.status, \"CREATED\") != \"CANCELLED\" group by bpi.product_msn, bpi.warehouse_id", nativeQuery = true)
	List<ProductInventoryData> getRealtimeProductInventoryData(Integer warehouseId, List<String> productMSNList);

}

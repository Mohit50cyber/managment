package com.moglix.wms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moglix.wms.dto.ExpiredInventoryDTO;
import com.moglix.wms.dto.FreshAvailableQuantityDetail;
import com.moglix.wms.dto.InboundStorageSalesAllocationDTO;
import com.moglix.wms.dto.InventoryDataDTO;
import com.moglix.wms.dto.InventoryDataResult;
import com.moglix.wms.dto.ProductInventoryDetailsDTO;
import com.moglix.wms.dto.VmiReportDataDTO;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;

@Repository
public interface InboundStorageRepository extends CrudRepository<InboundStorage, Integer> {
	List<InboundStorage> findAllByInboundIdIn(List<Integer> inboundIds);
	List<InboundStorage> findAllByAllocatedQuantityGreaterThan(Double allocatedQ);

	List<InboundStorage> findByProductId(Integer productId);

	@Query("SELECT obj FROM InboundStorage obj WHERE obj.product.id = :productId and obj.availableQuantity > 0 order by obj.created")
	List<InboundStorage> findAvailableByProduct(@Param("productId") Integer productId);

	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.product.id = :productId and obj.storageLocation.warehouse.id = :warehouseId and obj.quantity > 0 and obj.storageLocation.type = 'GOOD' and coalesce(obj.expiryDate, '2070-12-16') > current_date")
	List<InboundStorage> findByProductAndWarehouseForTotal(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId);
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.product.id = :productId and obj.storageLocation.warehouse.id = :warehouseId and obj.storageLocation.zone.id = :zoneId and obj.quantity > 0 and coalesce(obj.expiryDate, '2070-12-16') > current_date")
	List<InboundStorage> findByProductAndWarehouseAndZoneForTotal(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId, @Param("zoneId") Integer zoneId);
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.product.id = :productId and obj.storageLocation.warehouse.id = :warehouseId and obj.storageLocation.zone.id = :zoneId and obj.storageLocation.bin.id = :binId and obj.quantity > 0 and coalesce(obj.expiryDate, '2070-12-16') > current_date")
	List<InboundStorage> findByProductAndWarehouseAndZoneAndBinForTotal(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId, @Param("zoneId") Integer zoneId, @Param("binId") Integer binId);

	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.product.id = :productId and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 and obj.storageLocation.active = true and obj.storageLocation.type = 'GOOD' order by obj.created asc")
	List<InboundStorage> findByProductAndWarehouseForAvailable(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId);
	
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.inbound.id = :inboundId and obj.product.id = :productId and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 and obj.storageLocation.active = true and obj.storageLocation.type = 'GOOD' order by obj.created asc")
	List<InboundStorage> findByProductAndWarehouseAndInboundForAvailable(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId, @Param("inboundId") Integer inboundId);
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.product.id = :productId and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 and obj.storageLocation.active = true and obj.storageLocation.type = 'GOOD' and coalesce(obj.expiryDate, '2070-12-16') > current_date order by obj.expiryDate asc, obj.created asc")
	List<InboundStorage> findByProductAndWarehouseForAvailableBasedOnExpiry(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId);
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.inbound.id = :inboundId and obj.product.id = :productId and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 and obj.storageLocation.active = true and obj.storageLocation.type = 'GOOD' and coalesce(obj.expiryDate, '2070-12-16') > current_date order by obj.expiryDate asc, obj.created asc")
	List<InboundStorage> findByProductAndWarehouseAndInboundForAvailableBasedOnExpiry(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId, @Param("inboundId") Integer inboundId);
	
	@Query(value = "select sum(ist.available_quantity) from inbound_storage ist left join product p on ist.product_id = p.id left join storage_location sl on ist.storage_location_id = sl.id where ist.confirmed = true and p.id = ?2 and sl.warehouse_id = ?1 and ist.available_quantity > 0 and sl.active = true and sl.type = \"GOOD\" and current_date() > coalesce(ist.expiry_date, '2070-12-16')", nativeQuery = true)
	Double getTotalExpiredInventoryByWarehouseIdAndProductId(Integer warehouseId, Integer productId);
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.product.id = :productId and obj.storageLocation.zone.id = :zoneId and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 and obj.storageLocation.active = true and obj.storageLocation.type = 'GOOD' and coalesce(obj.expiryDate, '2070-12-16') > current_date order by obj.created asc")
	List<InboundStorage> findByProductAndWarehouseAndZoneForAvailable(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId, @Param("zoneId") Integer zoneId);
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.product.id = :productId and obj.storageLocation.zone.id = :zoneId and obj.storageLocation.bin.id = :binId and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 and obj.storageLocation.active = true and obj.storageLocation.type = 'GOOD' and coalesce(obj.expiryDate, '2070-12-16') > current_date order by obj.created asc")
	List<InboundStorage> findByProductAndWarehouseAndZoneAndBinForAvailable(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId, @Param("zoneId") Integer zoneId, @Param("binId") Integer binId);

   @Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true  and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 order by obj.created asc")
	List<InboundStorage> findByWarehouseForAvailable(@Param("warehouseId") Integer warehouseId);


	List<InboundStorage> findByProductIdAndStorageLocationId(Integer productId, Integer storageLocationId);

	List<InboundStorage> findByProductIdAndStorageLocationIdAndQuantityGreaterThan(Integer productId, Integer storageLocationId, Double quantity);
	
	@Query(value = "select sale_order_allocation.id as orderAllocationId, sale_order_allocation.allocated_quantity as orderAllocationQuantity, sale_order_allocation.available_quantity as orderAvailableQuantity , sale_order_allocation.sale_order_id as orderId from inbound_storage join sale_order_allocation where storage_location_id = ?1 and product_id = ?2 and sale_order_allocation.inbound_storage_id = inbound_storage.id;", nativeQuery = true)
	List<InboundStorageSalesAllocationDTO> getOrderAllocations(Integer storageLocationId, Integer productId);
	
	
	@Query("SELECT obj FROM InboundStorage obj WHERE    obj.allocatedQuantity > :allocatedQuantity and obj.storageLocation.warehouse.id = :warehouseId and obj.availableQuantity > 0 order by obj.created asc")
	List<InboundStorage> findByAllocatedQuantityGreaterThanAndAvailableQuantityGreaterThanAndWarehouseId(@Param("warehouseId") Integer warehouseId,@Param("allocatedQuantity") Double allocatedQ);

	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.storageLocation.type = 'GOOD'")
	List<InboundStorage> findConfirmedGoodBins();

	List<InboundStorage> findByConfirmedTrue();
	
	@Query(value = "SELECT sum(CASE\n" + 
			" WHEN pi.status IS NOT NULL THEN pi.quantity\n" + 
			" WHEN sol.status = \"ALLOCATED\" THEN sol.available_quantity\n" + 
			" ELSE ins.quantity\n" + 
			" END) AS quantity,\n" + 
			" w.name AS warehouseName,\n" + 
			" ins.product_id AS productId,\n" + 
			" p.product_msn AS productMsn,\n" + 
			" p.name AS name,\n" + 
			" p.uom as uom,\n" + 
			" z.name AS zoneId,\n" + 
			" sl.name AS storageLocation,\n" + 
			" CASE\n" + 
			" WHEN pi.status = \"AVAILABLE\" THEN \"PACKED\"\n" + 
			" WHEN sol.status = \"ALLOCATED\" THEN sol.status\n" + 
			" ELSE \"FREE\"\n" + 
			" END AS status\n" + 
			" FROM\n" + 
			" inbound_storage ins\n" + 
			" INNER JOIN\n" + 
			" packet_item pi ON ins.id = pi.inbound_storage_id\n" + 
			" LEFT JOIN\n" + 
			" sale_order_allocation sol ON sol.id = pi.sale_order_allocation_id\n" + 
			" LEFT JOIN\n" + 
			" storage_location sl ON ins.storage_location_id = sl.id\n" + 
			"  LEFT JOIN\n" + 
			" product p ON p.id = ins.product_id\n" + 
			" LEFT JOIN\n" + 
			" zone z ON z.id = sl.zone_id\n" + 
			" LEFT JOIN\n" + 
			" warehouse w ON w.id = sl.warehouse_id\n" + 
			" WHERE\n" + 
			" ROUND(ins.quantity, 4) != 0\n" + 
			" AND sol.status = \"ALLOCATED\"\n" + 
			" AND pi.status != \"CANCELLED\"\n" +
			" AND sl.warehouse_id = ?1 \n"+
			" group by w.name,ins.product_id,p.product_msn,p.name,p.uom,z.name,sl.name, CASE\n" + 
			"  WHEN pi.status = 'AVAILABLE' THEN 'PACKED'\n" + 
			"  WHEN sol.status = 'ALLOCATED' THEN sol.status\n" + 
			"  ELSE 'FREE'\n" + 
			"  END ", nativeQuery = true)
	List<InventoryDataDTO> getInventoryDataByWarehouseId(Integer warehouseId);

	@Query(value = "SELECT sum(CASE\n" + 
			" WHEN pi.status IS NOT NULL THEN pi.quantity\n" + 
			" WHEN sol.status = \"ALLOCATED\" THEN sol.available_quantity\n" + 
			" ELSE ins.quantity\n" + 
			" END )AS quantity,\n" + 
			" w.name AS warehouseName,\n" + 
			" ins.product_id AS productId,\n" + 
			" p.product_msn AS productMsn,\n" + 
			" p.name AS name,\n" + 
			" p.uom,\n" + 
			" z.name AS zoneId,\n" + 
			" sl.name AS storageLocation,\n" + 
			" CASE\n" + 
			" WHEN pi.status = \"AVAILABLE\" THEN \"PACKED\"\n" + 
			" WHEN sol.status = \"ALLOCATED\" THEN sol.status\n" + 
			" ELSE \"FREE\"\n" + 
			" END AS status\n" + 
			" FROM\n" + 
			" inbound_storage ins\n" + 
			" INNER JOIN\n" + 
			" packet_item pi ON ins.id = pi.inbound_storage_id\n" + 
			" LEFT JOIN\n" + 
			" sale_order_allocation sol ON sol.id = pi.sale_order_allocation_id\n" + 
			" LEFT JOIN\n" + 
			" storage_location sl ON ins.storage_location_id = sl.id\n" + 
			"  LEFT JOIN\n" + 
			" product p ON p.id = ins.product_id\n" + 
			" LEFT JOIN\n" + 
			" zone z ON z.id = sl.zone_id\n" + 
			" LEFT JOIN\n" + 
			" warehouse w ON w.id = sl.warehouse_id\n" + 
			" WHERE\n" + 
			" ROUND(ins.quantity, 4) != 0\n" + 
			" AND pi.status != \"CANCELLED\"\n" +
			" AND sol.status = \"ALLOCATED\" \n" +
			" group by w.name,ins.product_id,p.product_msn,p.name,p.uom,z.name,sl.name, CASE\n" + 
			"  WHEN pi.status = 'AVAILABLE' THEN 'PACKED'\n" + 
			"  WHEN sol.status = 'ALLOCATED' THEN sol.status\n" + 
			"  ELSE 'FREE'\n" + 
			"  END ", nativeQuery = true)
	List<InventoryDataDTO> getInventoryData();

	List<InboundStorage> findByInbound(Inbound inbound);
	
	 @Query(value = "SELECT sum(ins.available_quantity ) AS quantity, w.name AS warehouseName, p.product_msn AS productMsn, p.name AS name, p.uom as uom, z.name AS zone, sl.name AS bin,\"FREE\" AS status FROM inbound_storage ins LEFT JOIN storage_location sl ON ins.storage_location_id = sl.id LEFT JOIN product p ON p.id = ins.product_id LEFT JOIN zone z ON z.id = sl.zone_id LEFT JOIN warehouse w ON w.id = sl.warehouse_id LEFT JOIN inbound inb on ins.inbound_id = inb.id  WHERE ROUND(ins.available_quantity, 4) > 0 AND w.id = ?1 and inventorize = true group by w.name , ins.product_id ,p.product_msn,p.name,p.uom, z.name ,sl.name;", nativeQuery = true)
	 List<InventoryDataDTO> getFreeInventoryDataByWarehouseId(Integer warehouseId);
	 
	 @Query(value = "SELECT sum(ins.available_quantity ) AS quantity, w.name AS warehouseName, p.product_msn AS productMsn, p.name AS name, p.uom as uom, z.name AS zone, sl.name AS bin,\"FREE\" AS status, pr.invoice_number as invoiceNumber FROM inbound_storage ins LEFT JOIN storage_location sl ON ins.storage_location_id = sl.id LEFT JOIN product p ON p.id = ins.product_id LEFT JOIN zone z ON z.id = sl.zone_id LEFT JOIN warehouse w ON w.id = sl.warehouse_id LEFT JOIN inbound inb on ins.inbound_id = inb.id left join batch b on inb.batch_id = b.id left join packet_return pr on b.ems_return_id = pr.ems_return_id  WHERE ROUND(ins.available_quantity, 4) > 0 AND w.id = ?1 and inb.inventorize = false and inb.type = \"CUSTOMER_RETURN\" group by w.name , ins.product_id ,p.product_msn,p.name,p.uom, z.name ,sl.name, pr.invoice_number;", nativeQuery = true)
	 List<InventoryDataDTO> getFreeReturnInventoryDataByWarehouseId(Integer warehouseId);
	 
	 @Query(value = "SELECT sum(ins.available_quantity ) AS quantity, w.name AS warehouseName, \n" + 
	 		"p.product_msn AS productMsn,\n" + 
	 		" p.name AS name,\n" + 
	 		" p.uom as uom,\n" + 
	 		" z.name AS zone,\n" + 
	 		" sl.name AS bin,\n" + 
	 		" \"FREE\" AS status\n" + 
	 		" FROM inbound_storage ins \n" + 
	 		" LEFT JOIN storage_location sl ON ins.storage_location_id = sl.id \n" + 
	 		" LEFT JOIN product p ON p.id = ins.product_id\n" + 
	 		" LEFT JOIN zone z ON z.id = sl.zone_id \n" + 
	 		" LEFT JOIN warehouse w ON w.id = sl.warehouse_id \n" + 
	 		" LEFT JOIN inbound inb on ins.inbound_id = inb.id \n" + 
	 		" WHERE ROUND(ins.available_quantity, 4) > 0 and inb.inventorize = true\n" + 
	 		" group by w.name , ins.product_id ,p.product_msn,p.name,p.uom, z.name ,sl.name", nativeQuery = true)
	List<InventoryDataDTO> getFreeInventoryData();
	
	 
	@Query(value = " SELECT sum(ins.available_quantity ) AS quantity, w.name AS warehouseName, \n" + 
			"p.product_msn AS productMsn,\n" + 
			" p.name AS name,\n" + 
			" p.uom as uom,\n" + 
			" z.name AS zone,\n" + 
			" sl.name AS bin,\n" + 
			" pr.invoice_number as invoiceNumber,\n" + 
			" \"FREE\" AS status\n" + 
			" FROM inbound_storage ins \n" + 
			" LEFT JOIN storage_location sl ON ins.storage_location_id = sl.id \n" + 
			" LEFT JOIN product p ON p.id = ins.product_id\n" + 
			" LEFT JOIN zone z ON z.id = sl.zone_id \n" + 
			" LEFT JOIN warehouse w ON w.id = sl.warehouse_id \n" + 
			" LEFT JOIN inbound inb on ins.inbound_id = inb.id\n" + 
			" LEFT JOIN batch b on inb.batch_id = b.id\n" + 
			" LEFT JOIN packet_return pr on b.ems_return_id = pr.ems_return_id\n" + 
			" WHERE ROUND(ins.available_quantity, 4) > 0 and inb.inventorize = false and inb.type = \"CUSTOMER_RETURN\"\n" + 
			" group by w.name , ins.product_id ,p.product_msn,p.name,p.uom, z.name ,sl.name, pr.invoice_number", nativeQuery = true) 
	List<InventoryDataDTO> getReturnFreeInventoryData();

	 
	 @Query(value = "SELECT sol.available_quantity\n" + 
			    " AS quantity,\n" + 
				" w.name AS warehouseName,\n" + 
				" p.product_msn AS productMsn,\n" + 
				" p.name AS name,\n" + 
				" p.uom as uom,\n" + 
				" z.name AS zone,\n" + 
				" sl.name AS bin,\n" + 
				" \"ALLOCATED\" as status\n" + 
				" FROM\n" + 
				" inbound_storage ins\n" + 
				" INNER JOIN\n" + 
				" packet_item pi ON ins.id = pi.inbound_storage_id\n" + 
				" LEFT JOIN\n" + 
				" sale_order_allocation sol ON sol.id = pi.sale_order_allocation_id\n" + 
				" LEFT JOIN\n" + 
				" storage_location sl ON ins.storage_location_id = sl.id\n" + 
				"  LEFT JOIN\n" + 
				" product p ON p.id = ins.product_id\n" + 
				" LEFT JOIN\n" + 
				" zone z ON z.id = sl.zone_id\n" + 
				" LEFT JOIN\n" + 
				" warehouse w ON w.id = sl.warehouse_id\n" + 
				" WHERE\n" + 
				"  sol.id = ?1 \n", nativeQuery = true)

	 List<InventoryDataDTO> getInventoryDataBySaleOrderAllocationIdAndAllocated(Integer saleOrderAllocationId);
	
	 
	 @Query(value = "SELECT sol.packed_quantity\n" + 
			    " AS quantity,\n" + 
				" w.name AS warehouseName,\n" + 
				" p.product_msn AS productMsn,\n" + 
				" p.name AS name,\n" + 
				" p.uom as uom,\n" + 
				" z.name AS zone,\n" + 
				" sl.name AS bin,\n" + 
				" \"PACKED\" as status\n" + 
				" FROM\n" + 
				" inbound_storage ins\n" + 
				" INNER JOIN\n" + 
				" packet_item pi ON ins.id = pi.inbound_storage_id\n" + 
				" LEFT JOIN\n" + 
				" sale_order_allocation sol ON sol.id = pi.sale_order_allocation_id\n" + 
				" LEFT JOIN\n" + 
				" storage_location sl ON ins.storage_location_id = sl.id\n" + 
				"  LEFT JOIN\n" + 
				" product p ON p.id = ins.product_id\n" + 
				" LEFT JOIN\n" + 
				" zone z ON z.id = sl.zone_id\n" + 
				" LEFT JOIN\n" + 
				" warehouse w ON w.id = sl.warehouse_id\n" + 
				" WHERE\n" + 
				"  sol.id = ?1 \n", nativeQuery = true)

	 List<InventoryDataDTO> getInventoryDataBySaleOrderAllocationIdAndpacked(Integer saleOrderAllocationId);

	 
	 @Query(value = "select  w.name as warehouseName,p.product_msn as productMsn,p.name as name, sum(soa.available_quantity) as quantity, p.uom as uom ,z.name as zone,\n" + 
	 		"	 sl.name as bin,\"ALLOCATED\" as status  from product_inventory pi\n" + 
	 		"	 left join product p on p.id=pi.product_id\n" + 
	 		"	 left join inbound_storage ins on ins.product_id=pi.product_id\n" + 
	 		"	 left join storage_location sl on sl.id=ins.storage_location_id \n" + 
	 		"	 left join sale_order_allocation soa on soa.inbound_storage_id=ins.id\n" + 
	 		"	 left join zone z on z.id=sl.zone_id\n" + 
	 		"	 left join warehouse w on w.id=pi.warehouse_id\n" + 
	 		"	 where  sl.warehouse_id=pi.warehouse_id and soa.status='ALLOCATED' and  soa.available_quantity>0 \n" +
	 		"    and pi.warehouse_id= ?1 group by p.id,sl.id ", nativeQuery = true)

	 List<InventoryDataDTO> getInventoryAllocatedByWarehouseId(Integer warehouseId);
	 
	 @Query(value = "select  w.name as warehouseName,p.product_msn as productMsn,p.name as name, sum(soa.available_quantity) as quantity, p.uom as uom ,z.name as zone,\n" + 
		 		"	 sl.name as bin,\"ALLOCATED\" as status  from product_inventory pi\n" + 
		 		"	 left join product p on p.id=pi.product_id\n" + 
		 		"	 left join inbound_storage ins on ins.product_id=pi.product_id\n" + 
		 		"	 left join storage_location sl on sl.id=ins.storage_location_id \n" + 
		 		"	 left join sale_order_allocation soa on soa.inbound_storage_id=ins.id\n" + 
		 		"	 left join zone z on z.id=sl.zone_id\n" + 
		 		"	 left join warehouse w on w.id=pi.warehouse_id\n" + 
		 		"	 where  sl.warehouse_id=pi.warehouse_id and soa.status='ALLOCATED'  and soa.available_quantity>0 group by p.id,sl.id ", nativeQuery = true)

		 List<InventoryDataDTO> getInventoryAllocated();
	 
	 
	 
	 @Query(value = "select  w.name as warehouseName,pi.id as productId,p.product_msn as productMsn,p.name as name, pi.allocated_quantity-sum(soa.available_quantity) as quantity, p.uom as uom ,z.name as zone,sl.name as bin ,\n" + 
	 		"\"PACKED\" as status  from product_inventory pi\n" + 
	 		"left join product p on p.id=pi.product_id\n" + 
	 		"left join inbound_storage ins on ins.product_id=pi.product_id\n" + 
	 		"left join storage_location sl on sl.id=ins.storage_location_id \n" + 
	 		"left join sale_order_allocation soa on soa.inbound_storage_id=ins.id\n" + 
	 		"left join zone z on z.id=sl.zone_id\n" + 
	 		"left join warehouse w on w.id=pi.warehouse_id\n" + 
	 		"where  sl.warehouse_id=pi.warehouse_id and soa.status='ALLOCATED' and pi.warehouse_id= ?1  and soa.available_quantity>=0  group by pi.id,w.id " , nativeQuery = true)

	 List<InventoryDataDTO> getInventoryPackedByWarehouseId(Integer warehouseId);
   
	 @Query(value = "select  w.name as warehouseName,pi.id as productId,p.product_msn as productMsn,p.name as name, pi.allocated_quantity-sum(soa.available_quantity) as quantity, p.uom as uom ,z.name as zone,sl.name as bin,\n" + 
		 		"\"PACKED\" as status  from product_inventory pi\n" + 
		 		"left join product p on p.id=pi.product_id\n" + 
		 		"left join inbound_storage ins on ins.product_id=pi.product_id\n" + 
		 		"left join storage_location sl on sl.id=ins.storage_location_id \n" + 
		 		"left join sale_order_allocation soa on soa.inbound_storage_id=ins.id\n" + 
		 		"left join zone z on z.id=sl.zone_id\n" + 
		 		"left join warehouse w on w.id=pi.warehouse_id\n" + 
		 		"where  sl.warehouse_id=pi.warehouse_id and soa.status='ALLOCATED'  and soa.available_quantity>=0  group by pi.id,w.id", nativeQuery = true)

		 List<InventoryDataDTO> getInventoryPacked();

	List<InboundStorage> findByQuantityGreaterThan(double quantity);

	@Query(value = "select i.warehouse_name as warehouseName, p.product_msn as productMsn, p.name as productName, i.type as inventoryType, z.name as zone, sl.name as bin, i.supplier_po_id as supplierPoId, i.supplier_po_item_id as supplierPoItemId, round(i.purchase_price, 2) as purchasePrice, ist.allocated_quantity as allocatedQuantity, ist.available_quantity as availableQuantity, i.quantity as totalQuantity, i.inventorize, b.created as mrnDate, pr.invoice_number as invoiceNumber from inbound_storage ist left join inbound i on ist.inbound_id = i.id left join product p on i.product_id = p.id left join storage_location sl on ist.storage_location_id  = sl.id left join batch b on i.batch_id = b.id left join zone z on sl.zone_id = z.id left join packet_return pr on b.ems_return_id = pr.ems_return_id  where ist.quantity > 0", nativeQuery = true)
	List<InventoryDataResult>getInventoryReportData();
	
	
	@Query(value = "select pt.id as productId, pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id left join warehouse w on sl.warehouse_id = w.id where pt.product_msn = ?1 and ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?2 and sl.zone_id = ?3 group by ist.product_id, sl.warehouse_id",
			countQuery = "select count(productMsn) from ((select pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id left join warehouse w on sl.warehouse_id = w.id where pt.product_msn = ?1 and ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?2 and sl.zone_id = ?3 group by ist.product_id, sl.warehouse_id) AS T)",
			nativeQuery = true)
	Page<ProductInventoryDetailsDTO> getInventoryDetailsByProductWarehouseAndZone(String productMsn, Integer warehouseId, Integer zoneId, Pageable page);
	
	
	@Query(value = "select pt.id as productId, pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id left join warehouse w on sl.warehouse_id = w.id where pt.product_msn = ?1 and ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?2 and sl.zone_id = ?3 and sl.bin_id = ?4 group by ist.product_id, sl.warehouse_id",
			countQuery = "select count(productMsn) from ((select pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id left join warehouse w on sl.warehouse_id = w.id where pt.product_msn = ?1 and ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?2 and sl.zone_id = ?3 and sl.bin_id = ?4 group by ist.product_id, sl.warehouse_id) AS T);",
			nativeQuery = true)
	Page<ProductInventoryDetailsDTO> getInventoryDetailsByProductWarehouseAndZoneAndBin(String productMsn, Integer warehouseId,
			Integer zoneId, Integer binId, Pageable page);
	
	@Query(value = "select pt.id as productId, pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id left join warehouse w on sl.warehouse_id = w.id where ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?1 and sl.zone_id = ?2 group by ist.product_id, sl.warehouse_id",
			countQuery =  "select count(productMsn) from ((select pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id left join warehouse w on sl.warehouse_id = w.id where ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?1 and sl.zone_id = ?2 group by ist.product_id, sl.warehouse_id) AS T)",
			nativeQuery =  true)
	Page<ProductInventoryDetailsDTO> getInventoryDetailsByWarehouseAndZone(Integer warehouseId, Integer zoneId, Pageable page);
	
	@Query(value = "select pt.id as productId, pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id  left join warehouse w on sl.warehouse_id = w.id where ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?1 and sl.zone_id = ?2 and bin_id = ?3 group by ist.product_id, sl.warehouse_id",
			countQuery = "select count(productMsn) from ((select pt.product_msn as productMsn, round(sum(ist.available_quantity), 4) as availableQuantity, round(sum(ist.allocated_quantity), 4) as allocatedQuantity, round(sum(ist.quantity),4) as currentQuantity, sl.warehouse_id as warehouseId, w.name as warehouseName, pt.name as productName from inbound_storage ist left join product pt on ist.product_id = pt.id left join storage_location sl on ist.storage_location_id = sl.id  left join warehouse w on sl.warehouse_id = w.id where ist.confirmed = true and sl.type = \"GOOD\" and sl.active = true and sl.warehouse_id = ?1 and sl.zone_id = ?2 and bin_id = ?3 group by ist.product_id, sl.warehouse_id) AS T)",
			nativeQuery = true)
	Page<ProductInventoryDetailsDTO> getInventoryDetailsByWarehouseAndZoneAndBin(Integer warehouseId, Integer zoneId,
			Integer binId, Pageable page);
	
	
	 @Query(value = "select w.name as warehouseName, pic.product_msn as productMsn, pic.minimum_quantity as minimumQuantity,pic.maximum_quantity as maximumQuantity,p.buyers_plant_id as plantId,pic.warehouse_id as warehouseId,pic.purchase_price as purchasePrice from product_inventory_config pic  left join plant_product_inventory_config_mapping ppicm on pic.id = ppicm.product_inventory_config_id left join  plant p  on p.id = ppicm.plant_id left join warehouse w on w.id = pic.warehouse_id where w.iso_number = ?1", nativeQuery = true)
	 List<VmiReportDataDTO> getVmiReport(Integer iso_number);
	 
	@Query(value = "select sum(ist.available_quantity) as quantity, p.product_msn as productMsn, sl.name as storageLocation, cast(ist.expiry_date as date) as expiryDate, w.name as warehouse from inbound_storage ist left join product p on ist.product_id = p.id left join storage_location sl on ist.storage_location_id = sl.id  left join warehouse w on sl.warehouse_id = w.id where ist.type = \"EXPIRED\" group by ist.product_id, sl.warehouse_id , sl.name, ist.expiry_date", nativeQuery = true) 
	List<ExpiredInventoryDTO> getExpiredInventory();
	
	@Query(value = "select sum(ist.available_quantity) as quantity, p.product_msn as productMsn, sl.name as storageLocation, cast(ist.expiry_date as date) as expiryDate, w.name as warehouse from inbound_storage ist left join product p on ist.product_id = p.id left join storage_location sl on ist.storage_location_id = sl.id left join warehouse w on sl.warehouse_id = w.id where ist.type = \"EXPIRED\" and sl.warehouse_id = ?1 group by ist.product_id, sl.warehouse_id , sl.name, ist.expiry_date", nativeQuery = true)
	List<ExpiredInventoryDTO> getExpiredInventoryByWarehouseId(Integer warehouseId);
	
	@Query(value = "select p.product_msn as productMsn, sum(ist.available_quantity) as quantity , inb.supplier_po_id as supplierPoId, inb.supplier_po_item_id as supplierPoItemId, b.ref_no as mrnId from inbound_storage ist left join product p on ist.product_id = p.id left join storage_location sl on ist.storage_location_id = sl.id left join inbound inb on ist.inbound_id = inb.id left join batch b on inb.batch_id = b.id where b.batch_type in (\"INBOUND\") and ist.available_quantity > 0 and confirmed = true and sl.type = \"GOOD\" group by ist.product_id, inb.supplier_po_id, inb.supplier_po_item_id, b.ref_no", nativeQuery = true)
	List<FreshAvailableQuantityDetail> getFreshAvailableQuantity();
	
	@Query("SELECT obj FROM InboundStorage obj WHERE obj.confirmed = true and obj.inbound.id = :inboundId and obj.storageLocation.active = true and obj.storageLocation.type = 'GOOD' order by obj.created asc")
	List<InboundStorage> findByInbound(@Param("inboundId") Integer inboundId);
	
	@Query(value = "SELECT coalesce(sum(obj.available_quantity),0) as available_quantity FROM inbound_storage obj join storage_location sl on obj.storage_location_id = sl.id  and sl.warehouse_id = ?1 and obj.available_quantity > 0 and sl.active = true  WHERE obj.confirmed = true and obj.product_id = (Select id from product where product_msn=?2) order by obj.created desc", nativeQuery = true)
	Double totalAvailableQTY(@Param("warehouseId") Integer warehouseId, @Param("productMSN") String productMSN);
	
	@Query(value = "select round (coalesce(sum(pi.quantity),0),4) from packet_item pi,sale_order so,packet p,product pr where pi.sale_order_id=so.id and pi.packet_id=p.id and so.product_id=pr.id and pr.product_msn=?1 and so.warehouse_id=?2 and p.status in ('INVOICED','PICKUPLIST_DONE','SCANNED')", nativeQuery = true)
	Double totalPackedQuantity(String productMsn,Integer warehouseId);

	@Query(value = "select round (coalesce(sum(pi.quantity),0),4) from packet_item pi,sale_order so,packet p,product pr,storage_location sl,inbound_storage ibs where pi.sale_order_id=so.id and pi.packet_id=p.id and so.product_id=pr.id and pr.product_msn=?1 and so.warehouse_id=?2 and pi.inbound_storage_id=ibs.id and ibs.storage_location_id=sl.id and sl.zone_id=?3 and p.status in ('INVOICED','PICKUPLIST_DONE','SCANNED')", nativeQuery = true)
	Double totalPackedQuantityByZone(String productMsn,Integer warehouseId,Integer zone);

	@Query(value = "select round (coalesce(sum(pi.quantity),0),4) from packet_item pi,sale_order so,packet p,product pr,storage_location sl,inbound_storage ibs where pi.sale_order_id=so.id and pi.packet_id=p.id and so.product_id=pr.id and pr.product_msn=?1 and so.warehouse_id=?2 and pi.inbound_storage_id=ibs.id and ibs.storage_location_id=sl.id and sl.zone_id=?3 and sl.bin_id=?4 and p.status in ('INVOICED','PICKUPLIST_DONE','SCANNED')", nativeQuery = true)
	Double totalPackedQuantityByZoneAndBin(String productMsn,Integer warehouseId,Integer zone,Integer bin);

	@Query(value = "select  coalesce(sum(available_quantity),0) from inbound_storage where inbound_id=?1 and confirmed=true", nativeQuery = true)
	Double availableQuantityByInbound(Integer inboundid);
}

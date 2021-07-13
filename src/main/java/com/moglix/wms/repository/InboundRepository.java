package com.moglix.wms.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.dto.DnDataDTO;
import com.moglix.wms.dto.InboundPoItemIdQuntityDTO;
import com.moglix.wms.dto.InventoryDataDTO;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.Product;

/**
 * @author pankaj on 30/4/19
 */
@Repository
public interface InboundRepository extends CrudRepository<Inbound, Integer>, JpaSpecificationExecutor<Inbound> {
    List<Inbound> findByBatchId(Integer batchId);
    Inbound findByBatchIdAndWarehouseIdAndProductId(Integer batchId, Integer warehouseId, Integer productId);
    Inbound findByBatchIdAndWarehouseIdAndProductIdAndSupplierPoIdAndSupplierPoItemIdAndStatus(Integer batchId, Integer warehouseId, Integer productId, Integer supplierPoId, Integer supplierPoItemId, InboundStatusType status);
    
    Page<Inbound> findAllByOrderByModifiedDesc(Pageable page);
    Page<Inbound> findByWarehouseId(Integer warehouseId, Pageable page);
    
    Page<Inbound>findByWarehouseIdAndProduct(Integer warehouseId, Product product, Pageable page);
        
    @Modifying
    @Transactional
    @Query("DELETE FROM Inbound p WHERE p.id in ?1")
    void deleteInbounds(Set<Integer> ids);
    
    
   
    @Query(value = "SELECT\n" + 
    		" sum(ins.available_quantity ) AS quantity,\n" + 
    		" w.name AS warehouseName,\n" + 
    		" ins.product_id AS productId,\n" + 
    		" p.product_msn AS productMsn,\n" + 
    		" p.name AS name,\n" + 
    		" p.uom as uom,\n" + 
    		" z.name AS zoneId,\n" + 
    		" sl.name AS storageLocation,\n" + 
    		"\"FREE\" AS status\n" + 
    		" FROM\n" + 
    		" inbound_storage ins\n" + 
    		" LEFT JOIN\n" + 
    		" storage_location sl ON ins.storage_location_id = sl.id\n" + 
    		" LEFT JOIN\n" + 
    		" product p ON p.id = ins.product_id\n" + 
    		" LEFT JOIN\n" + 
    		" zone z ON z.id = sl.zone_id\n" + 
    		" LEFT JOIN\n" + 
    		" warehouse w ON w.id = sl.warehouse_id\n" + 
    		" WHERE\n" + 
    		" ROUND(ins.available_quantity, 4) > 0\n" + 
			" AND w.id = ?1 \n" +
    		" group by w.name , ins.product_id ,p.product_msn,p.name,p.uom, z.name ,sl.name", nativeQuery = true)
	List<InventoryDataDTO> getInboundDataByWarehouseId(Integer warehouseId);
    
    @Query(value = "SELECT\n" + 
    		" sum(ins.available_quantity) AS quantity,\n" + 
    		" w.name AS warehouseName,\n" + 
    		" ins.product_id AS productId,\n" + 
    		" p.product_msn AS productMsn,\n" + 
    		" p.name AS name,\n" + 
    		" p.uom as uom,\n" + 
    		" z.name AS zoneId,\n" + 
    		" sl.name AS storageLocation,\n" + 
    		"\"FREE\" AS status\n" + 
    		" FROM\n" + 
    		" inbound_storage ins\n" + 
    		" LEFT JOIN\n" + 
    		" storage_location sl ON ins.storage_location_id = sl.id\n" + 
    		" LEFT JOIN\n" + 
    		" product p ON p.id = ins.product_id\n" + 
    		" LEFT JOIN\n" + 
    		" zone z ON z.id = sl.zone_id\n" + 
    		" LEFT JOIN\n" + 
    		" warehouse w ON w.id = sl.warehouse_id\n" + 
    		" WHERE\n" + 
    		" ROUND(ins.available_quantity, 4) > 0\n"+
    		" group by w.name , ins.product_id ,p.product_msn,p.name,p.uom, z.name ,sl.name", nativeQuery = true)
	
	List<InventoryDataDTO> getInboundData();
    
    @Query("SELECT obj FROM Inbound obj WHERE obj.batch.refNo = :refNo and obj.supplierPoItemId = :supplierPoItemId and obj.supplierPoId = :supplierPoId")
	Inbound getInboundtoUpdate(@Param("supplierPoItemId") Integer supplierPoItemId,
			@Param("refNo") String refNo, @Param("supplierPoId") Integer supplierPoId);


    @Query(value = "SELECT w.name as warehouseName, inb.product_id as productId, p.product_msn as productMsn, p.name as name, sum(inb.quantity) as quantity, inb.uom, inb.status as status\n" + 
			"FROM inbound inb\n" + 
			"LEFT JOIN product p\n" + 
			"ON inb.product_id = p.id\n" + 
			"LEFT JOIN warehouse w\n" + 
			"ON w.id = inb.warehouse_id\n" +
			"WHERE inb.status = \"STARTED\"\n" + 
			"AND inb.warehouse_id = ?1 \n" +
			"AND inb.inventorize = true \n" +			
			" group by  w.name,inb.product_id,p.product_msn,p.name,inb.uom,inb.status", nativeQuery = true)
	List<InventoryDataDTO> getInboundDataByWarehouseIdStarted(Integer warehouseId);
    
    
    @Query(value = "SELECT\n" + 
    		"  w.name as warehouseName,\n" + 
    		"  inb.product_id as productId,\n" + 
    		"  p.product_msn as productMsn,\n" + 
    		"  p.name as name,\n" + 
    		"  sum(inb.quantity) as quantity,\n" + 
    		"  inb.uom,\n" + 
    		"  inb.status as status,\n" + 
    		"  pr.invoice_number as invoiceNumber,\n" + 
    		"  b.id\n" + 
    		"FROM\n" + 
    		"  inbound inb\n" + 
    		"  LEFT JOIN product p ON inb.product_id = p.id\n" + 
    		"  LEFT JOIN warehouse w ON w.id = inb.warehouse_id\n" + 
    		"  LEFT JOIN batch b on b.id = inb.batch_id\n" + 
    		"  LEFT JOIN packet_return pr on b.ems_return_id = pr.ems_return_id\n" + 
    		"WHERE\n" + 
    		"  inb.status = \"STARTED\"\n" + 
    		"  and inb.type = \"CUSTOMER_RETURN\"\n" + 
    		"  AND inb.warehouse_id = ?1\n" + 
    		"  AND inb.inventorize = false\n" + 
    		"group by\n" + 
    		"  w.name,\n" + 
    		"  inb.product_id,\n" + 
    		"  p.product_msn,\n" + 
    		"  p.name,\n" + 
    		"  inb.uom,\n" + 
    		"  inb.status,\n" + 
    		"  b.id, pr.invoice_number;", nativeQuery = true)
    List<InventoryDataDTO> getReturnInboundDataByWarehouseIdStarted(Integer warehouseId);
    
    @Query(value = "SELECT w.name as warehouseName, inb.product_id as productId, p.product_msn as productMsn, p.name as name, sum(inb.quantity) as quantity, inb.uom, inb.status as status\n" + 
			"FROM inbound inb\n" + 
			"LEFT JOIN product p\n" + 
			"ON inb.product_id = p.id\n" + 
			"LEFT JOIN warehouse w\n" + 
			"ON w.id = inb.warehouse_id\n" +
			"WHERE inb.status = \"STARTED\" \n"+
			"and inb.inventorize = true \n"+
			"group by  w.name,inb.product_id,p.product_msn,p.name,inb.uom,inb.status;", nativeQuery = true)
	List<InventoryDataDTO> getInboundDataStarted();
    
    @Query(value = "SELECT w.name as warehouseName, inb.product_id as productId, p.product_msn as productMsn, p.name as name, sum(inb.quantity) as quantity, inb.uom, inb.status as status, pr.invoice_number as invoiceNumber FROM inbound inb LEFT JOIN product p ON inb.product_id = p.id LEFT JOIN warehouse w ON w.id = inb.warehouse_id LEFT JOIN batch b on inb.batch_id = b.id LEFT JOIN packet_return pr on b.ems_return_id = pr.ems_return_id WHERE inb.status = \"STARTED\" and inb.inventorize = false and inb.type = \"CUSTOMER_RETURN\" group by  w.name,inb.product_id,p.product_msn,p.name,inb.uom,inb.status, b.id, pr.invoice_number", nativeQuery = true)
    List<InventoryDataDTO> getReturnInboundDataStarted();
    
	List<Inbound> findBySupplierPoIdAndSupplierPoItemIdAndType(Integer supplierPoId, Integer supplierPoItemId,
			InboundType inboundtype);
	List<Inbound> findByIdIn(List<Integer> inboundIds);
	List<Inbound> findBySupplierPoItemId(Integer poItemId);
	
	@Query(value = "select supplier_po_item_id as poItemId,type ,sum(quantity) as totalQuantity from inbound where type='NEW' group by supplier_po_item_id,type;",nativeQuery = true)
	List<InboundPoItemIdQuntityDTO> getInboundQuantityByPoItemId();
	
	@Query(value = "SELECT coalesce(count(id),0) FROM wms.inbound where warehouse_id = ?1 and status = 'STARTED';",nativeQuery = true)
	Integer pickuplistcount(Integer warehouseId);

	@Query(value = "select w.name as warehouse , p.product_msn as productmsn , p.name as name , rpl.ems_return_note_id as returnid,rpl.total_quantity as quantity , rpl.status as status , rpl.credit_note_number as debitnotenumber from return_pickup_list rpl left join return_pickup_list_item rpli on rpl.id=rpli.return_pickup_list_id left join product p on p.id = rpli.product_id left join warehouse w on w.id=rpl.warehouse_id where rpl.status in ('INITIATED','CREATED') and rpli.quantity>0 and rpl.warehouse_id=?1 order by w.name , rpl.status",nativeQuery = true)
	List<DnDataDTO> getDnDataByWarehouseId(Integer warehouseId);
	
	@Query(value = "select w.name as warehouse , p.product_msn as productmsn , p.name as name , rpl.ems_return_note_id as returnid,rpl.total_quantity as quantity , rpl.status as status , rpl.credit_note_number as debitnotenumber from return_pickup_list rpl left join return_pickup_list_item rpli on rpl.id=rpli.return_pickup_list_id left join product p on p.id = rpli.product_id left join warehouse w on w.id=rpl.warehouse_id where rpl.status in ('INITIATED','CREATED') and rpli.quantity>0 order by w.name , rpl.status",nativeQuery = true)
	List<DnDataDTO> getDnData();

	@Query(value = "select * from wms.inbound where supplier_po_id like ?1%  and status='STARTED' and warehouse_id=?2 order by created desc",nativeQuery = true)
	Page<Inbound> findBySupplierPOIdandWarehouseId( String supplierPoId,Integer warehouse,Pageable page);

	@Query(value = "select * from wms.inbound where status='STARTED' and warehouse_id=?1 order by created desc",nativeQuery = true)
	Page<Inbound> findByWarehouseIdandStatus(Integer warehouse,Pageable page);

	@Query(value = "select inbound.* from inbound,inbound_storage where inbound.product_id=?1 and inbound.warehouse_id=?2 and inbound_storage.inbound_id=inbound.id and inbound_storage.confirmed=true group by inbound_id order by sum(inbound_storage.available_quantity) desc",nativeQuery = true)
	List<Inbound> getValidInbound(Integer productid,int warehouseid);

}

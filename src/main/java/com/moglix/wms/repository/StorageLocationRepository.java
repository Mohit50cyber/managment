package com.moglix.wms.repository;

import com.moglix.wms.constants.StorageLocationType;
import com.moglix.wms.dto.StorageContent;
import com.moglix.wms.entities.StorageLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author pankaj on 1/5/19
 */
@Repository
public interface StorageLocationRepository extends CrudRepository<StorageLocation, Integer> {
    List<StorageLocation> findByWarehouseId(Integer id);
    Page<StorageLocation> findByWarehouseIdAndZoneIdOrderByModifiedDesc(Integer warehouseId, Integer zoneId, Pageable page);
    Page<StorageLocation> findByWarehouseIdAndZoneIdAndActiveAndTypeOrderByModifiedDesc(Integer warehouseId, Integer zoneId,Boolean active, StorageLocationType type,Pageable page);
    @Override
    List<StorageLocation> findAll();
    
    StorageLocation findByWarehouseIdAndZoneIdAndRackIdAndBinId(Integer warehouseId, Integer zoneId, Integer rackId, Integer binId);
    
    @Query(value = "select round(sum(i.available_quantity + i.allocated_quantity),4) AS quantity, s.id As StorageLocationId, s.bin_id As BinId, s.zone_id As ZoneId, s.warehouse_id As WarehouseId, s.rack_id As RackId, i.product_id as productId, p.product_msn AS productMsn, p.name AS productName , s.name As binName from inbound_storage i join product p join storage_location s where i.storage_location_id = ?1 AND i.product_id = p.id And i.storage_location_id = s.id group by i.product_id having quantity > 0 And round(sum(i.available_quantity + i.allocated_quantity),4) > 0;", nativeQuery = true)
    List<StorageContent>getStorageContents(int storageLocationId);
   
    @Query(value = "select round(sum(i.available_quantity + i.allocated_quantity),4) AS quantity,s.id As StorageLocationId, s.bin_id As BinId, s.zone_id As ZoneId, s.warehouse_id As WarehouseId, s.rack_id As RackId, i.product_id as productId, p.product_msn AS productMsn, p.name AS productName,s.name As binName from inbound_storage i join storage_location s join product p where p.id = i.product_id and s.id = i.storage_location_id And p.product_msn = ?1 And s.warehouse_id = ?2 group by i.product_id, s.id, s.zone_id, s.rack_id, s.bin_id having quantity > 0 And round(sum(i.available_quantity + i.allocated_quantity),4) > 0;", nativeQuery = true)
    List<StorageContent> getStorageContentsWithMsn(String msn, int warehouseId);
   
    @Query(value = "select round(sum(i.available_quantity + i.allocated_quantity),4) AS quantity, s.id As StorageLocationId, s.bin_id As BinId, s.zone_id As ZoneId, s.warehouse_id As WarehouseId, s.rack_id As RackId, i.product_id as productId, p.product_msn AS productMsn, p.name AS productName , s.name As binName from inbound_storage i join product p join storage_location s where i.storage_location_id = ?1 And p.product_msn = ?2 AND i.product_id = p.id And i.storage_location_id = s.id group by i.product_id having quantity > 0 And round(sum(i.available_quantity + i.allocated_quantity),4) > 0;", nativeQuery = true)
    List<StorageContent>getStorageContentsWithMsnAndStorageId(int storageLocationId,String msn);
    
    @Query(value = "select round(sum(i.quantity),4) AS quantity,s.id As StorageLocationId, s.bin_id As BinId, s.zone_id As ZoneId, s.warehouse_id As WarehouseId, s.rack_id As RackId, i.product_id as productId, p.product_msn AS productMsn, p.name AS productName,s.name As binName, cast(i.expiry_date as date) as expiryDate from inbound_storage i left join storage_location s on i.storage_location_id = s.id left join product p on i.product_id = p.id where p.product_msn = ?1 And s.warehouse_id = ?2 and s.id = ?3 group by i.product_id, s.id, cast(i.expiry_date as date) having round(sum(i.quantity),4) > 0", nativeQuery = true)
    List<StorageContent>getStorageContentsWithMsnExpiry(String msn,int warehouseId, int storageLocationId);
    
    @Query(value = "select * from wms.storage_location where name=?1 and warehouse_id=?2 and active=TRUE limit 1 ", nativeQuery = true)
    StorageLocation fetchstoragelocationFromZoneandBin(String bin,int warehouseId);
    
    @Query("SELECT obj FROM StorageLocation obj WHERE obj.zone.name = :zoneName and obj.warehouse.id = :warehouseId and obj.name = :storageLocationName")
    StorageLocation getStorageLocationByWarehouseZoneAndBin(@Param("warehouseId") Integer warehouseId,@Param("zoneName") String zoneName, @Param("storageLocationName") String storageLocationName);
	
    Page<StorageLocation> findByWarehouseIdAndZoneIdAndTypeNotAndActiveOrderByModifiedDesc(Integer warehouseId,
			Integer zoneId, StorageLocationType bad, boolean active, Pageable page);

}

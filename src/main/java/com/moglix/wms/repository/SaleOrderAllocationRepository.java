package com.moglix.wms.repository;

import java.util.List;

import com.moglix.wms.entities.SaleOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.SaleOrderAllocation;

/**
 * @author pankaj on 6/5/19
 */
@Repository
public interface SaleOrderAllocationRepository extends CrudRepository<SaleOrderAllocation, Integer> {
    List<SaleOrderAllocation> getSaleOrderAllocationBySaleOrderId(Integer saleOrderId);
    List<SaleOrderAllocation> findAllByInboundStorage(InboundStorage inboundStorage);
    List<SaleOrderAllocation> findAllByInboundStorageAndSaleOrder(InboundStorage inboundStorage, SaleOrder so);

    List<SaleOrderAllocation> findAllByInboundStorageAndStatus(InboundStorage inboundStorage, SaleOrderAllocationStatus status);
    
    List<SaleOrderAllocation> getSaleOrderAllocationBySaleOrderIdAndStatus(Integer saleOrderId, SaleOrderAllocationStatus status);

    @Query("SELECT obj FROM SaleOrderAllocation obj WHERE obj.saleOrder.warehouse.id = :warehouseId and obj.saleOrder.product.id = :productId and status = 'ALLOCATED' ")
    List<SaleOrderAllocation> getForAllocatedQtyAndProduct(@Param("warehouseId") Integer warehouseId, @Param("productId") Integer productId);
    
    @Query("SELECT obj FROM SaleOrderAllocation obj WHERE obj.saleOrder.warehouse.id = :warehouseId and obj.saleOrder.product.id = :productId and status in ( 'ALLOCATED','TRANSFERRED' ) ")
    List<SaleOrderAllocation> getForAllocatedQtyAndProductTransferred(@Param("warehouseId") Integer warehouseId, @Param("productId") Integer productId);
    
	@Query("SELECT obj FROM SaleOrderAllocation obj WHERE obj.saleOrder.warehouse.id = :warehouseId and obj.saleOrder.product.id = :productId and status in ( 'ALLOCATED','TRANSFERRED' ) and obj.inboundStorage.storageLocation.zone.id = :zoneId")
	List<SaleOrderAllocation> getForAllocatedQtyAndProductAndZoneTransferred(@Param("warehouseId") Integer warehouseId,
			@Param("productId") Integer productId, @Param("zoneId") Integer zoneId);

	@Query("SELECT obj FROM SaleOrderAllocation obj WHERE obj.saleOrder.warehouse.id = :warehouseId and obj.saleOrder.product.id = :productId and status in ( 'ALLOCATED','TRANSFERRED' ) and obj.inboundStorage.storageLocation.zone.id = :zoneId and obj.inboundStorage.storageLocation.bin.id = :binId")
	List<SaleOrderAllocation> getForAllocatedQtyAndProductAndZoneAndBinTransferred(
			@Param("warehouseId") Integer warehouseId, @Param("productId") Integer productId,
			@Param("zoneId") Integer zoneId, @Param("binId") Integer binId);
	List<SaleOrderAllocation> findAllByInboundStorageAndStatusAndAvailableQuantityGreaterThan(InboundStorage storage,
			SaleOrderAllocationStatus allocated, double d);
    
}

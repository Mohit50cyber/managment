package com.moglix.wms.repository;

import com.moglix.wms.constants.SaleOrderStatus;
import com.moglix.wms.entities.SaleOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author pankaj on 6/5/19
 */
@Repository
public interface SaleOrderRepository extends CrudRepository<SaleOrder, Integer> {
    List<SaleOrder> getSaleOrderByEmsOrderId(Integer emsOrderId);
    SaleOrder getSaleOrderByEmsOrderItemId(Integer emsOrderItemId);
    List<SaleOrder> findSaleOrderByEmsOrderItemIdIn(List<Integer> emsOrderItemId);
    List<SaleOrder> findAllByEmsOrderItemIdInAndStatus(Set<Integer> emsOrderItemId, SaleOrderStatus status);
    List<SaleOrder> getSaleOrderByWarehouseId(Integer warehouseId);
    void deleteAllByEmsOrderItemIdIn(List<Integer> emsOrderItemId);
    void deleteByEmsOrderItemId(Integer emsOrderItemId);
    SaleOrder findByItemRef(String itemRef);
    List<SaleOrder> findAllByItemRefIn(List<String> itemRef);
    
    @Query("SELECT obj FROM SaleOrder obj WHERE obj.emsOrderItemId = :emsOrderItemId and obj.emsOrderId = :emsOrderId")
    Optional<SaleOrder> findbyEmsOrderItemIdAndEmsOrderId(Integer emsOrderItemId, Integer emsOrderId);

    @Query("SELECT obj FROM SaleOrder obj WHERE obj.product.id = :productId and obj.status = 'OPEN' and (obj.allocatedQuantity + obj.packedQuantity) < obj.orderedQuantity order by obj.created")
    List<SaleOrder> findOpenSaleOrderForProduct(@Param("productId") Integer productId);
    
    List<SaleOrder> findByStatusIn(List<SaleOrderStatus> saleOrderStatues);
	List<SaleOrder> findByAllocatedQuantityGreaterThan(double d);
}

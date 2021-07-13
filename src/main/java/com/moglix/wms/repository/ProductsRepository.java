package com.moglix.wms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moglix.wms.dto.BadInventory;
import com.moglix.wms.entities.Product;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {

	@Query(value = "select * from product where product_msn = ?1 order by created desc limit 1;", nativeQuery = true)
	Product getUniqueByProductMsn(String productMsn);
	
	Optional<Product> findByProductMsnAndUom(String productMsn, String uom);
	
	@Query(value = "select ist.id, ist.available_quantity as availableQuantity, ist.created, ist.modified, ist.product_id as productId, ist.confirmed, sl.warehouse_id as warehouseId, p.shelf_life as shelfLife, ist.expiry_date as expiryDate from product p left join inbound_storage ist on p.id = ist.product_id left join storage_location sl on ist.storage_location_id = sl.id where p.expiry_date_management_enabled = true and ist.available_quantity > 0 and ist.confirmed = true and sl.active = true and sl.type = \"GOOD\" and current_date() > coalesce(ist.expiry_date, '2070-12-16')", nativeQuery = true)
	List<BadInventory>findExpiredInventory();
	
	@Query(value = "select product_msn from wms.product where id=?1", nativeQuery = true)
	String getProductMsn(Integer productId);
}

package com.moglix.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.ems.entities.EnterpriseMrnItems;
import com.moglix.wms.dto.EMSInventory;

@Repository
public interface EMSInventoryRepository extends CrudRepository<EnterpriseMrnItems, Integer>{

	@Query(value = "SELECT epo.warehouse_id AS warehouseId, emi.mrn_id AS mrnId, epo.id AS poId, emi.po_item_id AS poItemId , emi.created_at AS mrnDate, epoi.product_mpn AS  productMpn, epoi.product_name AS productName, emi.arrived_quantity/1000 AS arrivedQuantity, epoi.brand_name AS brandName, epoi.product_unit AS productUnit, epo.supplier_id AS supplierId , epo.supplier_name AS supplierName, epoi.transfer_tax/1000 AS taxRate, epoi.transfer_price/100 AS transferPrice\n" + 
			"FROM enterprise_mrn em\n" + 
			"LEFT JOIN enterprise_mrn_items emi\n" + 
			"ON em.id = emi.mrn_id\n" + 
			"LEFT JOIN `enterprise_purchase_order_item` epoi\n" + 
			"ON emi.po_item_id = epoi.id\n" + 
			"LEFT JOIN `enterprise_purchase_order` epo\n" + 
			"ON epo.id = epoi.purchase_order_id\n" + 
			"WHERE (epo.warehouse_id=5  AND emi.created_at BETWEEN '2016-01-01' AND '2019-12-15 23:59:59')\n" + 
			"OR (epo.warehouse_id <> 5 AND emi.created_at BETWEEN '2016-01-01' AND '2019-08-15')\n" + 
			"AND em.status = '20'\n" + 
			"ORDER BY mrnDate DESC;", nativeQuery = true)
	public List<EMSInventory> getProductMrnInventory();
	
	
	@Query(value = "SELECT epo.warehouse_id AS warehouseId, emi.mrn_id AS mrnId, epo.id AS poId, emi.po_item_id AS poItemId , emi.created_at AS mrnDate, epoi.product_mpn AS  productMpn, epoi.product_name AS productName, emi.arrived_quantity/1000 AS arrivedQuantity, epoi.brand_name AS brandName, epoi.product_unit AS productUnit, epo.supplier_id AS supplierId , epo.supplier_name AS supplierName, epoi.transfer_tax/1000 AS taxRate, epoi.transfer_price/100 AS transferPrice\n" + 
			"FROM enterprise_mrn em\n" + 
			"LEFT JOIN enterprise_mrn_items emi\n" + 
			"ON em.id = emi.mrn_id\n" + 
			"LEFT JOIN `enterprise_purchase_order_item` epoi\n" + 
			"ON emi.po_item_id = epoi.id\n" + 
			"LEFT JOIN `enterprise_purchase_order` epo\n" + 
			"ON epo.id = epoi.purchase_order_id\n" + 
			"WHERE emi.created_at BETWEEN '2016-01-01' AND  NOW()\n" + 
			"AND em.status = '20'\n" + 
			"ORDER BY mrnDate DESC;", nativeQuery = true)
	public List<EMSInventory> getProductMrnInventoryCSV();

}

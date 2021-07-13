package com.moglix.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.ems.entities.EnterpriseReturnItems;
import com.moglix.wms.dto.EMSReturnInventory;

@Repository
public interface EMSReturnRepository extends CrudRepository<EnterpriseReturnItems, Integer> {	
	@Query(value = "SELECT distinct eri.return_id AS returnId, eri.product_name AS productName,\n" + 
			"er.packet_id AS packetId, eri.quantity/1000 AS quantity,\n" + 
			"er.invoice_number AS invoiceNumber, er.credit_note_no AS creditNoteNo\n" + 
			",epi.product_mpn AS productMpn, epi.warehouse_id AS warehouseId\n" + 
			"FROM enterprise_return_items eri\n" + 
			"LEFT JOIN \n" + 
			"enterprise_returns er\n" + 
			"ON er.id = eri.return_id\n" + 
			"JOIN enterprise_packet_item epi\n" + 
			"ON eri.item_id = epi.item_id\n" + 
			"WHERE eri.created_at BETWEEN '2019-07-01' AND '2019-07-23'\n" + 
			"AND eri.item_status = 110;", nativeQuery = true)
	public List<EMSReturnInventory> getProductReturnInventory();
}

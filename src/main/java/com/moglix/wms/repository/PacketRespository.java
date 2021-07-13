package com.moglix.wms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.dto.LotInfo;
import com.moglix.wms.dto.MSNListDTO;
import com.moglix.wms.dto.ReturnDetailDTO;
import com.moglix.wms.entities.Packet;

@Repository
public interface PacketRespository extends CrudRepository<Packet, Integer> {
	Page<Packet> findByWarehouseIdAndStatusOrderByIdAsc(Integer warehouseId, PacketStatus status, Pageable page);

	Page<Packet> findByWarehouseIdOrderByIdAsc(Integer warehouseId, Pageable page);

	Page<Packet> findByWarehouseIdAndStatusNotOrderByIdAsc(Integer warehouseId, PacketStatus status, Pageable page);

	Page<Packet> findByWarehouseIdAndInvoiceNumberAndStatusNotOrderByIdAsc(Integer warehouseId, String invoiceNumber,
			PacketStatus status, Pageable page);

	Optional<Packet> findByEmsPacketIdAndStatusNot(Integer emsPacketId, PacketStatus status);

	Optional<Packet> findByEmsPacketIdAndStatus(Integer emsPacketId, PacketStatus status);

	Optional<Packet> findByEmsPacketId(Integer emsPacketId);

	List<Packet> findByIdIn(List<Integer> id);

	Page<Packet> findByWarehouseIdAndInvoiceNumberAndStatusOrderByIdAsc(Integer warehouseId, String invoiceNumber,
			PacketStatus status, Pageable page);

	Optional<Packet> findByInvoiceNumberAndStatusNot(String invoiceNumber, PacketStatus status);

	@Query(value = "select inb.id as inboundId, b.ems_return_id as emsReturnId, inb.inventorisable_quantity as returnedQuantity, inb.customer_dedit_done_quantity as debitDoneQuantity, b.supplier_id as supplierId, b.supplier_name as supplierName, b.warehouse_id as warehouseId, b.warehouse_name as warehouseName, inb.purchase_price as purchasePrice, inb.supplier_po_id as supplierPoId, inb.supplier_po_item_id supplierPoItemId, inb.tax as tax, pt.product_msn as productMsn, pt.name as productName , pt.uom as uom from packet p left join packet_return pr on p.id = pr.packet_id left join batch b on pr.ems_return_id = b.ems_return_id left join inbound inb on b.id = inb.batch_id left join product pt on inb.product_id = pt.id  where inb.inventorisable_quantity > 0 and inb.type = \"CUSTOMER_RETURN\" and inb.inventorize = false and inb.status != \"STARTED\" and pr.status != \"CANCELLED\" and p.invoice_number = ?1", nativeQuery = true)
	List<ReturnDetailDTO> getReturnPacketDetails(String invoiceNumber);

	List<Packet> findByInvoiceNumberIn(List<String> invoiceNumbers);

	@Query(value = "select p.invoice_number as invoiceNumber, sum(pi.quantity) as quantity, ist.lot_number as lotNumber, pt.product_msn as productMsn, pt.uom as uom, pt.name as description from packet p left join packet_item pi on p.id = pi.packet_id left join inbound_storage ist on pi.inbound_storage_id = ist.id left join product pt on ist.product_id = pt.id where invoice_number in ?1 and (ist.lot_number is not null and ist.lot_number != '') group by p.invoice_number, pt.product_msn, ist.lot_number", nativeQuery = true)
	List<LotInfo> findLotInformatonByInvoiceNumbers(List<String> invoiceNumbers);
	
	Optional<Packet> findById(Integer id);
	
	@Query(value = "select pi.packet_id as packetId,so.product_id as productId,p.product_msn as productMSN,COALESCE(sum(pi.quantity),0) as quantity from wms.packet_item as pi,wms.sale_order as so,wms.product as p where packet_id=?1 and pi.sale_order_id=so.id and so.product_id=p.id group by product_msn ", nativeQuery = true)
	List<MSNListDTO> findMSNListusingPacketId(Integer packetId);
	
	//Apps
	@Query(value = "select count(id) from wms.packet where warehouse_id=?1 and status='INVOICED'", nativeQuery = true)
	Integer countdata(Integer warehouseId);
	
	
}

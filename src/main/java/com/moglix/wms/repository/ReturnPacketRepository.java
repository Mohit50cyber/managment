package com.moglix.wms.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.entities.ReturnPacket;

@Repository
public interface ReturnPacketRepository extends CrudRepository<ReturnPacket, Integer> {
	Page<ReturnPacket>findByWarehouseIdAndEmsPacketIdAndStatusOrderByCreated(Integer warehouseId, Integer emsPacketId, Pageable page, PacketStatus status);
	Page<ReturnPacket>findByWarehouseIdAndInvoiceNumberAndStatusOrderByCreated(Integer warehouseId, String invoiceNumber, Pageable page, PacketStatus status);
	Page<ReturnPacket>findByInvoiceNumberAndStatusOrderByCreated(String invoiceNumber, Pageable page, PacketStatus status);
	Page<ReturnPacket>findByEmsPacketIdAndStatusOrderByCreated(Integer emsPacketId, Pageable page, PacketStatus status);
	Page<ReturnPacket>findByWarehouseIdAndStatusOrderByCreated(Integer warehouseId, Pageable page, PacketStatus status);
	Page<ReturnPacket> findByStatusOrderByCreated(Pageable page, PacketStatus status);
	List<ReturnPacket>findByEmsPacketIdAndStatusOrderByCreated(Integer emsPacketId, PacketStatus status);
	@Query(value = "select returnPacket from ReturnPacket returnPacket inner join returnPacket.warehouse wr where wr.id = :warehouseId and returnPacket.emsPacketId = :searchKey or returnPacket.invoiceNumber = :searchKey")
	Page<ReturnPacket>findByWarehouseIdAndSearchKeyOrderByCreated(@Nullable @Param("warehouseId") Integer warehouseId, @Nullable @Param("searchKey") String searchKey, Pageable page);	
	Optional<ReturnPacket> findByEmsReturnId(Integer emsReturnId);
	ReturnPacket findByEmsReturnIdAndStatusOrderByCreated(Integer emsReturnId, PacketStatus returned);
	Page<ReturnPacket> findByWarehouseIdAndCreatedBetweenAndStatusOrderByCreated(Integer warehouseId, Date start,
			Date end, Pageable page, PacketStatus status);
	Page<ReturnPacket> findByCreatedBetweenAndStatusOrderByCreated(Date start, Date end, Pageable page,
			PacketStatus status);
}

package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.constants.BulkInvoiceStatus;
import com.moglix.wms.entities.BulkInvoicingSaleOrder;

@Repository
public interface BulkInvoicingSaleOrderRepository extends CrudRepository<BulkInvoicingSaleOrder, Integer> {
	BulkInvoicingSaleOrder findByItemRefAndStatus(String itemRef, BulkInvoiceStatus status);
	BulkInvoicingSaleOrder findByItemRefAndStatusNot(String itemRef, BulkInvoiceStatus status);

}

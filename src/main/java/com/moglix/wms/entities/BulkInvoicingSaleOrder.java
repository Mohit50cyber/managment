package com.moglix.wms.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.moglix.wms.constants.BulkInvoiceStatus;

@Entity
@Table(name = "bulk_invoicing_sale_order")
public class BulkInvoicingSaleOrder {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "bulk_invoice_id")
    private String bulkInvoiceId;
    
    @Column(name = "buyers_order_id")
    private String buyerOrderId;

    @Column(name = "item_ref", unique = true)
    private String itemRef;
	
    @Column(name = "ordered_quantity")
    private Double orderedQuantity;
    
    @Column (name = "warehouse_id")
    private Integer warehouseId;
	
	@Column (name = "product_msn")
    private String productMsn;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "status")
	private BulkInvoiceStatus status = BulkInvoiceStatus.CREATED;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();
	
	@Column (name = "unique_block_transaction_id")
    private String uniqueblockid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBulkInvoiceId() {
		return bulkInvoiceId;
	}

	public void setBulkInvoiceId(String bulkInvoiceId) {
		this.bulkInvoiceId = bulkInvoiceId;
	}

	public String getBuyerOrderId() {
		return buyerOrderId;
	}

	public void setBuyerOrderId(String buyerOrderId) {
		this.buyerOrderId = buyerOrderId;
	}

	public String getItemRef() {
		return itemRef;
	}

	public void setItemRef(String itemRef) {
		this.itemRef = itemRef;
	}

	public Double getOrderedQuantity() {
		return orderedQuantity;
	}

	public void setOrderedQuantity(Double orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public BulkInvoiceStatus getStatus() {
		return status;
	}

	public void setStatus(BulkInvoiceStatus status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public String getUniqueblockid() {
		return uniqueblockid;
	}

	public void setUniqueblockid(String uniqueblockid) {
		this.uniqueblockid = uniqueblockid;
	}
}

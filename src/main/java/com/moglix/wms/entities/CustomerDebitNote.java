package com.moglix.wms.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moglix.wms.constants.CustomerDebitNoteStatus;
import com.moglix.wms.constants.CustomerDebitNoteType;

@Entity
@Table(name = "customer_debit_note", uniqueConstraints=@UniqueConstraint(columnNames={"debit_note_number"}))
public class CustomerDebitNote {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "debit_note_number", unique = true)
    private String debitNoteNumber;

    @Column(name="total_quantity")
    private Double totalQuantity = 0d;

    @Column(name = "warehouse_id")
	private Integer warehouseId;
	
	@Column(name = "warehouse_name")
	private String warehouseName;
	
    @Enumerated(EnumType.STRING)
	@Column(name = "type")
	private CustomerDebitNoteType type;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CustomerDebitNoteStatus status = CustomerDebitNoteStatus.CREATED;
    
    @Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customerDebitNote", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<CustomerDebitNoteItem> debitNoteItems;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDebitNoteNumber() {
		return debitNoteNumber;
	}

	public void setDebitNoteNumber(String debitNoteNumber) {
		this.debitNoteNumber = debitNoteNumber;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public CustomerDebitNoteStatus getStatus() {
		return status;
	}

	public void setStatus(CustomerDebitNoteStatus status) {
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

	public Set<CustomerDebitNoteItem> getDebitNoteItems() {
		return debitNoteItems;
	}

	public void setDebitNoteItems(Set<CustomerDebitNoteItem> debitNoteItems) {
		this.debitNoteItems = debitNoteItems;
	}

	public CustomerDebitNoteType getType() {
		return type;
	}

	public void setType(CustomerDebitNoteType type) {
		this.type = type;
	}
}

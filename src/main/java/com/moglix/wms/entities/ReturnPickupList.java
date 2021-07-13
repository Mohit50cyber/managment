package com.moglix.wms.entities;

import java.io.Serializable;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moglix.wms.constants.ReturnPickupListStatus;

@Entity
@Table(name = "return_pickup_list")
public class ReturnPickupList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4107784276148615601L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "credit_note_number")
	private String creditNoteNumber;


	@Column(name = "supplier_id")
	private Integer supplierId;
	
	@Column(name = "total_quantity")
	private Double totalQuantity;
	
	@Column(name = "supplier_po_id")
	private Integer supplierPoId;
	
	@Column(name = "supplier_name")
	private String supplierName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private ReturnPickupListStatus status;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "returnPickupList", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<ReturnPickupListItem> returnPickupListItems;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "return_packet_id", nullable = true)
	private ReturnPacket returnPacket;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modified = new Date();
    
    
    @NotNull
    @Column(name = "ems_return_note_id")
    private String emsReturnNoteId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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

	public Set<ReturnPickupListItem> getReturnPickupListItems() {
		return returnPickupListItems;
	}

	public void setReturnPickupListItems(Set<ReturnPickupListItem> returnPickupListItems) {
		this.returnPickupListItems = returnPickupListItems;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public ReturnPacket getReturnPacket() {
		return returnPacket;
	}

	public void setReturnPacket(ReturnPacket returnPacket) {
		this.returnPacket = returnPacket;
	}

	public String getCreditNoteNumber() {
		return creditNoteNumber;
	}

	public void setCreditNoteNumber(String creditNoteNumber) {
		this.creditNoteNumber = creditNoteNumber;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public ReturnPickupListStatus getStatus() {
		return status;
	}

	public void setStatus(ReturnPickupListStatus status) {
		this.status = status;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}
	
	public String getEmsReturnNoteId() {
		return emsReturnNoteId;
	}

	public void setEmsReturnNoteId(String emsReturnNoteId) {
		this.emsReturnNoteId = emsReturnNoteId;
	}
	

}

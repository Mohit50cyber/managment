package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name = "putaway_list")
public class PutawayList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3996845298370998015L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "pdf_url")
	private String pdfUrl;
	
	
	@Column(name = "quantity")
	private Double quantity;
	
	@Column(name = "supplier_name")
	private String supplierName;
	
	@Column(name = "supplier_id")
	private Integer supplierId;
	
	@Column(name = "warehouse_id")
	private Integer warehouseId;
	
	@Column(name = "generated_by")
	private String generatedBy;
	
	@Column(name = "assigned_to")
	private String assignedTo;
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inbound", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<InboundStorage> inboundStorages;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "putawayList", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<PutawayListInboundStorageMapping> putawayListInboundStorageMapping;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
    private Date created = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modified = new Date();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
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

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public Set<InboundStorage> getInboundStorages() {
		return inboundStorages;
	}

	public void setInboundStorages(Set<InboundStorage> inboundStorages) {
		this.inboundStorages = inboundStorages;
	}

	public Set<PutawayListInboundStorageMapping> getPutawayListInboundStorageMapping() {
		return putawayListInboundStorageMapping;
	}

	public void setPutawayListInboundStorageMapping(
			Set<PutawayListInboundStorageMapping> putawayListInboundStorageMapping) {
		this.putawayListInboundStorageMapping = putawayListInboundStorageMapping;
	}
}
